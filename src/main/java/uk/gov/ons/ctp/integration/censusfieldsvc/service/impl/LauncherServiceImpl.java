package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.CaseServiceClientServiceImpl;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.CaseContainerDTO;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.LauncherService;

@Service
public class LauncherServiceImpl implements LauncherService {

  private static final Logger log = LoggerFactory.getLogger(LauncherServiceImpl.class);

  @Autowired private CaseServiceClientServiceImpl caseServiceClient;

  @Override
  public String getEqUrl(UUID caseId) {
    log.with("caseId", caseId)
        .debug(
            "getEqUrl() calling Case Service to get the details required "
                + "for launching the questionnaire in EQ");
    CaseContainerDTO caseDetails = caseServiceClient.getCaseById(caseId, false);
    log.info("The case details received are: " + caseDetails);
    return null;
  }
}
