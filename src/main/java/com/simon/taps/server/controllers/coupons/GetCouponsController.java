package com.simon.taps.server.controllers.coupons;

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
public class GetCouponsController {

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

      if (coupon.getActive()) {
        couponsStr.add(coupon.getId());
      }
    }

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.COUPONS, couponsStr);

    return responseMap;
  }

}
