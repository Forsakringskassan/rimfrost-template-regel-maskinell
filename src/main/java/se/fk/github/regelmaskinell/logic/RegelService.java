package se.fk.github.regelmaskinell.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggningUpdate;
import se.fk.rimfrost.framework.regel.Utfall;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgift;
import se.fk.rimfrost.framework.regel.maskinell.logic.RegelMaskinellServiceInterface;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellResult;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellRequest;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellResult;
import se.fk.rimfrost.framework.uppgiftstatusprovider.UppgiftStatusProvider;

@ApplicationScoped
public class RegelService implements RegelMaskinellServiceInterface
{

   private static final Logger LOGGER = LoggerFactory.getLogger(RegelService.class);

   @Inject
   ObjectMapper objectMapper;

   @Inject
   UppgiftStatusProvider uppgiftStatusProvider;

   @Override
   public RegelMaskinellResult processRegel(RegelMaskinellRequest regelRequest)
   {
      //TODO Implement the rule and return the result.
      //All information that the rule uses to make a decision should be sent as RegelMaskinellResult.HandlaggningUpdate.Underlag (RegelUtils.createUnderlag() can be used to create Underlag objects).
      //ProduceratResultat should be sent in the RegelMaskinellResult.HandlaggningUpdate.Yrkande.ProduceradeResultat if a new result has been added or an existing has been updated.

      var uppgift = ImmutableUppgift.builder()
            .from(regelRequest.uppgift())
            .utfordTs(OffsetDateTime.now())
            .uppgiftStatus(uppgiftStatusProvider.getAvslutadId())
            .build();

      var handlaggningUpdate = ImmutableHandlaggningUpdate.builder()
            .id(regelRequest.handlaggning().id())
            .version(regelRequest.handlaggning().version())
            .yrkande(regelRequest.handlaggning().yrkande())
            .processInstansId(regelRequest.processInstansId())
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
