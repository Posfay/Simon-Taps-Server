package com.simon.taps.server.controllers;

import java.time.LocalDateTime;
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
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.wrappers.PostRequestWrapper;

@RestController
public class JoinController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  private HashMap<String, Object> craftResponse(final long playersInRoom) {

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put("status", "OK");
    responseMap.put("number_of_players", playersInRoom);

    return responseMap;
  }

  @PostMapping("/join")
  public HashMap<String, Object> postJoin(@Valid @RequestBody final PostRequestWrapper postBody) {

    boolean roomExists = this.roomRepository.existsById(postBody.getRoomId());

    // -----------------------------player join-----------------------------------------------------
    if (roomExists) {

      long playersInRoom = this.playerRepository.findByRoomId(postBody.getRoomId()).size();

      if (playersInRoom >= 4) {
        return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.ROOM_IS_FULL);
      }

      Player newPlayer = new Player();
      newPlayer.setId(postBody.getPlayerId());
      newPlayer.setRoomId(postBody.getRoomId());
      newPlayer.setTileId(null);
      newPlayer.setReady(false);

      this.playerRepository.save(newPlayer);

      if (++playersInRoom >= 4) {

        Room room = this.roomRepository.findById(postBody.getRoomId()).get();
        room.setState(GameUtil.PREPARING);
        room.setTimer(LocalDateTime.now());

        this.roomRepository.save(room);
      }

      return craftResponse(playersInRoom);
    }

    // -----------------------------new room--------------------------------------------------------
    Room newRoom = new Room();
    newRoom.setId(postBody.getRoomId());
    newRoom.setPattern("");
    newRoom.setPatternCompleted("");
    newRoom.setState(GameUtil.WAITING);
    newRoom.setTimer(LocalDateTime.now());

    this.roomRepository.save(newRoom);

    Player newPlayer = new Player();
    newPlayer.setId(postBody.getPlayerId());
    newPlayer.setRoomId(postBody.getRoomId());
    newPlayer.setTileId(null);
    newPlayer.setReady(false);

    this.playerRepository.save(newPlayer);

    return craftResponse(1);
  }

}
