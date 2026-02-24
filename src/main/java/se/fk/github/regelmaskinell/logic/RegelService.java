package se.fk.github.regelmaskinell.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.fk.rimfrost.framework.regel.Utfall;
import se.fk.rimfrost.framework.regel.logic.dto.Beslutsutfall;
import se.fk.rimfrost.framework.regel.logic.entity.ImmutableErsattningData;
import se.fk.rimfrost.framework.regel.logic.entity.ImmutableUnderlag;
import se.fk.rimfrost.framework.regel.logic.entity.Underlag;
import se.fk.rimfrost.framework.regel.maskinell.logic.RegelMaskinellServiceInterface;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellResult;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellRequest;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.RegelMaskinellResult;

@ApplicationScoped
public class RegelService implements RegelMaskinellServiceInterface
{

   private static final Logger LOGGER = LoggerFactory.getLogger(RegelService.class);

   @Inject
   ObjectMapper objectMapper;

   @Override
   public RegelMaskinellResult processRegel(RegelMaskinellRequest regelResult)
   {

      //TODO Implement the rule and return the result.
      //All information that the rule uses to make a decision should be sent in the result as Underlag.
      //Ersattningar should be sent in the result if they have been updated with a new beslutsutfall or avslagsanledning.

      var underlag = regelResult.ersattning().stream()
            .map(e -> createUnderlag("Ersättning", "1.0", e))
            .toList();

      var ersattningar = regelResult.ersattning().stream()
            .map(e -> ImmutableErsattningData.builder()
                  .id(e.ersattningsId())
                  .beslutsutfall(Beslutsutfall.NEJ)
                  .avslagsanledning("Some reason")
                  .build())
            .toList();

      return ImmutableRegelMaskinellResult.builder()
            .utfall(Utfall.JA)
            .addAllUnderlag(underlag)
            .addAllErsattningar(ersattningar)
            .build();
   }

   private Underlag createUnderlag(String typ, String version, Object object)
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
}
