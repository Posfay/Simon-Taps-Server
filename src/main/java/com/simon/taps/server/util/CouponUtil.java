package com.simon.taps.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.simon.taps.server.database.Coupon;

public class CouponUtil {

  public static final long COUPON_LENGTH = 6;

  public static final long MAX_COUPONS_PER_REQUEST = 100;

  public static List<Coupon> createCoupons(final long numberOfCoupons) {

    List<Coupon> coupons = new ArrayList<>();

    for (int i = 0; i < numberOfCoupons; i++) {

      String uuidStr = UUID.randomUUID().toString();
      uuidStr = uuidStr.replace("-", "");
      String couponStr = "";

      for (int j = 0; j < CouponUtil.COUPON_LENGTH; j++) {

        int randomIndex = (int) Math.floor(Math.random() * uuidStr.length());
        couponStr += uuidStr.charAt(randomIndex);
      }

      couponStr = couponStr.toUpperCase();

      Coupon coupon = new Coupon();
      coupon.setId(couponStr);
      coupon.setActive(true);
      coupon.setIssued(false);

      coupons.add(coupon);
    }

    return coupons;
  }

  private CouponUtil() {
  }

}
