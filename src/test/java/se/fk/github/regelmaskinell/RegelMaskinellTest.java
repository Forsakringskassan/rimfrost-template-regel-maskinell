package se.fk.github.regelmaskinell;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import se.fk.github.regelmaskinell.logic.RegelService;
import se.fk.rimfrost.framework.regel.*;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableErsattning;
import se.fk.rimfrost.framework.regel.maskinell.logic.dto.ImmutableRegelMaskinellRequest;
import java.time.LocalDate;
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
      var ersattning = ImmutableErsattning.builder()
            .ersattningsId(UUID.randomUUID())
            .ersattningsTyp("Dagsersättning")
            .omfattningsProcent(100)
            .belopp(40000)
            .berakningsgrund(40000)
            .beslutsutfall("JA")
            .franOchMed(LocalDate.now())
            .tillOchMed(LocalDate.now())
            .build();

      var request = ImmutableRegelMaskinellRequest.builder()
            .kundbehovsflodeId(UUID.randomUUID())
            .personnummer("19900101-1234")
            .formanstyp("VAH")
            .addErsattning(ersattning)
            .build();

      var result = regelService.processRegel(request);

      assertEquals(Utfall.JA, result.utfall());
      assertEquals(1, result.underlag().size());
      assertEquals(1, result.ersattningar().size());
   }
}
