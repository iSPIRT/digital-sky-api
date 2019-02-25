package com.ispirit.digitalsky.exception;

public class ApplicationNotInApprovedByAtc extends RuntimeException {
  public ApplicationNotInApprovedByAtc() {
    super("Application Form Can Only be Approved or Rejected by AFMLU When in ATC Approved Status");
  }
}
