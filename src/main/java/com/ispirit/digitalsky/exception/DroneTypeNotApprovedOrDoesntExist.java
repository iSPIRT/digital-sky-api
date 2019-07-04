package com.ispirit.digitalsky.exception;

public class DroneTypeNotApprovedOrDoesntExist extends RuntimeException {
  public DroneTypeNotApprovedOrDoesntExist() { super("DroneType does not exist or has not been approved yet"); }
}
