package org.project.backend.exception;

import org.project.backend.dto.response.ApplicationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(InvalidInputException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApplicationResponse<Void> handleInvalidInputException(InvalidInputException ex) {
    log.error("Invalid input error: {}", ex.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("code", "INVALID_INPUT");
    return ApplicationResponse.error(HttpStatus.BAD_REQUEST.value(), errorDetails);
  }

  @ExceptionHandler(ParkingFullException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApplicationResponse<Void> handleParkingFullException(ParkingFullException ex) {
    log.error("Parking full error: {}", ex.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("code", "PARKING_FULL");
    return ApplicationResponse.error(HttpStatus.CONFLICT.value(), errorDetails);
  }

  @ExceptionHandler(VehicleNotCheckedInException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApplicationResponse<Void> handleVehicleNotCheckedInException(VehicleNotCheckedInException ex) {
    log.error("Vehicle not checked in error: {}", ex.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("code", "VEHICLE_NOT_CHECKED_IN");
    return ApplicationResponse.error(HttpStatus.NOT_FOUND.value(), errorDetails);
  }

  @ExceptionHandler(InvalidTicketException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApplicationResponse<Void> handleInvalidTicketException(InvalidTicketException ex) {
    log.error("Invalid ticket error: {}", ex.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("code", "INVALID_TICKET");
    return ApplicationResponse.error(HttpStatus.BAD_REQUEST.value(), errorDetails);
  }

  @ExceptionHandler(VehicleNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApplicationResponse<Void> handleVehicleNotFoundException(VehicleNotFoundException ex) {
    log.error("Vehicle not found error: {}", ex.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    errorDetails.put("code", "VEHICLE_NOT_FOUND");
    return ApplicationResponse.error(HttpStatus.NOT_FOUND.value(), errorDetails);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApplicationResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errorDetails.put(error.getField(), error.getDefaultMessage())
    );
    errorDetails.put("code", "VALIDATION_FAILED");
    return ApplicationResponse.error(HttpStatus.BAD_REQUEST.value(), errorDetails);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApplicationResponse<Void> handleRuntimeException(RuntimeException ex) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", "An unexpected error occurred");
    errorDetails.put("code", "INTERNAL_SERVER_ERROR");
    return ApplicationResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorDetails);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApplicationResponse<Void> handleGeneralException(Exception ex) {
    log.error("General error occurred: {}", ex.getMessage(), ex);
    Map<String, String> errorDetails = new HashMap<>();
    errorDetails.put("message", "Something went wrong");
    errorDetails.put("code", "UNKNOWN_ERROR");
    return ApplicationResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorDetails);
  }
}
