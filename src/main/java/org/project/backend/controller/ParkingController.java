package org.project.backend.controller;

import jakarta.validation.Valid;
import org.project.backend.dto.request.CheckInRequest;
import org.project.backend.dto.request.CheckOutRequest;
import org.project.backend.dto.request.StatsRequest;
import org.project.backend.dto.response.ApplicationResponse;
import org.project.backend.dto.response.CheckInResponse;
import org.project.backend.dto.response.CheckOutResponse;
import org.project.backend.dto.response.StatsResponse;
import org.project.backend.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

  private static final Logger log = LoggerFactory.getLogger(ParkingController.class);
  private final ParkingService parkingService;

  public ParkingController(ParkingService parkingService) {
    this.parkingService = parkingService;
  }

  @PostMapping("/check-in")
  public ApplicationResponse<CheckInResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
    log.info("CheckIn request: {}", request);
    CheckInResponse response = parkingService.checkIn(request);
    log.info("CheckIn response {}", response);
    return ApplicationResponse.of(HttpStatus.OK.value(), response);
  }

  @PostMapping("/check-out")
  public ApplicationResponse<CheckOutResponse> checkOut(@RequestBody CheckOutRequest request) {
    log.info("CheckOut request: {}", request);
    CheckOutResponse response = parkingService.checkOut(request);
    log.info("CheckOut response {}", response);
    return ApplicationResponse.of(HttpStatus.OK.value(), response);
  }

  @GetMapping("/find")
  public ApplicationResponse<CheckInResponse> findVehicle(@RequestParam("license_plate") String licensePlate) {
    log.info("Find vehicle request: {}", licensePlate);
    CheckInResponse response = parkingService.findVehicle(licensePlate);
    log.info("Find vehicle response {}", response);
    return ApplicationResponse.of(HttpStatus.OK.value(), response);
  }

  @PostMapping("/stats")
  public ApplicationResponse<StatsResponse> getStats(@Valid @RequestBody StatsRequest request) {
    log.info("Get stats request: {}", request);
    StatsResponse response = parkingService.getStats(request.getStart(), request.getEnd());
    log.info("Get stats response {}", response);
    return ApplicationResponse.of(200, response);
  }
}
