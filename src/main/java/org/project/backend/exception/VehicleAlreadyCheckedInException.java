package org.project.backend.exception;

public class VehicleAlreadyCheckedInException extends RuntimeException {
  public VehicleAlreadyCheckedInException(String message) {
    super(message);
  }
}
