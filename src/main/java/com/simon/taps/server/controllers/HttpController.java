package com.simon.taps.server.controllers;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;

@RestController
public class HttpController {

  Logger logger = LoggerFactory.getLogger(JoinController.class);

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @PostMapping("/players")
  public Player createPlayer(@Valid @RequestBody final Player player) {

    return this.playerRepository.save(player);
  }

  @GetMapping("/players")
  public List<Player> getPlayer() {

    return this.playerRepository.findAll();
  }

  @GetMapping("/players/{playerId}")
  public Player getPlayerById(@PathVariable final String playerId) {

    return this.playerRepository.findById(playerId).get();
  }

  @GetMapping("/test2/{roomId}")
  public HashMap<String, Object> getPlayersInRoom(@PathVariable final String roomId) {

    HashMap<String, Object> map = new HashMap<>();

    List<Player> players = this.playerRepository.findByRoomId(roomId);

    map.put("size", players.size());
    map.put("players", players);

    return map;
  }

  @GetMapping("/rooms/{roomId}")
  public Room getRoomById(@PathVariable final String roomId) {

    return this.roomRepository.findById(roomId).get();
  }

  @GetMapping("/json")
  public HashMap<String, Object> getTestJson(
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    HashMap<String, Object> map = new HashMap<>();

    map.put("gameState", "waiting");
    map.put("ready", true);
    map.put("numberOfPlayers", 42);

    return map;
  }

  @PostMapping("/json")
  public HashMap<String, Object> getTestJson2(@Valid @RequestBody final String request) {

    this.logger.info(request);

    HashMap<String, Object> map = new HashMap<>();

    map.put("gameState", "waiting");
    map.put("ready", true);
    map.put("numberOfPlayers", 42);

    return map;
  }

}
