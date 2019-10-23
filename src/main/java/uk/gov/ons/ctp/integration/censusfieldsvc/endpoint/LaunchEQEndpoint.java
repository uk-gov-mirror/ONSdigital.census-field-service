package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.SAMLUser;
import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;

@RestController
@RequestMapping(value = "/launch", produces = "application/json")
public final class LaunchEQEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(LaunchEQEndpoint.class);

  /**
   * Redirects the caller to start EQ for the supplied case. If there is no user signed in then the
   * G-Suite IDP will require them to sign in before control is passed to this endpoint.
   *
   * @param caseId the id of the case.
   * @param user contains information about the signed in user.
   * @return a ResponseEntity redirecting the caller to EQ.
   * @throws CTPException something went wrong.
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<String> launchEQ(
      @PathVariable("caseId") final UUID caseId, @SAMLUser SAMLUserDetails user)
      throws CTPException {
    log.with("pathParam", caseId).with("user", user.getUsername()).info("Entering launchEQ");

    // To prove redirection build a test url. This is based on the data available to prove it's
    // dynamic.
    String uuidPart = caseId.toString().split("-")[1];
    String userPrefix = user.getUsername().split("@")[0];
    String targetUrl = "http://www.google.com/search?q=" + uuidPart + "+" + userPrefix;

    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", targetUrl);
    return new ResponseEntity<String>(headers, HttpStatus.TEMPORARY_REDIRECT);
  }
}
