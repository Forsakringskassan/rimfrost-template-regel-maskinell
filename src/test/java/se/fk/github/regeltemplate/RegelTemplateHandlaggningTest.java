package se.fk.github.regeltemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import se.fk.rimfrost.framework.handlaggning.model.Uppgiftsdata;
import se.fk.rimfrost.framework.handlaggning.model.Uppgiftsdatakoppling;
import se.fk.rimfrost.framework.regel.logic.RegelUtils;
import se.fk.rimfrost.framework.regel.maskinell.base.AbstractRegelMaskinellHandlaggningTest;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockRegelTemplate.class)
})
public class RegelTemplateHandlaggningTest extends AbstractRegelMaskinellHandlaggningTest
{

   @Inject
   ObjectMapper objectMapper;

   @Override
   protected ArrayList<Uppgiftsdata> createExpectedUnderlag()
   {
      var _template_mydata_1_UnderlagData = ""; // TODO ersätt med regelns förväntade underlag
      var _template_mydata_2_UnderlagData = ""; // TODO ersätt med regelns förväntade underlag
      return new ArrayList<>(List.of(
            RegelUtils.createUppgiftsdatakopia(
                  "TEST_UNDERLAG_TYP_1", // TODO ersätt med underlagstyp
                  _template_mydata_1_UnderlagData,
                  objectMapper),
            RegelUtils.createUppgiftsdatakopia(
                  "TEST_UNDERLAG_TYP_1", // TODO ersätt med underlagstyp
                  _template_mydata_2_UnderlagData,
                  objectMapper)));
   }

   @Override
   protected List<Uppgiftsdatakoppling> createExpectedResultat()
   {
      // TODO ersätt med regelns förväntade resultat

      return List.of(
      // RegelUtils.createUppgiftsdatakoppling(...)
      );
   }
}
