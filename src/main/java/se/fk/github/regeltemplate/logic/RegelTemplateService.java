package se.fk.github.regeltemplate.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableHandlaggningUpdate;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUppgift;
import se.fk.rimfrost.framework.regel.Utfall;
import se.fk.rimfrost.framework.regel.logic.RegelUtils;
import se.fk.rimfrost.framework.regel.maskinell.logic.RegelMaskinellServiceInterface;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellSuccessResult;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellRequest;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellResult;

@ApplicationScoped
public class RegelTemplateService implements RegelMaskinellServiceInterface
{

   @SuppressWarnings("unused")
   @Inject
   ObjectMapper objectMapper;

   @Override
   public RegelMaskinellResult processRegel(RegelMaskinellRequest regelRequest)
   {
      //TODO Implement the rule and return the result.
      //All information that the rule uses to make a decision should be sent as RegelMaskinellResult.HandlaggningUpdate.Uppgift.Underlag
      //(RegelUtils.createUppgiftsdatakopia() & RegelUtils.RegelUtils.createUppgiftsdatakoppling() can be used to create Underlag objects).
      //SakfragaStallningstagande should be sent in the RegelMaskinellResult.HandlaggningUpdate.Yrkande.SakfragorStallningstaganden
      //if a new SakfragaStallningstagande has been added or an existing has been updated.

      // TODO replace with underlag of the service
      var underlag = new ArrayList<>(
            List.of(
                  RegelUtils.createUppgiftsdatakopia("TEST_UNDERLAG_TYP_1", "", objectMapper),
                  RegelUtils.createUppgiftsdatakopia("TEST_UNDERLAG_TYP_1", "", objectMapper)));

      var uppgift = ImmutableUppgift.builder()
            .from(regelRequest.uppgift())
            .utfordTS(OffsetDateTime.now())
            .addAllUnderlag(underlag)
            .build();

      var handlaggningUpdate = ImmutableHandlaggningUpdate.builder()
            .handlaggning(regelRequest.handlaggning())
            .uppgift(uppgift)
            .build();

      return ImmutableRegelMaskinellSuccessResult.builder()
            .handlaggningUpdate(handlaggningUpdate)
            .utfall(Utfall.JA)
            .build();
   }
}
