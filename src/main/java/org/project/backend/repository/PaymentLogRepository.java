package org.project.backend.repository;

import org.project.backend.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, String> {
  @Query("SELECT SUM(pl.amount) FROM PaymentLog pl WHERE pl.paymentTime >= :start AND pl.paymentTime <= :end")
  Double sumFeesByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);
}
