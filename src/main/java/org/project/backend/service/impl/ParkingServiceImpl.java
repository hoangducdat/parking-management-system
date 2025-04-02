package org.project.backend.service.impl;

import java.util.List;
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
import org.project.backend.exception.InvalidInputException;
import org.project.backend.exception.InvalidTicketException;
import org.project.backend.exception.ParkingFullException;
import org.project.backend.exception.VehicleNotCheckedInException;
import org.project.backend.exception.VehicleNotFoundException;
import org.project.backend.repository.ParkingRecordRepository;
import org.project.backend.repository.ParkingZoneRepository;
import org.project.backend.repository.PaymentLogRepository;
import org.project.backend.repository.UserRepository;
import org.project.backend.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ParkingServiceImpl implements ParkingService {

  private static final Random RANDOM = new Random();
  private static final Logger log = LoggerFactory.getLogger(ParkingServiceImpl.class);
  private final ParkingRecordRepository parkingRecordRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  private final PaymentLogRepository paymentLogRepository;
  private final UserRepository userRepository;
  private final RedisTemplate<String, String> redisTemplate;

  public ParkingServiceImpl(ParkingRecordRepository parkingRecordRepository,
      ParkingZoneRepository parkingZoneRepository,
      PaymentLogRepository paymentLogRepository,UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
    this.parkingRecordRepository = parkingRecordRepository;
    this.parkingZoneRepository = parkingZoneRepository;
    this.paymentLogRepository = paymentLogRepository;
    this.userRepository = userRepository;
    this.redisTemplate = redisTemplate;
  }

  @Override
  @Transactional
  public CheckInResponse checkIn(CheckInRequest request) {
    log.info("CheckIn vehicle with licensePlate {} : ", request.getLicensePlate());
    String licensePlate = request.getLicensePlate();
    String vehicleType = request.getVehicleType();
    validateInput(licensePlate, vehicleType);

    log.debug("Fetching all parking zones from database");
    List<ParkingZone> zones = parkingZoneRepository.findAll();
    if (zones.isEmpty()) {
      log.error("No parking zones found. Cannot proceed with check-in.");
      throw new RuntimeException("No parking zones available in the system");
    }

    ParkingZone selectedZone = null;
    for (int i = 0; i < zones.size(); i++) {
      log.info("Check zone in parking zone {} : ", zones.get(i).getId());
      ParkingZone zone = zones.get(RANDOM.nextInt(zones.size()));
      String redisKey = Constants.ZONE_COUNT_PREFIX + zone.getId() + ":current_count";
      log.debug("Retrieving current count from Redis for zone {} with key {}", zone.getId(), redisKey);
      String currentCountStr = redisTemplate.opsForValue().get(redisKey);
      int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
      log.debug("Zone {} has current count: {} and max slots: {}", zone.getId(), currentCount, zone.getMaxSlots());

      if (currentCount < zone.getMaxSlots()) {
        selectedZone = zone;
        log.info("Selected zone {} for check-in. Incrementing count in Redis", zone.getId());
        redisTemplate.opsForValue().increment(redisKey, 1);
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
    log.debug("Storing zone name in Redis with key: {}", redisParkingKey);
    redisTemplate.opsForValue().set(redisParkingKey, selectedZone.getZoneName());
    log.info("Saved vehicle successfully with license plate {} : ", licensePlate);

    return mapToResponse(savedRecord);
  }

  @Override
  @Transactional
  public CheckOutResponse checkOut(CheckOutRequest request) {
    log.info("Starting check-out process for vehicle with license plate: {}", request.getLicensePlate());

    if (request == null) {
      log.error("CheckOutRequest is null");
      throw new IllegalArgumentException("Check-out request cannot be null");
    }

    String licensePlate = request.getLicensePlate();
    String operatorId = request.getOperatorId();

    log.debug("Received check-out request: licensePlate={}, operatorId={}", licensePlate, operatorId);
    if (licensePlate == null) {
      log.error("License plate is null in check-out request");
      throw new IllegalArgumentException("License plate cannot be null");
    }
    if (operatorId == null) {
      log.error("Operator ID is null in check-out request");
      throw new IllegalArgumentException("Operator ID cannot be null");
    }

    // Tìm parking record
    log.debug("Fetching parking record for license plate: {}", licensePlate);
    ParkingRecord record = parkingRecordRepository.findByLicensePlate(licensePlate);
    if (record == null) {
      log.error("No active parking record found for vehicle: {}", licensePlate);
      throw new VehicleNotCheckedInException("No active parking record found for license plate " + licensePlate);
    }

    // Kiểm tra ticket trong Redis
    String redisParkingKey = Constants.PARKING_KEY_PREFIX + record.getId();
    log.debug("Checking Redis for parking ticket with key: {}", redisParkingKey);
    String zoneName = redisTemplate.opsForValue().get(redisParkingKey);
    if (zoneName == null) {
      log.error("No active parking ticket found for vehicle: {}", licensePlate);
      throw new InvalidTicketException("No active parking ticket found for " + licensePlate);
    }

    // Tìm user (operator)
    log.debug("Fetching operator with ID: {}", operatorId);
    User operator = userRepository.findById(operatorId)
        .orElseThrow(() -> {
          log.error("Operator with ID {} not found", operatorId);
          return new RuntimeException("Operator with ID " + operatorId + " not found");
        });

    // Cập nhật thời gian check-out
    LocalDateTime now = LocalDateTime.now();
    log.debug("Updating check-out time for record: {}", record.getId());
    record.setCheckOutTime(now);
    parkingRecordRepository.save(record);
    log.info("Check-out recorded for vehicle: {}", licensePlate);

    // Tính phí
    double fee = calculateFee(record);
    log.debug("Calculated fee for vehicle {}: {}", licensePlate, fee);

    // Lưu payment log
    PaymentLog paymentLog = new PaymentLog(record, fee, now, operator);
    log.debug("Saving payment log for record: {}", record.getId());
    PaymentLog savedPaymentLog = paymentLogRepository.save(paymentLog);
    log.info("Payment logged for vehicle: {} with fee: {}", licensePlate, fee);

    // Cập nhật Redis: giảm số xe trong zone
    String redisZoneKey = Constants.ZONE_COUNT_PREFIX + record.getZone().getId() + ":current_count";
    log.debug("Decrementing zone count in Redis with key: {}", redisZoneKey);
    redisTemplate.opsForValue().decrement(redisZoneKey, 1);

    // Xóa ticket khỏi Redis
    log.debug("Deleting parking ticket from Redis with key: {}", redisParkingKey);
    redisTemplate.delete(redisParkingKey);

    // Tạo response
    CheckOutResponse response = new CheckOutResponse(
        savedPaymentLog.getId(),
        savedPaymentLog.getParkingRecord().getId(),
        savedPaymentLog.getAmount(),
        savedPaymentLog.getPaymentTime(),
        savedPaymentLog.getOperator()
    );
    log.info("Check-out completed successfully for vehicle: {}", licensePlate);
    return response;
  }

  @Override
  public CheckInResponse findVehicle(String licensePlate) {
    log.info("Finding vehicle with license plate: {}", licensePlate);
    log.debug("Querying database for parking record with license plate: {}", licensePlate);
    ParkingRecord record = parkingRecordRepository.findByLicensePlate(licensePlate);
    if (record == null) {
      log.error("Vehicle not found with license plate: {}", licensePlate);
      throw new VehicleNotFoundException("Vehicle with license plate " + licensePlate + " not found");
    }
    log.info("Vehicle found with ID: {}", record.getId());
    return mapToResponse(record);
  }

  @Override
  public StatsResponse getStats(LocalDateTime start, LocalDateTime end) {
    log.info("Fetching statistics from {} to {}", start, end);
    log.debug("Counting parking records between {} and {}", start, end);
    long count = parkingRecordRepository.countByCheckInTimeBetween(start, end);
    log.debug("Calculating revenue between {} and {}", start, end);
    double revenue = getRevenue(start, end);
    log.debug("Calculating average parking time between {} and {}", start, end);
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

  private void validateInput(String licensePlate, String vehicleType) {
    if (licensePlate == null || !licensePlate.matches(Constants.LICENSE_PLATE_REGEX)) {
      log.error("Invalid license plate format: {}", licensePlate);
      throw new InvalidInputException("Invalid license plate format");
    }
    if (vehicleType == null || (!vehicleType.equals(Constants.VEHICLE_TYPE_CAR) && !vehicleType.equals(Constants.VEHICLE_TYPE_MOTORBIKE))) {
      log.error("Invalid vehicle type: {}", vehicleType);
      throw new InvalidInputException("Vehicle type must be " + Constants.VEHICLE_TYPE_CAR + " or " + Constants.VEHICLE_TYPE_MOTORBIKE);
    }
    log.info("Validated input successfully for vehicle: {}", licensePlate);
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

  private double calculateFee(ParkingRecord record) {
    log.debug("Calculating fee for parking record: {}", record.getId());
    String vehicleType = record.getVehicleType();
    LocalDateTime checkInTime = record.getCheckInTime();
    LocalDateTime checkOutTime = record.getCheckOutTime();

    boolean isOvernight = isOvernight(checkInTime, checkOutTime);
    log.debug("Is overnight parking? {}", isOvernight);

    double fee;
    if (vehicleType.equals(Constants.VEHICLE_TYPE_MOTORBIKE)) {
      fee = isOvernight ? Constants.MOTORBIKE_OVERNIGHT_FEE : Constants.MOTORBIKE_FEE;
    } else if (vehicleType.equals(Constants.VEHICLE_TYPE_CAR)) {
      fee = isOvernight ? Constants.CAR_OVERNIGHT_FEE : Constants.CAR_FEE;
    } else {
      fee = 0.0;
    }
    log.debug("Fee calculated: {} for vehicle type: {}", fee, vehicleType);
    return fee;
  }

  private boolean isOvernight(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
    if (checkInTime == null || checkOutTime == null) {
      log.debug("Check-in or check-out time is null, not overnight");
      return false;
    }
    boolean checkInBefore6PM = checkInTime.getHour() < Constants.OVERNIGHT_CHECK_IN_HOUR;
    boolean checkOutAfter6AMNextDay = checkOutTime.isAfter(checkInTime.plusDays(1).withHour(Constants.OVERNIGHT_CHECK_OUT_HOUR).withMinute(0).withSecond(0));
    boolean isOvernight = checkInBefore6PM && checkOutAfter6AMNextDay;
    log.debug("Overnight check: checkInBefore6PM={}, checkOutAfter6AMNextDay={}, result={}", checkInBefore6PM, checkOutAfter6AMNextDay, isOvernight);
    return isOvernight;
  }
}

