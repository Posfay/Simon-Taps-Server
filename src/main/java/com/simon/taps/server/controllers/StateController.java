package com.simon.taps.server.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

@RestController
public class StateController {

  @Autowired
  private DatabaseUtil databaseUtil;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  private boolean checkPlayerAndRoomExists(final String roomId,
      final String playerId) {

    if (!this.roomRepository.existsById(roomId) || !this.playerRepository.existsById(playerId)) {
      return false;
    }

    return true;
  }

  private HashMap<String, Object> craftResponse(final String state) {

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.GAME_STATE, state);

    return responseMap;
  }

  private HashMap<String, Object> failEndState() {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.FAIL_END);
    return responseMap;
  }

  private void generatePattern(final String roomId) {

    List<Long> pattern = new ArrayList<>();
    for (int i = 0; i < ((GameUtil.MAX_PATTERN_LENGTH / 4) + 1); i++) {
      pattern.add(1L);
      pattern.add(2L);
      pattern.add(3L);
      pattern.add(4L);
    }
    Collections.shuffle(pattern);

    String patternStr = "";

    for (int i = 0; i < GameUtil.MAX_PATTERN_LENGTH; i++) {
      patternStr += pattern.remove(0).toString();
    }

    Room room = this.roomRepository.findById(roomId).get();

    // mar generalva lett a pattern
    if (!room.getPattern().equals("")) {
      return;
    }

    room.setPattern(patternStr);
    this.roomRepository.save(room);
  }

  @GetMapping("/state/{roomId}/{playerId}")
  public HashMap<String, Object> getState(@PathVariable final String roomId,
      @PathVariable final String playerId,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    if (!checkPlayerAndRoomExists(roomId, playerId)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.INTERNAL_ERROR);
    }

    Room room = this.roomRepository.findById(roomId).get();

    // ----------------------------------WAITING----------------------------------------------------
    if (room.getState().equals(GameUtil.WAITING)) {

      return waitingState(roomId);
    }

    // ----------------------------------PREPARING--------------------------------------------------
    if (room.getState().equals(GameUtil.PREPARING)) {

      return preparingState(room, playerId);
    }

    // ----------------------------------SHOWING_PATTERN--------------------------------------------
    if (room.getState().equals(GameUtil.SHOWING_PATTERN)) {

      return showingPatternState(room);
    }

    // ----------------------------------PLAYING----------------------------------------------------
    if (room.getState().equals(GameUtil.PLAYING)) {

      return playingState(room);
    }

    // ----------------------------------FAIL_END---------------------------------------------------
    if (room.getState().equals(GameUtil.FAIL_END)) {

      return failEndState();
    }

    // ----------------------------------SUCCESSFUL_END---------------------------------------------
    if (room.getState().equals(GameUtil.SUCCESSFUL_END)) {

      return successfulEndState();
    }

    return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.INTERNAL_ERROR);
  }

  private HashMap<String, Object> playingState(final Room room) {

    LocalDateTime nowMinusSec = LocalDateTime.now().minusSeconds(GameUtil.WAIT_BETWEEN_2_CLICKS);
    LocalDateTime timerStart = room.getTimer();

    if (nowMinusSec.isAfter(timerStart)) {

      room.setState(GameUtil.FAIL_END);
      this.roomRepository.save(room);

      HashMap<String, Object> responseMap = craftResponse(GameUtil.FAIL_END);
      return responseMap;
    }

    HashMap<String, Object> responseMap = craftResponse(GameUtil.PLAYING);
    return responseMap;
  }

  private HashMap<String, Object> preparingState(Room room, final String playerId) {

    LocalDateTime nowMinusSec = LocalDateTime.now().minusSeconds(GameUtil.WAIT_IN_PREPARING_SEC);
    LocalDateTime timerStart = room.getTimer();

    // TO SHOWING_PATTERN
    if (nowMinusSec.isAfter(timerStart)) {

      try {
        generatePattern(room.getId());
      } catch (Exception ignore) {
        // optimistic locking -> go forward
      }

      room = this.roomRepository.findById(room.getId()).get();
      room.setState(GameUtil.SHOWING_PATTERN);
      room = this.roomRepository.save(room);

      long round = room.getRound();

      HashMap<String, Object> responseMap = craftResponse(GameUtil.SHOWING_PATTERN);
      responseMap.put(ServerUtil.PATTERN, room.getPattern().substring(0, (int) round));
      return responseMap;
    }

    Player player = this.playerRepository.findById(playerId).get();

    if (player.getTileId() == null) {

      try {
        this.databaseUtil.generateTileIds(room.getId(), playerId);
      } catch (Exception ignore) {
        // optimistic locking -> go forward
      }
    }

    player = this.playerRepository.findById(playerId).get();

    HashMap<String, Object> responseMap = craftResponse(GameUtil.PREPARING);
    responseMap.put(ServerUtil.TILE_ID, player.getTileId());
    return responseMap;
  }

  private HashMap<String, Object> showingPatternState(final Room room) {

    long round = room.getRound();
    HashMap<String, Object> responseMap = craftResponse(GameUtil.SHOWING_PATTERN);
    responseMap.put(ServerUtil.PATTERN, room.getPattern().substring(0, (int) round));
    return responseMap;
  }

  private HashMap<String, Object> successfulEndState() {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.SUCCESSFUL_END);
    return responseMap;
  }

  private HashMap<String, Object> waitingState(final String roomId) {

    while (true) {

      Room room = this.roomRepository.findById(roomId).get();
      long playersInRoom = this.playerRepository.findByRoomId(roomId).size();

      if (playersInRoom >= 4) {

        room.setState(GameUtil.PREPARING);

        try {
          this.roomRepository.save(room);
        } catch (Exception ignore) {
          // optimistic locking -> go forward
          continue;
        }
      }

      break;
    }

    HashMap<String, Object> responseMap = craftResponse(GameUtil.WAITING);
    responseMap.put(ServerUtil.NUMBER_OF_PLAYERS,
        this.playerRepository.findByRoomId(roomId).size());
    return responseMap;
  }
}
