package org.project.backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.project.backend.constant.Constants;
import org.project.backend.dto.request.CheckInRequest;
import org.project.backend.dto.request.CheckOutRequest;
import org.project.backend.dto.response.CheckInResponse;
import org.project.backend.dto.response.CheckOutResponse;
import org.project.backend.dto.response.StatsResponse;
import org.project.backend.entity.ParkingRecord;
import org.project.backend.entity.ParkingZone;
import org.project.backend.entity.PaymentLog;
import org.project.backend.entity.User;
import org.project.backend.exception.InvalidTicketException;
import org.project.backend.exception.ParkingFullException;
import org.project.backend.exception.VehicleNotCheckedInException;
import org.project.backend.exception.VehicleNotFoundException;
import org.project.backend.repository.ParkingRecordRepository;
import org.project.backend.repository.ParkingZoneRepository;
import org.project.backend.repository.PaymentLogRepository;
import org.project.backend.repository.UserRepository;
import org.project.backend.helper.FeeCalculator;
import org.project.backend.service.ParkingService;
import org.project.backend.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ParkingServiceImpl implements ParkingService {
  private static final Logger log = LoggerFactory.getLogger(ParkingServiceImpl.class);
  private final ParkingRecordRepository parkingRecordRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  private final PaymentLogRepository paymentLogRepository;
  private final UserRepository userRepository;
  private final RedisService redisService;
  private final FeeCalculator feeCalculator;

  public ParkingServiceImpl(ParkingRecordRepository parkingRecordRepository,
      ParkingZoneRepository parkingZoneRepository,
      PaymentLogRepository paymentLogRepository,
      UserRepository userRepository,
      RedisService redisService,FeeCalculator feeCalculator) {
    this.parkingRecordRepository = parkingRecordRepository;
    this.parkingZoneRepository = parkingZoneRepository;
    this.paymentLogRepository = paymentLogRepository;
    this.userRepository = userRepository;
    this.redisService = redisService;
    this.feeCalculator = feeCalculator;
  }

    @Override
    @Transactional
    public CheckInResponse checkIn(CheckInRequest request) {
      log.info("CheckIn vehicle with licensePlate {} : ", request.getLicensePlate());
      String licensePlate = request.getLicensePlate();
      String vehicleType = request.getVehicleType();

      log.debug("Fetching all parking zones from database");
      List<ParkingZone> zones = parkingZoneRepository.findAll();
      if (zones.isEmpty()) {
        log.error("No parking zones found. Cannot proceed with check-in.");
        throw new RuntimeException("No parking zones available in the system");
      }

      ParkingZone selectedZone = null;
      String redisKey;
      int currentCount;

      for (ParkingZone zone : zones) {
        log.info("Checking parking zone {} : ", zone.getId());
        redisKey = Constants.ZONE_COUNT_PREFIX + zone.getId() + ":current_count";
        log.debug("Retrieving current count from Redis for zone {} with key {}", zone.getId(), redisKey);
        String currentCountStr = redisService.getValue(redisKey);
        currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
        log.debug("Zone {} has current count: {} and max slots: {}", zone.getId(), currentCount, zone.getMaxSlots());

        if (currentCount < zone.getMaxSlots()) {
          selectedZone = zone;
          log.info("Selected zone {} for check-in. Incrementing count in Redis", zone.getId());
          redisService.increment(redisKey, 1);
          break;
        }
      }

      if (selectedZone == null) {
        log.error("No available parking slots for vehicle: {}", request.getLicensePlate());
        throw new ParkingFullException("No available parking slots in any zone");
      }

      log.debug("Creating new ParkingRecord for vehicle: {}", licensePlate);
      ParkingRecord record = new ParkingRecord(licensePlate, vehicleType, selectedZone, LocalDateTime.now());
      ParkingRecord savedRecord = parkingRecordRepository.save(record);
      log.info("Vehicle checked in successfully with ID: {}", savedRecord.getId());

      String redisParkingKey = Constants.PARKING_KEY_PREFIX + savedRecord.getId();
      log.debug("Storing zone name in Redis with key: {} (no TTL)", redisParkingKey);
      redisService.save(redisParkingKey, selectedZone.getZoneName());
      log.info("Saved vehicle successfully with license plate {} : ", licensePlate);

      return mapToResponse(savedRecord);
    }

    @Override
    @Transactional
    public CheckOutResponse checkOut(CheckOutRequest request) {
      log.info("Starting check-out process for vehicle with license plate: {}", request.getLicensePlate());
      String licensePlate = request.getLicensePlate();

      log.debug("Received check-out request: licensePlate={}", licensePlate);
      if (licensePlate == null) {
        log.error("License plate is null in check-out request");
        throw new IllegalArgumentException("License plate cannot be null");
      }

      String operator = getCurrentOperator();
      log.info("Operator: {}", operator);
      if (operator == null) {
        log.error("No operator logged in for check-out process");
        throw new IllegalStateException("No operator logged in");
      }

      log.debug("Fetching parking record for license plate: {}", licensePlate);
      ParkingRecord record = Optional.ofNullable(parkingRecordRepository.findByLicensePlate(licensePlate))
          .orElseThrow(() -> {
            log.error("No active parking record found for vehicle: {}", licensePlate);
            return new VehicleNotCheckedInException("No active parking record found for license plate " + licensePlate);
          });

      String redisParkingKey = Constants.PARKING_KEY_PREFIX + record.getId();
      log.debug("Checking Redis for parking ticket with key: {}", redisParkingKey);
      String zoneName = Optional.ofNullable(redisService.getValue(redisParkingKey))
          .orElseGet(() -> {
            log.warn("Parking ticket not found in Redis for vehicle: {}. Checking DB instead.", licensePlate);
            if (record.getCheckOutTime() != null) {
              log.error("Vehicle already checked out: {}", licensePlate);
              throw new InvalidTicketException("Vehicle " + licensePlate + " has already checked out");
            }
            return record.getZone().getZoneName();
          });

      User operatorId = userRepository.findIdByUsername(operator);
      log.info("Fetching operator with ID: {}", operatorId);

      LocalDateTime now = LocalDateTime.now();
      log.debug("Updating check-out time for record: {}", record.getId());
      record.setCheckOutTime(now);
      parkingRecordRepository.save(record);
      log.info("Check-out recorded for vehicle: {}", licensePlate);

      double fee = feeCalculator.calculate(record);
      log.debug("Calculated fee for vehicle {}: {}", licensePlate, fee);

      PaymentLog paymentLog = new PaymentLog();
      paymentLog.setParkingRecord(record);
      paymentLog.setAmount(fee);
      paymentLog.setPaymentTime(now);
      paymentLog.setOperator(operatorId);
      paymentLogRepository.save(paymentLog);
      log.info("Payment logged for vehicle: {} with fee: {}", licensePlate, fee);

      String redisZoneKey = Constants.ZONE_COUNT_PREFIX + record.getZone().getId() + ":current_count";
      log.debug("Decrementing zone count in Redis with key: {}", redisZoneKey);
      redisService.decrement(redisZoneKey, 1);

      log.debug("Deleting parking ticket from Redis with key: {}", redisParkingKey);
      redisService.deleteKey(redisParkingKey);

      CheckOutResponse response = new CheckOutResponse(
          paymentLog.getId(),
          paymentLog.getParkingRecord().getId(),
          paymentLog.getAmount(),
          paymentLog.getPaymentTime(),
          paymentLog.getOperator().getId()
      );
      log.info("Check-out completed successfully for vehicle: {}", licensePlate);
      return response;
    }

    @Override
    public CheckInResponse findVehicle(String licensePlate) {
      log.info("Finding vehicle with license plate: {}", licensePlate);
      ParkingRecord record = Optional.ofNullable(parkingRecordRepository.findByLicensePlate(licensePlate))
          .orElseThrow(() -> {
            log.error("Vehicle not found with license plate: {}", licensePlate);
            return new VehicleNotFoundException("Vehicle with license plate " + licensePlate + " not found");
          });

      log.info("Vehicle found with ID: {}", record.getId());
      return mapToResponse(record);
    }

    @Override
    public StatsResponse getStats(LocalDateTime start, LocalDateTime end) {
      log.info("Fetching statistics from {} to {}", start, end);
      long count = parkingRecordRepository.countByCheckInTimeBetween(start, end);
      double revenue = getRevenue(start, end);
      double avgTime = getAverageParkingTime(start, end);
      log.info("Stats retrieved - Count: {}, Revenue: {}, Avg Time: {}", count, revenue, avgTime);
      return new StatsResponse(count, revenue, avgTime);
    }

    private double getRevenue(LocalDateTime start, LocalDateTime end) {
      log.debug("Summing fees from payment logs between {} and {}", start, end);
      Double revenue = paymentLogRepository.sumFeesByPaymentTimeBetween(start, end);
      double result = revenue != null ? revenue : 0.0;
      log.debug("Revenue calculated: {}", result);
      return result;
    }

    private double getAverageParkingTime(LocalDateTime start, LocalDateTime end) {
      log.debug("Calculating average parking time for records between {} and {}", start, end);
      double avgTime = parkingRecordRepository.findAll().stream()
          .filter(r -> r.getCheckOutTime() != null && r.getCheckInTime().isAfter(start) && r.getCheckOutTime().isBefore(end))
          .mapToLong(r -> Duration.between(r.getCheckInTime(), r.getCheckOutTime()).toMinutes())
          .average()
          .orElse(0.0);
      log.debug("Average parking time calculated: {}", avgTime);
      return avgTime;
    }

    private CheckInResponse mapToResponse(ParkingRecord record) {
      log.debug("Mapping ParkingRecord {} to CheckInResponse", record.getId());
      String zoneName = (record.getZone() != null) ? record.getZone().getZoneName() : "Unknown";
      CheckInResponse response = new CheckInResponse(
          record.getId(),
          record.getLicensePlate(),
          record.getVehicleType(),
          zoneName,
          record.getCheckInTime(),
          record.getCheckOutTime()
      );
      log.debug("Mapped ParkingRecord to response: id={}, licensePlate={}", response.getId(), response.getLicensePlate());
      return response;
    }

    private String getCurrentOperator() {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getUsername();
      } else {
        return principal.toString();
      }
    }
  }

