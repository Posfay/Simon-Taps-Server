package com.simon.taps.server.controllers;

import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;

@RestController
public class VersionCheckController {

  @GetMapping("/version/{version}")
  public HashMap<String, Object> checkVersion(@PathVariable final String version,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    String currentVersion = version;
    int majorCurrent = Integer.valueOf(currentVersion.substring(0, currentVersion.indexOf(".")));
    currentVersion = currentVersion.substring(currentVersion.indexOf(".") + 1);
    int minorCurrent = Integer.valueOf(currentVersion.substring(0, currentVersion.indexOf(".")));
    currentVersion = currentVersion.substring(currentVersion.indexOf(".") + 1);
    int patchCurrent = Integer.valueOf(currentVersion);

    String minVersion = ServerUtil.MIN_COMPATIBLE_VERSION;
    int majorMin = Integer.valueOf(minVersion.substring(0, minVersion.indexOf(".")));
    minVersion = minVersion.substring(minVersion.indexOf(".") + 1);
    int minorMin = Integer.valueOf(minVersion.substring(0, minVersion.indexOf(".")));
    minVersion = minVersion.substring(minVersion.indexOf(".") + 1);
    int patchMin = Integer.valueOf(minVersion);

    boolean compatible = false;

    if ((majorCurrent > majorMin) || ((majorCurrent == majorMin) && (minorCurrent > minorMin))
        || ((majorCurrent == majorMin) && (minorCurrent == minorMin)
            && (patchCurrent >= patchMin))) {
      compatible = true;
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);

    if (compatible) {

      responseMap.put(ServerUtil.COMPATIBLE_VERSION, true);
    } else {

      responseMap.put(ServerUtil.COMPATIBLE_VERSION, false);
    }

    return responseMap;
  }

}
