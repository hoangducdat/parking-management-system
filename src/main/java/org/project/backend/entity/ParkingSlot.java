package org.project.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "zone_id", nullable = false)
  private ParkingZone zone;

  @Column(name = "is_occupied", nullable = false)
  private boolean isOccupied;

  public ParkingSlot() {
  }

  public ParkingSlot(String id, ParkingZone zone, boolean isOccupied) {
    this.id = id;
    this.zone = zone;
    this.isOccupied = isOccupied;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ParkingZone getZone() {
    return zone;
  }

  public void setZone(ParkingZone zone) {
    this.zone = zone;
  }

  public boolean isOccupied() {
    return isOccupied;
  }

  public void setOccupied(boolean occupied) {
    isOccupied = occupied;
  }
}
