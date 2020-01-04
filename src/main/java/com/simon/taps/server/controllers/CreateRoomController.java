package com.simon.taps.server.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.DatabaseUtil;
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
  private DatabaseUtil databaseUtil;

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

    this.databaseUtil.saveRoomAndPlayer(newRoom, newPlayer);

    roomGarbageCollection();

    return craftResponse(1);
  }

  public void roomGarbageCollection() {

    List<Room> rooms = this.roomRepository.findAll();

    for (Room room : rooms) {

      LocalDateTime nowMinusIdle = LocalDateTime.now().minusMinutes(GameUtil.MAX_ROOM_IDLE_MINUTES);

      if (room.getTimer().isBefore(nowMinusIdle)) {

        List<Player> playersOfRoom = this.playerRepository.findByRoomId(room.getId());
        this.playerRepository.deleteAll(playersOfRoom);
        this.roomRepository.delete(room);
      }
    }
  }
}
