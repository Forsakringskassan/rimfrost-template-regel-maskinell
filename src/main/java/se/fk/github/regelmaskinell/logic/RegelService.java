package se.fk.github.regelmaskinell.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggningUpdate;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableProduceratResultat;
import se.fk.rimfrost.framework.handlaggning.model.Yrkandestatus;
import se.fk.rimfrost.framework.regel.Utfall;
import se.fk.rimfrost.framework.regel.logic.RegelUtils;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUnderlag;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgift;
import se.fk.rimfrost.framework.handlaggning.model.Underlag;
import se.fk.rimfrost.framework.handlaggning.model.UppgiftStatus;
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
      //All information that the rule uses to make a decision should be sent as RegelMaskinellResult.HandlaggningUpdate.Underlag (RegelUtils.createUnderlag() can be used to create Underlag objects).
      //ProduceratResultat should be sent in the RegelMaskinellResult.HandlaggningUpdate.Yrkande.ProduceradeResultat if a new result has been added or an existing has been updated.

      var uppgift = ImmutableUppgift.builder()
            .from(regelRequest.uppgift())
            .utfordTs(OffsetDateTime.now())
            .uppgiftStatus(UppgiftStatus.AVSLUTAD)
            .build();

      var handlaggningUpdate = ImmutableHandlaggningUpdate.builder()
            .id(regelRequest.handlaggning().id())
            .version(regelRequest.handlaggning().version())
            .yrkande(regelRequest.handlaggning().yrkande())
            .processInstansId(regelRequest.handlaggning().processInstansId())
            .skapadTS(regelRequest.handlaggning().skapadTS())
            .avslutadTS(regelRequest.handlaggning().avslutadTS())
            .handlaggningspecifikationId(regelRequest.handlaggning().handlaggningspecifikationId())
            .uppgift(uppgift)
            .build();

      return ImmutableRegelMaskinellResult.builder()
            .handlaggningUpdate(handlaggningUpdate)
            .utfall(Utfall.JA)
            .build();
   }
}
