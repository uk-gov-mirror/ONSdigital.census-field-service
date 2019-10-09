package uk.gov.ons.ctp.integration.censusfieldsvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.LauncherService;
import uk.gov.ons.ctp.integration.contactcentresvc.representation.CaseRequestDTO;

@RestController
@RequestMapping(value = "/", produces = "application/json")
public final class LauncherEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(LauncherEndpoint.class);

  private LauncherService launcherService;

  /**
   * Constructor for ContactCentreDataEndpoint
   *
   * @param caseService is a service layer object that we be doing the processing on behalf of this
   *     endpoint.
   */
  @Autowired
  public LauncherEndpoint(final LauncherService launcherService) {
    this.launcherService = launcherService;
  }

  /**
   * the GET end point to get a EQURL by caseId
   *
   * @param caseId the id of the case
   * @param requestParamsDTO contains request params
   * @return the case
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<String> getEqUrlById(
      @PathVariable("caseId") final UUID caseId, @Valid CaseRequestDTO requestParamsDTO)
      throws CTPException {
    log.with("pathParam", caseId)
        .with("requestParams", requestParamsDTO)
        .info("Entering GET getCaseById");

    String result = launcherService.getEqUrl(caseId);

    log.with(result).info("The result of calling the launcher service: " + result);

    return ResponseEntity.ok(result);
  }
}
