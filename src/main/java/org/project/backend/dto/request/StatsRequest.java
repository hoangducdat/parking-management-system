package org.project.backend.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StatsRequest {

  @NotNull(message = "Start time cannot be null")
  private LocalDateTime start;

  @NotNull(message = "End time cannot be null")
  private LocalDateTime end;

  public StatsRequest() {
  }

  public StatsRequest(LocalDateTime start, LocalDateTime end) {
    this.start = start;
    this.end = end;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  public void setEnd(LocalDateTime end) {
    this.end = end;
  }
}
