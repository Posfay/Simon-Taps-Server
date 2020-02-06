package com.simon.taps.server.controllers.coupons;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Coupon;
import com.simon.taps.server.database.CouponRepository;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;
import com.simon.taps.server.wrappers.UpdateCouponPostWrapper;

@RestController
public class UpdateCouponController {

  @Autowired
  private CouponRepository couponRepository;

  @PostMapping("/coupon/update")
  public HashMap<String, Object> updateCoupon(
      @Valid @RequestBody final UpdateCouponPostWrapper postBody,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    Coupon coupon = this.couponRepository.findById(postBody.getId()).get();

    coupon.setActive(postBody.getActive());

    coupon = this.couponRepository.save(coupon);

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.COUPON_ACTIVE, coupon.getActive());
    responseMap.put(ServerUtil.COUPON_ISSUED, coupon.getIssued());

    return responseMap;
  }

}
