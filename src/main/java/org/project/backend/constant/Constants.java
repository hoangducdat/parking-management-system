package org.project.backend.constant;

import java.util.Arrays;
import java.util.List;

public final class Constants {

  private Constants() {
  }
  public static final String PARKING_KEY_PREFIX = "parking:";
  public static final List<String> ZONES = Arrays.asList("A", "B", "C", "D", "E");
  public static final String ZONE_COUNT_PREFIX = "zone:";

  public static final double MOTORBIKE_FEE = 5000.0;
  public static final double CAR_FEE = 10000.0;

  public static final double MOTORBIKE_OVERNIGHT_FEE = 10000.0;
  public static final double CAR_OVERNIGHT_FEE = 20000.0;

  public static final String VEHICLE_TYPE_CAR = "CAR";
  public static final String VEHICLE_TYPE_MOTORBIKE = "MOTORBIKE";

  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_STAFF = "STAFF";

  public static final String LICENSE_PLATE_REGEX = "[0-9A9-Z-]+";

  public static final int OVERNIGHT_CHECK_IN_HOUR = 18;
  public static final int OVERNIGHT_CHECK_OUT_HOUR = 6;

}