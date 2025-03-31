package org.project.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "parking_zones")
public class ParkingZone {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "zone_name", nullable = false, unique = true)
  private String zoneName;

  @Column(name = "max_slots", nullable = false)
  private int maxSlots;

  public ParkingZone() {
  }

  public ParkingZone(String id, String zoneName, int maxSlots) {
    this.id = id;
    this.zoneName = zoneName;
    this.maxSlots = maxSlots;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getZoneName() {
    return zoneName;
  }

  public void setZoneName(String zoneName) {
    this.zoneName = zoneName;
  }

  public int getMaxSlots() {
    return maxSlots;
  }

  public void setMaxSlots(int maxSlots) {
    this.maxSlots = maxSlots;
  }
}