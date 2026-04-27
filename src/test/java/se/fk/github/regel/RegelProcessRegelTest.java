package se.fk.github.regel;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import se.fk.github.regel.logic.RegelService;
import se.fk.rimfrost.framework.regel.*;
import se.fk.rimfrost.framework.regel.maskinell.base.AbstractRegelMaskinellTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.fk.github.regel.RegelTestData.newRegelMaskinellRequest;

@QuarkusTest
@QuarkusTestResource.List(
{
      @QuarkusTestResource(WireMockRegel.class)
})
public class RegelProcessRegelTest extends AbstractRegelMaskinellTest
{

   @Inject
   RegelService regelService;

   @Test
   public void a_template_process_regel_test()
   {
      var request = newRegelMaskinellRequest();

      var result = regelService.processRegel(request);

      assertEquals(Utfall.JA, result.utfall());
   }

   // TODO: Add more tests as needed
}
