package com.simon.taps.server.controllers;

import java.util.HashMap;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.controllers.game.JoinController;
import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.database.UserRepository;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;

@RestController
public class HttpController {

  Logger logger = LoggerFactory.getLogger(JoinController.class);

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Value("${testprop}")
  private String test3;

  @Autowired
  private UserRepository userRepository;

  @PostMapping("/players")
  public Player createPlayer(@Valid @RequestBody final Player player) {

    return this.playerRepository.save(player);
  }

  @GetMapping("/players")
  public HashMap<String, Object> getPlayers(@RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
      required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("players", this.playerRepository.findAll());

    return responseMap;
  }

  @GetMapping("/rooms")
  public HashMap<String, Object> getRooms(@RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
      required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("rooms", this.roomRepository.findAll());

    return responseMap;
  }

  @GetMapping("/users")
  public HashMap<String, Object> getUsers(@RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
      required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("users", this.userRepository.findAll());

    return responseMap;
  }

  @GetMapping("/test3")
  public String test3() {

    return this.test3;
  }

}
