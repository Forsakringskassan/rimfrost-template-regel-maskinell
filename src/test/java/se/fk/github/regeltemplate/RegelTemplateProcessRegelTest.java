package se.fk.github.regeltemplate;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import se.fk.github.regeltemplate.logic.RegelTemplateService;
import se.fk.rimfrost.framework.regel.*;
import se.fk.rimfrost.framework.regel.maskinell.base.AbstractRegelMaskinellTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.fk.github.regeltemplate.RegelTemplateTestData.newRegelMaskinellRequest;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockRegelTemplate.class)
})
public class RegelTemplateProcessRegelTest extends AbstractRegelMaskinellTest
{

   @Inject
   RegelTemplateService regelService;

   @Test
   public void a_template_process_regel_test()
   {
      var request = newRegelMaskinellRequest();

      var result = regelService.processRegel(request);

      assertEquals(Utfall.JA, result.utfall());
   }

   // TODO: Add more tests as needed
}
