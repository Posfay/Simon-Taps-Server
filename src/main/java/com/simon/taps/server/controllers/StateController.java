package com.simon.taps.server.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Player;
import com.simon.taps.server.database.PlayerRepository;
import com.simon.taps.server.database.Room;
import com.simon.taps.server.database.RoomRepository;
import com.simon.taps.server.util.GameUtil;
import com.simon.taps.server.util.ResponseErrorsUtil;

@RestController
public class StateController {

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

    responseMap.put("status", "OK");
    responseMap.put("game_state", state);

    return responseMap;
  }

  private HashMap<String, Object> failEndState() {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.FAIL_END);
    return responseMap;
  }

  private void generatePattern(final String roomId) {

    List<Integer> pattern = new ArrayList<>();
    for (int i = 0; i < GameUtil.ROUND_LENGTH; i++) {
      pattern.add(1);
      pattern.add(2);
      pattern.add(3);
      pattern.add(4);
    }
    Collections.shuffle(pattern);

    String patternStr = "";

    for (int i = 0; i < (4 * GameUtil.ROUND_LENGTH); i++) {
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

  private void generateTileIds(final String roomId, final String playerId) {

    List<Player> playersInRoom = this.playerRepository.findByRoomId(roomId);
    List<Integer> tileIds = new ArrayList<>();
    tileIds.add(1);
    tileIds.add(2);
    tileIds.add(3);
    tileIds.add(4);
    Collections.shuffle(tileIds);

    for (Player player : playersInRoom) {

      // mar generalva lett
      if (!player.getTileId().equals(null)) {
        return;
      }

      player.setTileId(tileIds.remove(0).toString());
      this.playerRepository.save(player);
    }
  }

  @GetMapping("/state/{roomId}/{playerId}")
  public HashMap<String, Object> getState(@PathVariable final String roomId,
      @PathVariable final String playerId) {

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

      return playingState();
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

  private HashMap<String, Object> playingState() {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.PLAYING);
    return responseMap;
  }

  private HashMap<String, Object> preparingState(Room room, final String playerId) {

    LocalDateTime nowMinusSec = LocalDateTime.now().minusSeconds(GameUtil.WAIT_IN_PREPARING_SEC);
    LocalDateTime timerStart = room.getTimer();

    // TO SHOWING_PATTERN
    if (nowMinusSec.isAfter(timerStart)) {

      generatePattern(room.getId());

      room = this.roomRepository.findById(room.getId()).get();
      room.setState(GameUtil.SHOWING_PATTERN);
      room = this.roomRepository.save(room);

      HashMap<String, Object> responseMap = craftResponse(GameUtil.SHOWING_PATTERN);
      responseMap.put("pattern", room.getPattern());
      return responseMap;
    }

    Player player = this.playerRepository.findById(playerId).get();

    if (player.getTileId() == null) {
      generateTileIds(room.getId(), playerId);
    }

    player = this.playerRepository.findById(playerId).get();

    HashMap<String, Object> responseMap = craftResponse(GameUtil.PREPARING);
    responseMap.put("tile_id", player.getTileId());
    return responseMap;
  }

  private HashMap<String, Object> showingPatternState(final Room room) {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.SHOWING_PATTERN);
    responseMap.put("pattern", room.getPattern());
    return responseMap;
  }

  private HashMap<String, Object> successfulEndState() {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.SUCCESSFUL_END);
    return responseMap;
  }

  private HashMap<String, Object> waitingState(final String roomId) {

    HashMap<String, Object> responseMap = craftResponse(GameUtil.WAITING);
    responseMap.put("number_of_players", this.playerRepository.findByRoomId(roomId).size());
    return responseMap;
  }
}
