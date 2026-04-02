package se.fk.github.regelmaskinell;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import se.fk.github.regelmaskinell.logic.RegelService;
import se.fk.rimfrost.framework.handlaggning.model.FSSAinformation;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggning;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIndividYrkandeRoll;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableProduceratResultat;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgift;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgiftSpecifikation;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableYrkande;
import se.fk.rimfrost.framework.handlaggning.model.UppgiftStatus;
import se.fk.rimfrost.framework.handlaggning.model.Yrkandestatus;
import se.fk.rimfrost.framework.regel.*;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class RegelMaskinellTest
{

   @Inject
   RegelService regelService;

   @Test
   public void TestRegelMaskinell()
   {
      var individYrkandeRoll = ImmutableIndividYrkandeRoll.builder()
            .individId(UUID.randomUUID())
            .yrkandeRollId(UUID.randomUUID())
            .build();

      var produceradeResultat = ImmutableProduceratResultat.builder()
            .id(UUID.randomUUID())
            .version(1)
            .resultatFrom(OffsetDateTime.now(ZoneOffset.UTC))
            .resultatTom(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeStatus(Yrkandestatus.YRKAT)
            .typ("ERSATTNING")
            .data("{}")
            .build();

      var yrkande = ImmutableYrkande.builder()
            .id(UUID.randomUUID())
            .version(1)
            .erbjudandeId(UUID.randomUUID())
            .yrkandeDatum(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeStatus(Yrkandestatus.YRKAT)
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

      var uppgift = ImmutableUppgift.builder()
            .id(UUID.randomUUID())
            .version(1)
            .skapadTs(OffsetDateTime.now())
            .utforarId(UUID.randomUUID())
            .uppgiftStatus(UppgiftStatus.TILLDELAD)
            .aktivitetId(UUID.randomUUID())
            .fSSAinformation(FSSAinformation.HANDLAGGNING_PAGAR)
            .uppgiftSpecifikation(uppgiftSpecifikation)
            .build();

      var request = ImmutableRegelMaskinellRequest.builder()
            .handlaggning(handlaggning)
            .uppgift(uppgift)
            .build();

      var result = regelService.processRegel(request);

      assertEquals(Utfall.JA, result.utfall());
   }
}
