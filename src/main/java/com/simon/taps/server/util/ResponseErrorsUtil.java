package com.simon.taps.server.util;

import java.util.HashMap;

public class ResponseErrorsUtil {

  public static enum Error {
    FORBIDDEN, INTERNAL_ERROR, INVALID_COUPON, INVALID_COUPON_NUMBER, NO_SUCH_COUPON, NOT_LEFT, ROOM_ALREADY_EXISTS, ROOM_DOES_NOT_EXIST, ROOM_IS_FULL, USER_DOES_NOT_EXIST
  }

  public static HashMap<String, Object> errorResponse(final Error error) {

    HashMap<String, Object> map = new HashMap<>();

    map.put(ServerUtil.STATUS, ServerUtil.ERROR);
    map.put(ServerUtil.REASON, error.toString());

    return map;
  }

  private ResponseErrorsUtil() {
  }

}
