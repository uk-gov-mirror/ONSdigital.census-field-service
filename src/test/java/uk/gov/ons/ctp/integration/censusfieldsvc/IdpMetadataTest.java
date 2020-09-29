package uk.gov.ons.ctp.integration.censusfieldsvc;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.AppConfig;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.SsoConfig;

@RunWith(MockitoJUnitRunner.class)
public class IdpMetadataTest {
  private static final String AN_IDP_REDIRECT =
      "https://yet-another-idp/idp/profile/SAML2/Redirect/SSO";
  private static final String AN_IDP_POST = "https://yet-another-idp/idp/profile/SAML2/POST/SSO";
  private static final String AN_IDP_ENTITY_ID = "IDP-99";
  private static final String A_DEAD_CERT = "MIIDEjCCAfqgAwIBAg";

  @Mock private AppConfig appConfig;

  @InjectMocks private IdpMetadata idpMetadata;

  @Before
  public void setup() {
    SsoConfig ssoConfig = new SsoConfig();
    ssoConfig.setIdpEntityId(AN_IDP_ENTITY_ID);
    ssoConfig.setIdpPost(AN_IDP_POST);
    ssoConfig.setIdpRedirect(AN_IDP_REDIRECT);
    ssoConfig.setMetadataCertificate(A_DEAD_CERT);

    when(appConfig.getSso()).thenReturn(ssoConfig);
  }

  @Test
  public void dummy() {}

  @Test
  public void shouldLoadMetadata() throws Exception {
    String meta = idpMetadata.load();

    assertTrue(meta.contains("<md:EntityDescriptor"));
    assertTrue(meta.contains("entityID=\"" + AN_IDP_ENTITY_ID + "\""));
    assertTrue(meta.contains(A_DEAD_CERT));
    assertTrue(meta.contains("HTTP-Redirect\" Location=\"" + AN_IDP_REDIRECT + "\""));
    assertTrue(meta.contains("HTTP-POST\" Location=\"" + AN_IDP_POST + "\""));
  }
}
