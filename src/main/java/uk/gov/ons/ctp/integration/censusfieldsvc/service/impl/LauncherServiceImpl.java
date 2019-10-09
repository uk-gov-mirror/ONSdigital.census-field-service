package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import uk.gov.ons.ctp.integration.caseapiclient.caseservice.CaseServiceClientServiceImpl;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.CaseContainerDTO;
import uk.gov.ons.ctp.integration.censusfieldsvc.endpoint.LauncherEndpoint;
import uk.gov.ons.ctp.integration.censusfieldsvc.service.LauncherService;

@Service
public class LauncherServiceImpl implements LauncherService { 
	
  private static final Logger log = LoggerFactory.getLogger(LauncherServiceImpl.class);

  @Autowired private CaseServiceClientServiceImpl caseServiceClient;

  @Override
  public String getEqUrl(UUID caseId) {
    CaseContainerDTO caseDetails = caseServiceClient.getCaseById(caseId, false);
    log.info("The case details received are: " + caseDetails);
    return null;
  }
}
