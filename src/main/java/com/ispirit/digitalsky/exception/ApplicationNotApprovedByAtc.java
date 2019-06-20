package com.ispirit.digitalsky.exception;

public class ApplicationNotApprovedByAtc extends RuntimeException {
  public ApplicationNotApprovedByAtc() {
    super("Application Form Can Only be Approved or Rejected by AFMLU When in ATC Approved Status");
  }
}
