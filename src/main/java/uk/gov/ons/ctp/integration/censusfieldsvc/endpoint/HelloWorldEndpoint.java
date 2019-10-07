package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.HelloWorldService;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public final class HelloWorldEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(HelloWorldEndpoint.class);

  private HelloWorldService helloWorldService;

  /**
   * Constructor.
   *
   * @param helloWorldService example service.
   */
  @Autowired
  public HelloWorldEndpoint(
      final HelloWorldService helloWoldService, HelloWorldService helloWorldService) {
    this.helloWorldService = helloWorldService;
  }

  /**
   * Endpoint to prove that service is working. Delete when another endpoint has been added.
   *
   * @return a String.
   */
  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String getAddressesBySearchQuery() {
    log.info("Entering GET hello");
    return helloWorldService.getHelloText();
  }
}
