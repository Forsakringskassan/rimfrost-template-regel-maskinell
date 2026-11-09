package se.fk.github.regeltemplate;

import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggning;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableIdtyp;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableRollIYrkande;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableSakfragaStallningstagandeContainer;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgift;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgiftSpecifikation;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableYrkande;
import se.fk.rimfrost.framework.regel.maskinell.base.RegelMaskinellTestData;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellRequest;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class RegelTemplateTestData extends RegelMaskinellTestData
{
   public static RegelMaskinellRequest newRegelMaskinellRequest()
   {
      // TODO: Adjust test data values as needed

      var yrkandeId = UUID.randomUUID();

      var individ = ImmutableIdtyp.builder()
            .typId(UUID.randomUUID().toString())
            .varde(UUID.randomUUID().toString())
            .build();

      var individYrkandeRoll = ImmutableRollIYrkande.builder()
            .id(UUID.randomUUID())
            .individ(individ)
            .yrkandeRollId(UUID.randomUUID().toString())
            .avserYrkande(yrkandeId)
            .build();

      var produceradeResultat = ImmutableSakfragaStallningstagandeContainer.builder()
            .id(UUID.randomUUID())
            .objektTypId("ERSATTNING")
            .data("{}")
            .build();

      var yrkande = ImmutableYrkande.builder()
            .id(yrkandeId)
            .version(1)
            .ingangtypId(UUID.randomUUID().toString())
            .yrkandeDatum(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeStatus(UUID.randomUUID().toString())
            .yrkandeFrom(OffsetDateTime.now(ZoneOffset.UTC))
            .yrkandeTom(OffsetDateTime.now(ZoneOffset.UTC))
            .avsikt("NY")
            .rollerIYrkande(List.of(individYrkandeRoll))
            .sakfragorStallningstaganden(List.of(produceradeResultat))
            .beslut(List.of())
            .build();

      var handlaggning = ImmutableHandlaggning.builder()
            .id(UUID.randomUUID())
            .version(1)
            .yrkande(yrkande)
            .handlaggningIdTyp(UUID.randomUUID().toString())
            .handlaggningIdVarde(UUID.randomUUID().toString())
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
            .skapadTS(OffsetDateTime.now())
            .utforare(utforarId)
            .uppgiftStatus(UUID.randomUUID().toString())
            .aktivitetId(UUID.randomUUID())
            .fSSAinformation(UUID.randomUUID().toString())
            .uppgiftSpecifikation(uppgiftSpecifikation)
            .build();

      return ImmutableRegelMaskinellRequest.builder()
            .handlaggning(handlaggning)
            .uppgift(uppgift)
            .processInstansId(UUID.randomUUID())
            .build();
   }
}
