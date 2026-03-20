package se.fk.github.regelmaskinell.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.fk.github.regelmaskinell.logic.entity.RattenTillPeriod;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableProduceratResultat;
import se.fk.rimfrost.framework.handlaggning.model.Yrkandestatus;
import se.fk.rimfrost.framework.regel.Utfall;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUnderlag;
import se.fk.rimfrost.framework.handlaggning.model.Underlag;
import se.fk.rimfrost.framework.regel.maskinell.logic.RegelMaskinellServiceInterface;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellResult;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellRequest;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellResult;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@ApplicationScoped
public class RegelService implements RegelMaskinellServiceInterface
{

   private static final Logger LOGGER = LoggerFactory.getLogger(RegelService.class);

   @Inject
   ObjectMapper objectMapper;

   @Override
   public RegelMaskinellResult processRegel(RegelMaskinellRequest regelRequest)
   {

      //TODO Implement the rule and return the result.
      //All information that the rule uses to make a decision should be sent in the result as Underlag.
      //ProduceratResultat should be sent in the result if a new result has been added or an existing has been updated.

      var underlag = regelRequest.yrkande().produceradeResultat().stream().filter(pr -> pr.typ().equalsIgnoreCase("ersattning"))
            .map(e -> createUnderlag("Ersättning", 1, e)).toList();

      var produceratResultat = ImmutableProduceratResultat.builder()
            .id(UUID.randomUUID())
            .version(1)
            .yrkandeStatus(Yrkandestatus.YRKAT)
            .resultatFrom(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC))
            .resultatTom(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC))
            .typ("RATTENTILLPERIOD")
            .data(createProduceratResultatData(new RattenTillPeriod(100)))
            .build();

      return ImmutableRegelMaskinellResult.builder()
            .utfall(Utfall.JA)
            .addAllUnderlag(underlag)
            .addProduceradeResultat(produceratResultat)
            .build();
   }

   private Underlag createUnderlag(String typ, int version, Object object)
   {
      try
      {
         return ImmutableUnderlag.builder()
               .typ(typ)
               .version(version)
               .data(objectMapper.writeValueAsString(object))
               .build();
      }
      catch (JsonProcessingException e)
      {
         throw new InternalError("Could not parse object to String", e);
      }
   }

   private String createProduceratResultatData(Object object)
   {
      try
      {
         return objectMapper.writeValueAsString(object);
      }
      catch (JsonProcessingException e)
      {
         throw new InternalError("Could not parse object to String", e);
      }
   }
}
