package com.simon.taps.server.controllers;

import java.util.HashMap;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.ServerApplication;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;

@RestController
public class RestartApplicationController {

  @PostMapping("/restart-application")
  public HashMap<String, Object> restart(@RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
      required = false) final String authKey) {

    if (!ServerUtil.RESTART_APPLICATION_PASSWORD.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    ServerApplication.restart();

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("restart", "starting application...");

    return responseMap;
  }

}
