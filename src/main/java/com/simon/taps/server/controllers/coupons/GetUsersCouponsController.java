package com.simon.taps.server.controllers.coupons;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Coupon;
import com.simon.taps.server.database.CouponRepository;
import com.simon.taps.server.database.UserRepository;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;

@RestController
public class GetUsersCouponsController {

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/coupon/user/{userId}")
  public HashMap<String, Object> getCoupons(@PathVariable final String userId,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    boolean userExists = this.userRepository.existsById(userId);

    if (!userExists) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.USER_DOES_NOT_EXIST);
    }

    List<Coupon> coupons = this.couponRepository.findByUserId(userId);
    List<String> couponsStr = new ArrayList<>();

    for (Coupon coupon : coupons) {

      if (LocalDateTime.now().isBefore(coupon.getExpireAt())) {

        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = coupon.getExpireAt();

        LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        String couponPlusExpirationStr = coupon.getId() + "-" + days + " " + hours + " " + minutes;

        couponsStr.add(couponPlusExpirationStr);
      } else {

        coupon.setActive(false);
        this.couponRepository.save(coupon);
      }
    }

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.COUPONS, couponsStr);

    return responseMap;
  }

}
