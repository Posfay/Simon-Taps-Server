package com.simon.taps.server.controllers.game;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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
  public HashMap<String, Object> postGame(@Valid @RequestBody final PostRequestWrapper postBody,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    boolean correct;
    String newPattern;
    long round;

    Room room = this.roomRepository.findById(postBody.getRoomId()).get();
    Player player = this.playerRepository.findById(postBody.getPlayerId()).get();

    newPattern = room.getPatternCompleted() + player.getTileId();
    correct = newPattern.regionMatches(0, room.getPattern(), 0, newPattern.length());
    round = room.getRound();

    room.setPatternCompleted(newPattern);
    room.setTimer(LocalDateTime.now());

    room = this.roomRepository.save(room);

    // Jo es utolso -> SUCCESSFUL END
    if (correct && (newPattern.length() == room.getPattern().length())) {

      room.setState(GameUtil.END);
      room = this.roomRepository.save(room);

      return craftResponse(GameUtil.END);
    }

    // Jo es round vege -> uj round
    if (correct
        && (newPattern.length() == room.getPattern().substring(0, (int) round).length())) {

      room.setPatternCompleted("");
      room.setRound(round + 1);
      room.setState(GameUtil.SHOWING_PATTERN);
      room = this.roomRepository.save(room);

      List<Player> players = this.playerRepository.findByRoomId(room.getId());

      for (Player p : players) {

        p.setReady(false);
        this.playerRepository.save(p);
      }
    }
    // Jo
    if (correct) {

      return craftResponse(GameUtil.PLAYING);
    }

    // Rossz -> FAIL_END
    else {

      room.setState(GameUtil.END);
      this.roomRepository.save(room);

      return craftResponse(GameUtil.END);
    }
  }
}
