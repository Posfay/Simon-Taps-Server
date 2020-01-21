package com.simon.taps.server.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.simon.taps.server.util.GameUtil;

@Component
public class DatabaseUtil {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  public Player createDefaultPlayer() {

    Player newPlayer = new Player();
    newPlayer.setTileId(null);
    newPlayer.setReady(false);
    newPlayer.setRestartReady(false);

    return newPlayer;
  }

  public Room createDefaultRoom() {

    Room newRoom = new Room();
    newRoom.setPattern("");
    newRoom.setPatternCompleted("");
    newRoom.setRound(GameUtil.FIRST_ROUND_LENGTH);
    newRoom.setState(GameUtil.WAITING);
    newRoom.setTimer(LocalDateTime.now());

    return newRoom;
  }

  @Transactional
  public void generateTileIds(final String roomId, final String playerId) {

    List<Player> playersInRoom = this.playerRepository.findByRoomId(roomId);
    List<Integer> tileIds = new ArrayList<>();
    tileIds.add(1);
    tileIds.add(2);
    tileIds.add(3);
    tileIds.add(4);
    Collections.shuffle(tileIds);

    Room room = this.roomRepository.findById(roomId).get();

    for (Player player : playersInRoom) {

      player.setTileId(tileIds.remove(0).toString());
      this.playerRepository.save(player);

      // for optimistic locking
      room = this.roomRepository.save(room);
    }
  }

  @Transactional
  public void saveRoomAndPlayer(final Room room, final Player newPlayer) {

    this.roomRepository.save(room);
    this.playerRepository.save(newPlayer);
  }

  @Transactional
  public void saveRoomAndPlayers(final Room room, final List<Player> players) {

    this.roomRepository.save(room);
    for (Player player : players) {
      this.playerRepository.save(player);
    }
  }

}
