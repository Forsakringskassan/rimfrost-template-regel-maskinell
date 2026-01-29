package se.fk.github.regelmaskinell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import se.fk.rimfrost.regel.common.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation")
@QuarkusTest
@Testcontainers
public class RegelMaskinellContainerSmokeIT
{

   private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
         .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
   private static KafkaContainer kafka;
   private static int kafkaPort;
   private static GenericContainer<?> regelMaskinell;
   private static final String kafkaImage = TestConfig.get("kafka.image");
   private static final String regelMaskinellImage = TestConfig.get("regel.maskinell.image");
   private static final String regelMaskinellRequestsTopic = TestConfig.get("regel.maskinell.requests.topic");
   private static final String regelMaskinellResponsesTopic = TestConfig.get("regel.maskinell.responses.topic");
   private static final int topicTimeout = TestConfig.getInt("topic.timeout");
   private static final String networkAlias = TestConfig.get("network.alias");
   private static final String smallryeKafkaBootstrapServers = networkAlias + ":9092";
   private static Network network = Network.newNetwork();
   private static KafkaConsumer regelMaskinellResponsesConsumer;

   @BeforeAll
   static void setup()
   {
      setupKafka();
      setupRegelMaskinell();
      regelMaskinellResponsesConsumer = createKafkaConsumer(regelMaskinellResponsesTopic);
   }

   static void setupKafka()
   {
      kafka = new KafkaContainer(DockerImageName.parse(kafkaImage)
            .asCompatibleSubstituteFor("apache/kafka"))
            .withNetwork(network)
            .withNetworkAliases(networkAlias);
      kafka.start();
      System.out.println("Kafka host bootstrap servers: " + kafka.getBootstrapServers());
      try
      {
         createTopic(regelMaskinellRequestsTopic, 1, (short) 1);
         createTopic(regelMaskinellResponsesTopic, 1, (short) 1);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to create Kafka topics", e);
      }
   }

   static KafkaConsumer<String, String> createKafkaConsumer(String topic)
   {
      String bootstrap = kafka.getBootstrapServers().replace("PLAINTEXT://", "");
      Properties props = new Properties();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + System.currentTimeMillis());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
      consumer.subscribe(Collections.singletonList(topic));
      return consumer;
   }

   static void setupRegelMaskinell()
   {
      Properties props = new Properties();
      try (InputStream in = RegelMaskinellContainerSmokeIT.class.getResourceAsStream("/test.properties"))
      {
         if (in == null)
         {
            throw new RuntimeException("Could not find /test.properties in classpath");
         }
         props.load(in);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Failed to load test.properties", e);
      }

      //noinspection resource
      regelMaskinell = new GenericContainer<>(DockerImageName.parse(regelMaskinellImage))
            .withNetwork(network)
            .withStartupTimeout(Duration.ofMinutes(2))
            .withEnv("MP_MESSAGING_CONNECTOR_SMALLRYE_KAFKA_BOOTSTRAP_SERVERS", smallryeKafkaBootstrapServers)
            .withEnv("QUARKUS_PROFILE", "test"); // force test profile
      regelMaskinell.start();
   }

   static void createTopic(String topicName, int numPartitions, short replicationFactor) throws Exception
   {
      String bootstrap = kafka.getBootstrapServers().replace("PLAINTEXT://", "");
      Properties props = new Properties();
      props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);

      try (AdminClient admin = AdminClient.create(props))
      {
         NewTopic topic = new NewTopic(topicName, numPartitions, replicationFactor);
         admin.createTopics(List.of(topic)).all().get();
         System.out.printf("Created topic: %S%n", topicName);
      }
   }

   @AfterAll
   static void tearDown()
   {
      if (regelMaskinell != null)
         regelMaskinell.stop();
      if (kafka != null)
         kafka.stop();
   }

   private String readKafkaMessage(KafkaConsumer<String, String> consumer)
   {
      //ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(120));
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(360));
      if (records.isEmpty())
      {
         throw new IllegalStateException("No Kafka message received on topic ");
      }
      // return the first new record
      return records.iterator().next().value();
   }

   private void sendRegelMaskinellRequest(String kundbehovsflodeId) throws Exception
   {
      RegelRequestMessagePayload payload = new RegelRequestMessagePayload();
      RegelRequestMessagePayloadData data = new RegelRequestMessagePayloadData();
      data.setKundbehovsflodeId(kundbehovsflodeId);
      payload.setSpecversion(SpecVersion.NUMBER_1_DOT_0);
      payload.setId("99994567-89ab-4cde-9012-3456789abcde");
      payload.setSource("TestSource-001");
      payload.setType(regelMaskinellRequestsTopic);
      payload.setKogitoprocid("234567");
      payload.setKogitorootprocid("123456");
      payload.setKogitorootprociid("77774567-89ab-4cde-9012-3456789abcde");
      payload.setKogitoparentprociid("88884567-89ab-4cde-9012-3456789abcde");
      payload.setKogitoprocinstanceid("66664567-89ab-4cde-9012-3456789abcde");
      payload.setKogitoprocist("345678");
      payload.setKogitoprocversion("111");
      payload.setKogitoproctype(KogitoProcType.BPMN);
      payload.setKogitoprocrefid("56789");
      payload.setData(data);
      // Serialize entire payload to JSON
      String eventJson = mapper.writeValueAsString(payload);

      Properties props = new Properties();
      props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

      try (KafkaProducer<String, String> producer = new KafkaProducer<>(props))
      {
         ProducerRecord<String, String> record = new ProducerRecord<>(
               regelMaskinellRequestsTopic,
               eventJson);
         System.out.printf("Kafka sending to topic : %s, json: %s%n", regelMaskinellRequestsTopic, eventJson);
         producer.send(record).get();
      }
   }

   @ParameterizedTest
   @CsvSource(
   {
         "5367f6b8-cc4a-11f0-8de9-199901011234, 19990101-1234, Ja",
         "5367f6b8-cc4a-11f0-8de9-199901013333, 19990101-3333, Ja",
         "5367f6b8-cc4a-11f0-8de9-199901012222, 19990101-2222, Ja",
         "5367f6b8-cc4a-11f0-8de9-199901014444, 19990101-4444, Ja"
   })
   void TestRegelMaskinellSmoke(String kundbehovsflodeId,
         String persnr,
         String expectedUtfall) throws Exception
   {

      System.out.printf("Starting TestRegelMaskinellSmoke. %S%n", kundbehovsflodeId);
      // Send regel maskinell request to start workflow
      sendRegelMaskinellRequest(kundbehovsflodeId);

      String regelMaskinellResponseJson = readKafkaMessage(regelMaskinellResponsesConsumer);
      var regelMaskinellResponse = mapper.readValue(regelMaskinellResponseJson, RegelResponseMessagePayload.class);
      assertEquals(kundbehovsflodeId, regelMaskinellResponse.getData().getKundbehovsflodeId());
      assertEquals(expectedUtfall, regelMaskinellResponse.getData().getUtfall().getValue());

   }
}
