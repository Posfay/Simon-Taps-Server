package com.simon.taps.server.controllers;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.util.GameUtil;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;
import com.simon.taps.server.wrappers.LeavePostRequestWrapper;

@RestController
public class LeaveController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @PostMapping("/leave")
  public HashMap<String, Object> leaveRoom(
      @Valid @RequestBody final LeavePostRequestWrapper postBody,
      @RequestHeader(ServerUtil.AUTHENTICATION_HEADER) final String authKey) {

    if (!authKey.equals(ServerUtil.AUTHENTICATION_KEY)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    Player player = this.playerRepository.findById(postBody.getPlayerId()).get();
    Room room = this.roomRepository.findById(player.getRoomId()).get();

    if (room.getState().equals(GameUtil.WAITING)) {

      this.playerRepository.delete(player);
      HashMap<String, Object> responseMap = new HashMap<>();
      responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
      responseMap.put(ServerUtil.LEFT, true);
      return responseMap;
    }

    return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.NOT_LEFT);
  }

}
