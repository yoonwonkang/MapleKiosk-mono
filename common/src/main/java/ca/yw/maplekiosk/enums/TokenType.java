package ca.yw.maplekiosk.enums;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
  SHOP, KIOSK;

  private static final Map<String, TokenType> TYPE_MAP = new HashMap<>();

  static {
    for (TokenType tokenType : values()) {
      TYPE_MAP.put(tokenType.name().toLowerCase(), tokenType);
    }
  }

  public static TokenType fromString(String type) {
    if (type == null) return null;
    return TYPE_MAP.get(type.toLowerCase());
  }

  public static boolean isValid(String type) {
    return !(TokenType.fromString(type) == null);
  }
}
