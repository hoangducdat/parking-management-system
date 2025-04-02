package org.project.backend.repository;

import org.project.backend.entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, String> {

  ParkingZone findByZoneName(String zoneName);
}
