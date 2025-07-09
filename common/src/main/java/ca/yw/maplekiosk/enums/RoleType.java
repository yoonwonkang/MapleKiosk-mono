package ca.yw.maplekiosk.enums;

import java.util.HashMap;
import java.util.Map;

public enum RoleType {
  SHOP, KIOSK;

  private static final Map<String, RoleType> TYPE_MAP = new HashMap<>();

  static {
    for (RoleType tokenType : values()) {
      TYPE_MAP.put(tokenType.name().toLowerCase(), tokenType);
    }
  }

  public static RoleType fromString(String type) {
    if (type == null) return null;
    return TYPE_MAP.get(type.toLowerCase());
  }

  public static boolean isValid(String type) {
    return !(RoleType.fromString(type) == null);
  }
}
