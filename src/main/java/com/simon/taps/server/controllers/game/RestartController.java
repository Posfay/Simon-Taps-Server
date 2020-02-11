package com.simon.taps.server.controllers.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class RestartController {

  @Autowired
  private DatabaseUtil databaseUtil;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @PostMapping("/restart")
  public HashMap<String, Object> restart(
      @Valid @RequestBody final PostRequestWrapper postBody,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    long countOfRestartReady = 0;

    while (true) {

      Player currentPlayer = this.playerRepository.findById(postBody.getPlayerId()).get();
      Room room = this.roomRepository.findById(postBody.getRoomId()).get();

      currentPlayer.setRestartReady(true);

      currentPlayer = this.playerRepository.save(currentPlayer);

      List<Player> playersInRoom = this.playerRepository.findByRoomId(postBody.getRoomId());

      countOfRestartReady = 0;

      for (Player player : playersInRoom) {
        if (player.getRestartReady()) {
          countOfRestartReady++;
        }
      }

      if (countOfRestartReady == 4) {

        room.setPattern("");
        room.setPatternCompleted("");
        room.setRound(GameUtil.FIRST_ROUND_LENGTH);
        room.setState(GameUtil.PREPARING);
        room.setTimer(LocalDateTime.now());

        List<Player> toBeSavedPlayers = new ArrayList<>();

        for (Player player : playersInRoom) {

          player.setTileId(null);
          player.setReady(false);
          player.setRestartReady(false);
          player.setCoupon(null);

          toBeSavedPlayers.add(player);
        }

        try {
          this.databaseUtil.saveRoomAndPlayers(room, toBeSavedPlayers);
        } catch (OptimisticLockException ignore) {
          continue;
        }

        break;
      } else {

        try {
          room = this.roomRepository.save(room);
        } catch (OptimisticLockException ignore) {
          continue;
        }

        break;
      }
    }

    HashMap<String, Object> responseMap = new HashMap<>();
    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.NUMBER_OF_RESTART_PLAYERS, countOfRestartReady);

    return responseMap;
  }
}
