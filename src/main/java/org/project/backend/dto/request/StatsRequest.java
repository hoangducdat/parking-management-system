package org.project.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StatsRequest {

  @NotNull(message = "Start date cannot be null")
  private LocalDate start;

  @NotNull(message = "End date cannot be null")
  private LocalDate end;

  public StatsRequest() {
  }

  public StatsRequest(LocalDate start, LocalDate end) {
    this.start = start;
    this.end = end;
  }
}
