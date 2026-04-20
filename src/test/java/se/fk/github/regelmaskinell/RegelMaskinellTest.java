package se.fk.github.regelmaskinell;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import se.fk.github.regelmaskinell.logic.RegelService;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggning;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIdtyp;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIndividYrkandeRoll;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableProduceratResultat;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgift;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgiftSpecifikation;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableYrkande;
import se.fk.rimfrost.framework.regel.*;
import se.fk.rimfrost.framework.regel.maskinell.RegelMaskinellTestBase;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class RegelMaskinellTest extends RegelMaskinellTestBase
{

   @Inject
   RegelService regelService;

   @Test
   public void TestRegelMaskinell()
   {
      var individ = ImmutableIdtyp.builder()
            .typId(UUID.randomUUID().toString())
            .varde(UUID.randomUUID().toString())
            .build();

      var individYrkandeRoll = ImmutableIndividYrkandeRoll.builder()
            .individ(individ)
            .yrkandeRollId(UUID.randomUUID().toString())
            .build();

      var produceradeResultat = ImmutableProduceratResultat.builder()
            .id(UUID.randomUUID())
            .version(1)
            .resultatFrom(OffsetDateTime.now(ZoneOffset.UTC))
            .resultatTom(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeStatus(UUID.randomUUID().toString())
            .typ("ERSATTNING")
            .data("{}")
            .build();

      var yrkande = ImmutableYrkande.builder()
            .id(UUID.randomUUID())
            .version(1)
            .erbjudandeId(UUID.randomUUID().toString())
            .yrkandeDatum(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeStatus(UUID.randomUUID().toString())
            .yrkandeFrom(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeTom(OffsetDateTime.now(ZoneOffset.UTC))
            .avsikt("NY")
            .individYrkandeRoller(List.of(individYrkandeRoll))
            .produceradeResultat(List.of(produceradeResultat))
            .build();

      var handlaggning = ImmutableHandlaggning.builder()
            .id(UUID.randomUUID())
            .version(1)
            .yrkande(yrkande)
            .processInstansId(UUID.randomUUID())
            .skapadTS(OffsetDateTime.now())
            .handlaggningspecifikationId(UUID.randomUUID())
            .build();

      var uppgiftSpecifikation = ImmutableUppgiftSpecifikation.builder()
            .id(UUID.randomUUID())
            .version(1)
            .build();

      var utforarId = ImmutableIdtyp.builder()
            .typId(UUID.randomUUID().toString())
            .varde(UUID.randomUUID().toString())
            .build();

      var uppgift = ImmutableUppgift.builder()
            .id(UUID.randomUUID())
            .version(1)
            .skapadTs(OffsetDateTime.now())
            .utforarId(utforarId)
            .uppgiftStatus(UUID.randomUUID().toString())
            .aktivitetId(UUID.randomUUID())
            .fSSAinformation(UUID.randomUUID().toString())
            .uppgiftSpecifikation(uppgiftSpecifikation)
            .build();

      var request = ImmutableRegelMaskinellRequest.builder()
            .handlaggning(handlaggning)
            .uppgift(uppgift)
            .processInstansId(UUID.randomUUID())
            .build();

      var result = regelService.processRegel(request);

      assertEquals(Utfall.JA, result.utfall());
   }
}
