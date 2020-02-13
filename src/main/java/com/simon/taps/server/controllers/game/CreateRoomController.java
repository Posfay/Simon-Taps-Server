package com.simon.taps.server.controllers.game;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.DatabaseUtil;
import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.database.User;
import com.simon.taps.server.database.UserRepository;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;
import com.simon.taps.server.wrappers.PostRequestWrapper;

@RestController
public class CreateRoomController {

  @Autowired
  private DatabaseUtil databaseUtil;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserRepository userRepository;

  private HashMap<String, Object> craftResponse(final long playersInRoom) {

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.NUMBER_OF_PLAYERS, playersInRoom);

    return responseMap;
  }

  @PostMapping("/create")
  public HashMap<String, Object> createRoom(@Valid @RequestBody final PostRequestWrapper postBody,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    boolean roomExists = this.roomRepository.existsById(postBody.getRoomId());

    if (roomExists) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.ROOM_ALREADY_EXISTS);
    }

    Room newRoom = this.databaseUtil.createDefaultRoom();
    newRoom.setId(postBody.getRoomId());

    Player newPlayer = this.databaseUtil.createDefaultPlayer();
    newPlayer.setId(postBody.getPlayerId());
    newPlayer.setRoomId(postBody.getRoomId());

    boolean existsUser = this.userRepository.existsById(newPlayer.getId());

    // create user in User table
    if (!existsUser) {

      User newUser = this.databaseUtil.createDefaultUser();
      newUser.setId(newPlayer.getId());

      this.userRepository.save(newUser);
    }

    this.databaseUtil.saveRoomAndPlayer(newRoom, newPlayer);

    this.databaseUtil.garbageCollection();

    return craftResponse(1);
  }

}
