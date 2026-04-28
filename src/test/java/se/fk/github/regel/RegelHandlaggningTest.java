package se.fk.github.regel;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import se.fk.rimfrost.framework.handlaggning.model.ImmutableUnderlag;
import se.fk.rimfrost.framework.handlaggning.model.Underlag;
import se.fk.rimfrost.framework.regel.maskinell.base.AbstractRegelMaskinellHandlaggningTest;
import se.fk.rimfrost.jaxrsspec.controllers.generatedsource.model.ProduceratResultat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockRegel.class)
})
public class RegelHandlaggningTest extends AbstractRegelMaskinellHandlaggningTest
{

   @Override
   protected ArrayList<Underlag> createExpectedUnderlag()
   {
      var _template_mydata_1_UnderlagData = ""; // TODO ersätt med regelns förväntade underlag
       var _template_mydata_2_UnderlagData = ""; // TODO ersätt med regelns förväntade underlag
      return new ArrayList<>(List.of(
            ImmutableUnderlag.builder()
                  .typ("_TEMPLATE_TYP_1_") // TODO ersätt med underlagstyp
                  .version(1)
                  .data(_template_mydata_1_UnderlagData)
                  .build(),
            ImmutableUnderlag.builder()
                    .typ("_TEMPLATE_TYP_1_") // TODO ersätt med underlagstyp
                  .version(1)
                  .data(_template_mydata_2_UnderlagData)
                  .build()));
   }

   @Override
   protected List<ProduceratResultat> createExpectedProduceradeResultat()
   {
      List<ProduceratResultat> list = new ArrayList<>();

      // TODO ersätt med regelns förväntade producerade resultat

      ProduceratResultat r1 = new ProduceratResultat();
      r1.setId(UUID.fromString("66666666-6666-6666-6666-666666661234"));
      r1.setYrkandestatus("YRKAT");

      ProduceratResultat r2 = new ProduceratResultat();
      r2.setId(UUID.fromString("66666666-6666-6666-6666-666667771234"));
      r2.setYrkandestatus("YRKAT");

      list.add(r1);
      list.add(r2);

      return list;
   }
}
