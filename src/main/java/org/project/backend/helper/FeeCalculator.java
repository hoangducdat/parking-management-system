package org.project.backend.helper;

import org.project.backend.constant.Constants;
import org.project.backend.entity.ParkingRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class FeeCalculator {
  public double calculate(ParkingRecord record) {
    String vehicleType = record.getVehicleType();
    LocalDateTime checkInTime = record.getCheckInTime();
    LocalDateTime checkOutTime = record.getCheckOutTime();

    if (checkInTime == null || checkOutTime == null) {
      return 0.0;
    }

    long hours = Duration.between(checkInTime, checkOutTime).toHours();
    LocalDateTime midnightNextDay = checkInTime.toLocalDate().plusDays(1).atStartOfDay();
    boolean crossesMidnight = checkOutTime.isAfter(midnightNextDay);

    double fee;
    if (vehicleType.equals(Constants.VEHICLE_TYPE_MOTORBIKE)) {
      if (hours < 24 && !crossesMidnight) {
        fee = Constants.MOTORBIKE_FEE;
      } else if (hours < 24 && crossesMidnight) {
        fee = Constants.MOTORBIKE_OVERNIGHT_FEE;
      } else {
        long days = (hours + 23) / 24;
        fee = Constants.MOTORBIKE_OVERNIGHT_FEE * days;
      }
    } else if (vehicleType.equals(Constants.VEHICLE_TYPE_CAR)) {
      if (hours < 24 && !crossesMidnight) {
        fee = Constants.CAR_FEE;
      } else if (hours < 24 && crossesMidnight) {
        fee = Constants.CAR_OVERNIGHT_FEE;
      } else {
        long days = (hours + 23) / 24;
        fee = Constants.CAR_OVERNIGHT_FEE * days;
      }
    } else {
      fee = 0.0;
    }
    return fee;
  }
}
