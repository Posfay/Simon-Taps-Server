package com.simon.taps.server.controllers;

import java.util.HashMap;

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
import com.simon.taps.server.wrappers.LeavePostRequestWrapper;

@RestController
public class LeaveController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @PostMapping("/leave")
  public HashMap<String, Object> leaveRoom(
      @Valid @RequestBody final LeavePostRequestWrapper postBody) {

    Player player = this.playerRepository.findById(postBody.getPlayerId()).get();
    Room room = this.roomRepository.findById(player.getRoomId()).get();

    if (room.getState().equals(GameUtil.WAITING)) {

      this.playerRepository.delete(player);
      HashMap<String, Object> responseMap = new HashMap<>();
      responseMap.put("status", "OK");
      return responseMap;
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put("status", "NOT_LEFT");
    return responseMap;
  }

}
