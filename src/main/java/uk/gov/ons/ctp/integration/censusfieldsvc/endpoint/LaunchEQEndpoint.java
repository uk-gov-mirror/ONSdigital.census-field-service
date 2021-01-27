package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.SAMLUser;
import com.github.ulisesbocchio.spring.boot.security.saml.user.SAMLUserDetails;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
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
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.LauncherService;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.SurveyLaunchedService;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.FieldServiceException.Fault;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.impl.LaunchDetails;

@RestController
@RequestMapping(value = "/launch", produces = "application/json")
public final class LaunchEQEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(LaunchEQEndpoint.class);

  @Autowired private LauncherService launcherService;

  @Autowired private SurveyLaunchedService surveyLaunchedService;

  /**
   * Redirects the caller to start EQ for the supplied case. If there is no user signed in then the
   * G-Suite IDP will require them to sign in before control is passed to this endpoint.
   *
   * @param caseId the id of the case
   * @param user contains information about the signed in user.
   * @return a ResponseEntity redirecting the caller to EQ.
   * @throws CTPException something went wrong.
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public RedirectView launchEQ(
      @PathVariable("caseId") final String caseIdStr,
      @SAMLUser SAMLUserDetails user,
      RedirectAttributes redirectAttribs) {
    log.with("pathParam", caseIdStr).with("user", user.getUsername()).info("Entering launchEQ");

    LaunchDetails launchDetails;
    try {
      UUID caseId = UUID.fromString(caseIdStr);
      launchDetails = launcherService.getEqUrl(user.getUsername(), caseId);
    } catch (IllegalArgumentException e) {
      return errorRedirect("Bad request - Case ID invalid", redirectAttribs, e);
    } catch (FieldServiceException fse) {
      if (fse.getFault() == Fault.QUESTIONNAIRE_INACTIVE) {
        return new RedirectView("/questionnaireInactive", true);
      } else {
        switch (fse.getFault()) {
          case RESOURCE_NOT_FOUND:
            return errorRedirect("Data could not be found", redirectAttribs, fse);
          case BAD_REQUEST:
            return errorRedirect("Bad request", redirectAttribs, fse);
          case SYSTEM_ERROR:
            return errorRedirect("System error", redirectAttribs, fse);
          default:
            return errorRedirect("Unknown", redirectAttribs, fse);
        }
      }
    }

    try {
      surveyLaunchedService.surveyLaunched(
          launchDetails.getQuestionnaireId(), launchDetails.getCaseId(), user.getUsername());
    } catch (Exception e) {
      log.with("caseId", launchDetails.getCaseId())
          .with("errorMessage", e.getMessage())
          .warn("Failed to send surveyLaunched event");
      return errorRedirect("System error", redirectAttribs, e);
    }

    log.with("eqURL", launchDetails.getEqUrl()).debug("Redirecting caller to EQ");
    log.info("Exiting launchEQ");
    return new RedirectView(launchDetails.getEqUrl());
  }

  private RedirectView errorRedirect(
      String reason, RedirectAttributes redirectAttribs, Exception exception) {

    String sha256Hex = "";
    Date date = new Date();
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash =
          digest.digest(Long.valueOf(date.getTime()).toString().getBytes(StandardCharsets.UTF_8));
      sha256Hex = new String(Hex.encode(hash));
      redirectAttribs.addFlashAttribute("incident", sha256Hex.substring(0, 8));
    } catch (Exception e) {
      log.with("errorMessage", e.getMessage()).warn("Could not produce error hash for diagnostic");
      // carry on regardless - main functionality unaffected
    }
    log.with("incident", sha256Hex).error("Failed to launch EQ", exception);
    redirectAttribs.addFlashAttribute("reason", reason);
    return new RedirectView("/error", true);
  }
}
