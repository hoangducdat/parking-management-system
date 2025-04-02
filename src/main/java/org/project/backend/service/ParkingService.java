package org.project.backend.service;

import java.time.LocalDateTime;
import org.project.backend.dto.request.CheckInRequest;
import org.project.backend.dto.request.CheckOutRequest;
import org.project.backend.dto.response.CheckInResponse;
import org.project.backend.dto.response.CheckOutResponse;
import org.project.backend.dto.response.StatsResponse;

public interface ParkingService {
  CheckInResponse checkIn(CheckInRequest request);
  CheckOutResponse checkOut(CheckOutRequest request);
  CheckInResponse findVehicle(String licensePlate);
  StatsResponse getStats(LocalDateTime start, LocalDateTime end);
}
