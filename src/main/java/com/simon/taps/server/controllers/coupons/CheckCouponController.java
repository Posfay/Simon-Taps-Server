package com.simon.taps.server.controllers.coupons;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Coupon;
import com.simon.taps.server.database.CouponRepository;
import com.simon.taps.server.util.CouponUtil;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;

@RestController
public class CheckCouponController {

  @Autowired
  private CouponRepository couponRepository;

  @GetMapping("/coupon/check/{couponStr}")
  public HashMap<String, Object> checkCoupon(@PathVariable final String couponStr,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    if ((couponStr == null) || (couponStr.length() != CouponUtil.COUPON_LENGTH)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.INVALID_COUPON);
    }

    Optional<Coupon> couponOpt = this.couponRepository.findById(couponStr);

    if (!couponOpt.isPresent()) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.NO_SUCH_COUPON);
    }

    Coupon coupon = couponOpt.get();

    if (coupon.getExpireAt().isBefore(LocalDateTime.now())) {

      coupon.setActive(false);
      coupon = this.couponRepository.save(coupon);
    }

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.COUPON_ACTIVE, coupon.getActive());
    responseMap.put(ServerUtil.COUPON_ISSUED, coupon.getIssued());
    responseMap.put(ServerUtil.COUPON_ID, coupon.getId());

    return responseMap;
  }

}
