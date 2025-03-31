package org.project.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parking_records")
public class ParkingRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "license_plate", nullable = false)
  private String licensePlate;

  @Column(name = "vehicle_type", nullable = false)
  private String vehicleType;

  @Column(name = "zone", nullable = false)
  private String zone;

  @Column(name = "check_in_time", nullable = false)
  private LocalDateTime checkInTime;

  @Column(name = "check_out_time")
  private LocalDateTime checkOutTime;

  @Column(name = "fee")
  private Double fee;

  public ParkingRecord() {
  }

  public ParkingRecord(String id, String licensePlate, String vehicleType, String zone,
      LocalDateTime checkInTime, LocalDateTime checkOutTime, Double fee) {
    this.id = id;
    this.licensePlate = licensePlate;
    this.vehicleType = vehicleType;
    this.zone = zone;
    this.checkInTime = checkInTime;
    this.checkOutTime = checkOutTime;
    this.fee = fee;
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

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
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

  public Double getFee() {
    return fee;
  }

  public void setFee(Double fee) {
    this.fee = fee;
  }
}
