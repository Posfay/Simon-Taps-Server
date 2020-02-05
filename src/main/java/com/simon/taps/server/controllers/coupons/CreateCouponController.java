package com.simon.taps.server.controllers.coupons;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.simon.taps.server.database.Coupon;
import com.simon.taps.server.database.CouponRepository;
import com.simon.taps.server.util.CouponUtil;
import com.simon.taps.server.util.ResponseErrorsUtil;
import com.simon.taps.server.util.ServerUtil;
import com.simon.taps.server.wrappers.CreateCouponPostWrapper;

@RestController
public class CreateCouponController {

  @Autowired
  private CouponRepository couponRepository;

  @PostMapping("/coupon/create")
  public HashMap<String, Object> createCoupon(
      @Valid @RequestBody final CreateCouponPostWrapper postBody,
      @RequestHeader(value = ServerUtil.AUTHENTICATION_HEADER,
          required = false) final String authKey) {

    if (!ServerUtil.AUTHENTICATION_KEY.equals(authKey)) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.FORBIDDEN);
    }

    long numberOfCoupons = postBody.getNumberOfCoupons();

    // Too many or no coupons
    if (!((numberOfCoupons >= 1) && (numberOfCoupons <= CouponUtil.MAX_COUPONS_PER_REQUEST))) {
      return ResponseErrorsUtil.errorResponse(ResponseErrorsUtil.Error.INVALID_COUPON_NUMBER);
    }

    List<Coupon> coupons = CouponUtil.createCoupons(numberOfCoupons);

    this.couponRepository.saveAll(coupons);

    HashMap<String, Object> responseMap = new HashMap<>();

    responseMap.put(ServerUtil.STATUS, ServerUtil.OK);
    responseMap.put(ServerUtil.COUPONS, coupons);

    return responseMap;
  }

}
