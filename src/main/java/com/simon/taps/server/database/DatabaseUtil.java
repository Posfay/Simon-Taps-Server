package com.simon.taps.server.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseUtil {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

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

}
