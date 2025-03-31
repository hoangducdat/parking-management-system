package org.project.backend.constant;

import java.util.Arrays;
import java.util.List;

public final class Constants {

  private Constants() {
  }

  public static final String PARKING_KEY_PREFIX = "parking:";
  public static final List<String> ZONES = Arrays.asList("A", "B", "C", "D", "E");
  public static final double HOURLY_RATE = 5000.0;
  public static final String VEHICLE_TYPE_CAR = "CAR";
  public static final String VEHICLE_TYPE_MOTORBIKE = "MOTORBIKE";
  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_STAFF = "STAFF";
  public static final String LICENSE_PLATE_REGEX = "[0-9A-Z-]+";

}