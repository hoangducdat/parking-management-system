package org.project.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StatsResponse {

  private long parkingCount;
  private double revenue;
  private double averageParkingTime;

  public StatsResponse() {
  }

  public StatsResponse(long parkingCount, double revenue, double averageParkingTime) {
    this.parkingCount = parkingCount;
    this.revenue = revenue;
    this.averageParkingTime = averageParkingTime;
  }

  public long getParkingCount() {
    return parkingCount;
  }

  public void setParkingCount(long parkingCount) {
    this.parkingCount = parkingCount;
  }

  public double getRevenue() {
    return revenue;
  }

  public void setRevenue(double revenue) {
    this.revenue = revenue;
  }

  public double getAverageParkingTime() {
    return averageParkingTime;
  }

  public void setAverageParkingTime(double averageParkingTime) {
    this.averageParkingTime = averageParkingTime;
  }
}
