package com.simon.taps.server.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.simon.taps.server.util.CouponUtil;
import com.simon.taps.server.util.GameUtil;

@Component
public class DatabaseUtil {

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserRepository userRepository;

  public Player createDefaultPlayer() {

    Player newPlayer = new Player();
    newPlayer.setTileId(null);
    newPlayer.setReady(false);
    newPlayer.setRestartReady(false);
    newPlayer.setCoupon(null);

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

  public User createDefaultUser() {

    User newUser = new User();
    newUser.setLastWon(LocalDateTime.now());
    newUser.setWonToday(0L);
    newUser.setAdmin(false);

    return newUser;
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
  public void issueCoupons(final String playerId) {

    Player player = this.playerRepository.findById(playerId).get();
    User user = this.userRepository.findById(playerId).get();

    // New day -> resetting daily wins
    if (user.getLastWon().getDayOfYear() != LocalDateTime.now().getDayOfYear()) {
      user.setWonToday(0L);
    }

    user.setLastWon(LocalDateTime.now());

    // DID NOT reach daily limit (or admin) -> get coupon
    if ((user.getWonToday() < GameUtil.MAX_COUPONS_PER_DAY) || user.getAdmin()) {

      String couponStr;

      while (true) {

        couponStr = CouponUtil.createCouponStr();
        boolean exists = this.couponRepository.existsById(couponStr);

        if (!exists) {
          break;
        }
      }

      player.setCoupon(couponStr);
      player = this.playerRepository.save(player);

      user.setWonToday(user.getWonToday() + 1);

      Coupon coupon = new Coupon();
      coupon.setId(couponStr);
      coupon.setActive(true);
      coupon.setExpireAt(LocalDateTime.now().plusDays(GameUtil.COUPON_LIFETIME_DAY));
      coupon.setIssued(true);
      coupon.setUserId(user.getId());
      coupon = this.couponRepository.save(coupon);
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

  public void tryToIssueCoupons(final Room room, final String playerId) {

    long wonRoundNumber = room.getRound() - 1;

    // NOT enough points -> NO coupons
    if (!(wonRoundNumber >= GameUtil.MIN_SCORE_TO_EARN_COUPON)) {
      return;
    }

    Player player = this.playerRepository.findById(playerId).get();

    if (player.getCoupon() == null) {

      issueCoupons(playerId);
    }
  }

}
