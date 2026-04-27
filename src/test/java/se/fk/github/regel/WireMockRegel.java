package se.fk.github.regel;

import com.github.tomakehurst.wiremock.WireMockServer;
import se.fk.rimfrost.framework.regel.maskinell.helpers.WireMockRegelMaskinell;
import java.util.HashMap;
import java.util.Map;

public class WireMockRegel extends WireMockRegelMaskinell
{
   @Override
   protected Map<String, String> wiremockMapping(WireMockServer server)
   {
      Map<String, String> map = new HashMap<>(super.wiremockMapping(server));
      // TODO: Add any rule-specific base-urls that needs mocking to the map. Ex: map.put("abc.api.base-url", server.baseUrl())
      return map;
   }
}
