package uk.gov.ons.ctp.integration.censusfieldsvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.AppConfig;
import uk.gov.ons.ctp.integration.censusfieldsvc.config.SsoConfig;

/** Customise an IDP Metadata XML file. */
@Component
public class IdpMetadata {

  private AppConfig appConfig;

  @Autowired
  public IdpMetadata(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  /**
   * This method loads the G-suite IDP metadata. It reads the contents of a template metadata file
   * and replaces some placeholders with the actual runtime values.
   *
   * @return a String containing the G-suite IDP metadata.
   * @throws IOException if there is a problem reading the metadata file.
   */
  public String load() throws IOException {
    String rawIdpMetadata = readResourceFile("IDPMetadata.xml");

    String idpMetadata = replaceMetadataPlaceholders(rawIdpMetadata);
    return idpMetadata;
  }

  private String readResourceFile(String resourcePath) throws IOException {
    try (InputStream inputStream =
        getClass().getClassLoader().getResource(resourcePath).openStream()) {
      StringBuilder textBuilder = new StringBuilder();
      try (Reader reader =
          new BufferedReader(
              new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
        int c = 0;
        while ((c = reader.read()) != -1) {
          textBuilder.append((char) c);
        }
      }
      String idpMetadata = textBuilder.toString();
      return idpMetadata;
    }
  }

  /**
   * Replaces placeholders in the supplied string with actual values from application properties.
   * Placeholders are in the form '${name}'.
   *
   * @param rawMetadata is the string which requires placeholders to be resolved.
   * @return the completed metadata String.
   */
  private String replaceMetadataPlaceholders(String rawMetadata) {
    String metadata = rawMetadata;
    SsoConfig ssoConfig = appConfig.getSso();
    metadata = replacePlaceholder(metadata, "sso.idpRedirect", ssoConfig.getIdpRedirect());
    metadata = replacePlaceholder(metadata, "sso.idpPost", ssoConfig.getIdpPost());
    metadata = replacePlaceholder(metadata, "sso.idpEntityId", ssoConfig.getIdpEntityId());
    String cert = ssoConfig.getMetadataCertificate().replace("\n", "").replace("\r", "");
    return replacePlaceholder(metadata, "sso.metadataCertificate", cert);
  }

  private String replacePlaceholder(
      String metadata, String placeholderName, String placeholderValue) {
    String placeholderSpec = "\\$\\{" + placeholderName + "\\}";
    String updatedMetadata = metadata.replaceAll(placeholderSpec, placeholderValue);
    return updatedMetadata;
  }
}
