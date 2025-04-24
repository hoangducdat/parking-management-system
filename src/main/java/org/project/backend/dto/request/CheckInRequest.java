package org.project.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.project.backend.constant.Constants;

@Getter
@Setter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckInRequest {

  @NotBlank(message = "License plate cannot be blank")
  @Pattern(regexp = Constants.LICENSE_PLATE_REGEX, message = "Invalid license plate format")
  private String licensePlate;

  @NotBlank(message = "Vehicle type cannot be blank")
  @Pattern(regexp = "CAR|MOTORBIKE", message = "Vehicle type must be CAR or MOTORBIKE")
  private String vehicleType;
}

