package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.github.ulisesbocchio.spring.boot.security.saml.annotation.SAMLUser;
import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.AppConfig;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.LauncherService;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.SurveyLaunchedService;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException.Fault;

@RestController
@RequestMapping(value = "/launch", produces = "application/json")
public final class LaunchEQEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(LaunchEQEndpoint.class);

  @Autowired
  private LauncherService launcherService;

  @Autowired
  private AppConfig appConfig;

  @Autowired private SurveyLaunchedService surveyLaunchedService;

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
  public RedirectView launchEQ(@PathVariable("caseId") final String caseIdStr,
      @SAMLUser SAMLUserDetails user, RedirectAttributes redirectAttribs) {
    log.with("pathParam", caseIdStr).with("user", user.getUsername()).info("Entering launchEQ");

    String errorReason = null;
    RedirectView redirect = new RedirectView("/error", true);

    try {
      UUID caseId = UUID.fromString(caseIdStr);
      String eqURL = launcherService.getEqUrl(user.getUsername(), caseId);
      redirect = new RedirectView(eqURL);
      log.with("eqURL", eqURL).debug("Redirecting caller to EQ");
    } catch (IllegalArgumentException e) {
      errorReason = "Bad request - Case ID invalid";
    } catch (FieldServiceException fse) {
      if (fse.getFault() == Fault.QUESTIONNAIRE_INACTIVE) {
        redirect = new RedirectView("/questionnaireInactive", true);
      } else {
        switch (fse.getFault()) {
          case RESOURCE_NOT_FOUND:
            errorReason = "Data could not be found";
            break;
          case BAD_REQUEST:
            errorReason = "Bad request";
            break;
          case SYSTEM_ERROR:
            errorReason = "System error";
            break;
          default:
            errorReason = "Unknown";
            break;
        }
      }
    }
    if (errorReason != null) {
      Date date = new Date();
      try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash =
            digest.digest(Long.valueOf(date.getTime()).toString().getBytes(StandardCharsets.UTF_8));
        String sha256hex = new String(Hex.encode(hash));
        redirectAttribs.addFlashAttribute("incident", sha256hex.substring(0, 8));
      } catch (Exception e) {
        //
      }
      redirectAttribs.addFlashAttribute("reason", errorReason);
    }
    return redirect;
  }
}
