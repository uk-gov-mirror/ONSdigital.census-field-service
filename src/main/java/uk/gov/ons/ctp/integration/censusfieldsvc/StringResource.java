package uk.gov.ons.ctp.integration.censusfieldsvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.joda.time.DateTime;
import org.opensaml.util.resource.Resource;
import org.opensaml.util.resource.ResourceException;

/** This class allows a String value to be fed into a SAML metadata provider. */
public class StringResource implements Resource {
  private String metadata;
  private DateTime modificationTime = new DateTime();

  public StringResource(String metadata) {
    this.metadata = metadata;
  }

  @Override
  public String getLocation() {
    return "Metadata from IDPMetadata.xml";
  }

  @Override
  public boolean exists() throws ResourceException {
    return true;
  }

  @Override
  public InputStream getInputStream() throws ResourceException {
    return new ByteArrayInputStream(metadata.getBytes());
  }

  @Override
  public DateTime getLastModifiedTime() throws ResourceException {
    return modificationTime;
  }
}
