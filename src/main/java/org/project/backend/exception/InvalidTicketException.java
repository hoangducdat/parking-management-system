package org.project.backend.exception;

public class InvalidTicketException extends RuntimeException {
  public InvalidTicketException(String message) {
    super(message);
  }
}
