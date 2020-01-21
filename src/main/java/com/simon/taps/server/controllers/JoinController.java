package com.simon.taps.server.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.persistence.OptimisticLockException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.DatabaseUtil;
import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.util.GameUtil;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;
import com.simon.taps.server.wrappers.PostRequestWrapper;

@RestController
public class JoinController {

  @Autowired
  private DatabaseUtil databaseUtil;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  private HashMap<String, Object> craftResponse(final long playersInRoom) {

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.NUMBER_OF_PLAYERS, playersInRoom);

    return responseMap;
  }

  @PostMapping("/join")
  public HashMap<String, Object> postJoin(@Valid @RequestBody final PostRequestWrapper postBody,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    boolean roomExists = this.roomRepository.existsById(postBody.getRoomId());

    if (!roomExists) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.ROOM_DOES_NOT_EXIST);
    }

    long playersInRoom = 0;

    while (true) {

      playersInRoom = this.playerRepository.findByRoomId(postBody.getRoomId()).size();
      Room room = this.roomRepository.findById(postBody.getRoomId()).get();

      if (playersInRoom >= 4) {
        return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.ROOM_IS_FULL);
      }

      Player newPlayer = this.databaseUtil.createDefaultPlayer();
      newPlayer.setId(postBody.getPlayerId());
      newPlayer.setRoomId(postBody.getRoomId());

      if (++playersInRoom >= 4) {

        room.setState(GameUtil.PREPARING);
        room.setTimer(LocalDateTime.now());
      }

      try {
        this.databaseUtil.saveRoomAndPlayer(room, newPlayer);
      } catch (OptimisticLockException ignore) {
        continue;
      }

      break;
    }

    return craftResponse(playersInRoom);
  }

}
