package org.project.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckInResponse {

  private String id;
  private String licensePlate;
  private String vehicleType;
  private String zoneName;
  private LocalDateTime checkInTime;
  private LocalDateTime checkOutTime;

  public CheckInResponse() {
  }

  public CheckInResponse(String id, String licensePlate, String vehicleType, String zoneName,
      LocalDateTime checkInTime, LocalDateTime checkOutTime) {
    this.id = id;
    this.licensePlate = licensePlate;
    this.vehicleType = vehicleType;
    this.zoneName = zoneName;
    this.checkInTime = checkInTime;
    this.checkOutTime = checkOutTime;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLicensePlate() {
    return licensePlate;
  }

  public void setLicensePlate(String licensePlate) {
    this.licensePlate = licensePlate;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(String vehicleType) {
    this.vehicleType = vehicleType;
  }

  public String getZoneName() {
    return zoneName;
  }

  public void setZoneName(String zoneName) {
    this.zoneName = zoneName;
  }

  public LocalDateTime getCheckInTime() {
    return checkInTime;
  }

  public void setCheckInTime(LocalDateTime checkInTime) {
    this.checkInTime = checkInTime;
  }

  public LocalDateTime getCheckOutTime() {
    return checkOutTime;
  }

  public void setCheckOutTime(LocalDateTime checkOutTime) {
    this.checkOutTime = checkOutTime;
  }
}
