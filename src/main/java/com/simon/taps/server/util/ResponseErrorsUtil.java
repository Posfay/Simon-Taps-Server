package com.simon.taps.server.util;

import java.util.HashMap;

public class ResponseErrorsUtil {

  public static enum Error {
    INTERNAL_ERROR, ROOM_IS_FULL
  }

  public static HashMap<String, Object> errorResponse(final Error error) {

    HashMap<String, Object> map = new HashMap<>();

    map.put("status", "ERROR");
    map.put("reason", error.toString());

    return map;
  }

  private ResponseErrorsUtil() {
  }

}
