package com.simon.taps.server.controllers;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;

@RestController
public class HttpController {

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

  @GetMapping("/rooms/{roomId}")
  public Room getRoomById(@PathVariable final String roomId) {

    return this.roomRepository.findById(roomId).get();
  }

  @GetMapping("/json")
  public HashMap<String, Object> getTestJson() {

    HashMap<String, Object> map = new HashMap<>();

    map.put("game_state", "waiting");
    map.put("ready", true);
    map.put("number_of_players", 42);

    return map;
  }

}
