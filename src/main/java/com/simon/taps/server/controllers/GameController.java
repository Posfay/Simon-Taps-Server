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
import com.simon.taps.server.util.ServerUtil;
import com.simon.taps.server.wrappers.PostRequestWrapper;

@RestController
public class GameController {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  private HashMap<String, Object> craftResponse(final String state) {

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.GAME_STATE, state);

    return responseMap;
  }

  @PostMapping("/game")
  public HashMap<String, Object> postGame(@Valid @RequestBody final PostRequestWrapper postBody) {

    boolean correct;
    String newPattern;

    Room room = this.roomRepository.findById(postBody.getRoomId()).get();
    Player player = this.playerRepository.findById(postBody.getPlayerId()).get();

    newPattern = room.getPatternCompleted() + player.getTileId();
    correct = newPattern.regionMatches(0, room.getPattern(), 0, newPattern.length());

    room.setPatternCompleted(newPattern);
    room.setTimer(LocalDateTime.now());

    room = this.roomRepository.save(room);

    // Utolso es jo -> SUCCESSFUL END
    if (correct && (newPattern.length() == room.getPattern().length())) {

      room.setState(GameUtil.SUCCESSFUL_END);
      room = this.roomRepository.save(room);

      return craftResponse(GameUtil.SUCCESSFUL_END);
    }

    // Jo
    if (correct) {

      return craftResponse(GameUtil.PLAYING);
    }

    // Rossz -> FAIL_END
    else {

      room.setState(GameUtil.FAIL_END);
      this.roomRepository.save(room);

      return craftResponse(GameUtil.FAIL_END);
    }
  }
}
