package org.project.backend.repository;

import org.project.backend.entity.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, String> {
  @Query("SELECT pr FROM ParkingRecord pr WHERE pr.licensePlate = :licensePlate AND pr.checkOutTime IS NULL")
  ParkingRecord findByLicensePlate(String licensePlate);

  @Query("SELECT COUNT(pr) FROM ParkingRecord pr WHERE pr.checkInTime >= :start AND pr.checkInTime <= :end")
  long countByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);
}
