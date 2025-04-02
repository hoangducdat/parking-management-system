package org.project.backend.exception;

public class VehicleNotCheckedInException extends RuntimeException {
  public VehicleNotCheckedInException(String message) {
    super(message);
  }
}
