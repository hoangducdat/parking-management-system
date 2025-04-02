package org.project.backend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.project.backend.entity.User;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckOutResponse {

  private String id;
  private String parkingRecordId;
  private double amount;
  private LocalDateTime paymentTime;
  private User operator;

  public CheckOutResponse() {
  }

  public CheckOutResponse(String id, String parkingRecordId, double amount, LocalDateTime paymentTime, User operator) {
    this.id = id;
    this.parkingRecordId = parkingRecordId;
    this.amount = amount;
    this.paymentTime = paymentTime;
    this.operator = operator;
  }
}
