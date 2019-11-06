package com.simon.taps.server.controllers;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.util.GameUtil;
import com.simon.taps.server.wrappers.PostRequestWrapper;

@RestController
public class StartController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @PostMapping("/start")
  public HashMap<String, Object> postStart(@Valid @RequestBody final PostRequestWrapper postBody) {

    Player currentPlayer = this.playerRepository.findById(postBody.getPlayerId()).get();

    currentPlayer.setReady(true);

    currentPlayer = this.playerRepository.save(currentPlayer);

    List<Player> playersInRoom = this.playerRepository.findByRoomId(postBody.getRoomId());

    long countOfReady = 0;

    for (Player player : playersInRoom) {
      if (player.getReady()) {
        countOfReady++;
      }
    }

    if (countOfReady == 4) {

      Room room = this.roomRepository.findById(postBody.getRoomId()).get();
      room.setState(GameUtil.PLAYING);

      this.roomRepository.save(room);
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("status", "OK");

    return responseMap;
  }

}
