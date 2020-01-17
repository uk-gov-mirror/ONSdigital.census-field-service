package uk.gov.ons.ctp.integration.censusfieldsvc.service.impl;

public class FieldServiceException extends Exception {

  private static final long serialVersionUID = -1965708379758146762L;

  /** The list of CTP faults */
  public enum Fault {
    QUESTIONNAIRE_INACTIVE,
    SYSTEM_ERROR,
    RESOURCE_NOT_FOUND,
    VALIDATION_FAILED,
    ACCESS_DENIED,
    BAD_REQUEST;
  }

  private Fault fault;
  private long timestamp = System.currentTimeMillis();

  /**
   * Constructor
   *
   * @param afault associated with the CTPException about to be created.
   */
  public FieldServiceException(final Fault afault) {
    fault = afault;
  }

  /** @return the fault associated with the CTPException. */
  public final Fault getFault() {
    return fault;
  }

  /** @return the timestamp when the CTPException was created. */
  public final long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + fault.name();
  }
}
