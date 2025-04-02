package org.project.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.project.backend.entity.User;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckOutRequest {

  @NotBlank(message = "License plate cannot be blank")
  private String licensePlate;

  @NotBlank(message = "Operator ID cannot be blank")
  private String operatorId;

  public CheckOutRequest() {
  }

  public CheckOutRequest(String licensePlate, String operatorId) {
    this.licensePlate = licensePlate;
    this.operatorId = operatorId;
  }
}
