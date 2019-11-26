package com.simon.taps.server.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
public class CreateRoomController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  private HashMap<String, Object> craftResponse(final long playersInRoom) {

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put("status", "OK");
    responseMap.put("numberOfPlayers", playersInRoom);

    return responseMap;
  }

  @PostMapping("/create")
  public HashMap<String, Object> createRoom(@Valid @RequestBody final PostRequestWrapper postBody) {

    boolean roomExists = this.roomRepository.existsById(postBody.getRoomId());

    if (roomExists) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.ROOM_ALREADY_EXISTS);
    }

    Room newRoom = new Room();
    newRoom.setId(postBody.getRoomId());
    newRoom.setPattern("");
    newRoom.setPatternCompleted("");
    newRoom.setState(GameUtil.WAITING);
    newRoom.setTimer(LocalDateTime.now());

    Player newPlayer = new Player();
    newPlayer.setId(postBody.getPlayerId());
    newPlayer.setRoomId(postBody.getRoomId());
    newPlayer.setTileId(null);
    newPlayer.setReady(false);

    saveRoomAndPlayer(newRoom, newPlayer);

    return craftResponse(1);
  }

  @Transactional
  public void saveRoomAndPlayer(final Room room, final Player newPlayer) {

    this.roomRepository.save(room);
    this.playerRepository.save(newPlayer);
  }
}
