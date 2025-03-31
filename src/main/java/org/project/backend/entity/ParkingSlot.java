package org.project.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "zone", nullable = false)
  private String zone;

  @Column(name = "is_occupied", nullable = false)
  private boolean isOccupied;

  @Column(name = "total_slots", nullable = false)
  private int totalSlots;

  public ParkingSlot() {
  }

  public ParkingSlot(String id, String zone, boolean isOccupied, int totalSlots) {
    this.id = id;
    this.zone = zone;
    this.isOccupied = isOccupied;
    this.totalSlots = totalSlots;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public boolean isOccupied() {
    return isOccupied;
  }

  public void setOccupied(boolean occupied) {
    isOccupied = occupied;
  }

  public int getTotalSlots() {
    return totalSlots;
  }

  public void setTotalSlots(int totalSlots) {
    this.totalSlots = totalSlots;
  }
}
