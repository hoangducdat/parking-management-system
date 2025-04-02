package org.project.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_logs")
@Getter
@Setter
public class PaymentLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parking_record_id", nullable = false)
  private ParkingRecord parkingRecord;

  @Column(name = "amount", nullable = false)
  private double amount;

  @Column(name = "payment_time", nullable = false)
  private LocalDateTime paymentTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "operator_id", nullable = false)
  private User operator;

  public PaymentLog(ParkingRecord record, double fee, LocalDateTime now, User operator) {
    this.parkingRecord = record;
    this.amount = fee;
    this.paymentTime = now;
  }

  public PaymentLog() {

  }
}
