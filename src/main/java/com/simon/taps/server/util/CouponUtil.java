package com.simon.taps.server.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.simon.taps.server.database.Coupon;

public class CouponUtil {

  public static final long COUPON_LENGTH = 6;

  public static final long MAX_COUPONS_PER_REQUEST = 100;

  public static Coupon createCoupon() {

    String couponStr = createCouponStr();

    Coupon coupon = new Coupon();
    coupon.setId(couponStr);
    coupon.setActive(true);
    coupon.setExpireAt(LocalDateTime.now().plusDays(GameUtil.COUPON_LIFETIME_DAY));
    coupon.setIssued(false);
    coupon.setUserId(null);

    return coupon;
  }

  public static List<Coupon> createCoupons(final long numberOfCoupons) {

    List<Coupon> coupons = new ArrayList<>();

    for (int i = 0; i < numberOfCoupons; i++) {

      Coupon coupon = createCoupon();

      coupons.add(coupon);
    }

    return coupons;
  }

  public static String createCouponStr() {

    String uuidStr = UUID.randomUUID().toString();
    uuidStr = uuidStr.replace("-", "");
    String couponStr = "";

    for (int j = 0; j < CouponUtil.COUPON_LENGTH; j++) {

      int randomIndex = (int) Math.floor(Math.random() * uuidStr.length());
      couponStr += uuidStr.charAt(randomIndex);
    }

    couponStr = couponStr.toUpperCase();

    return couponStr;
  }

  private CouponUtil() {
  }

}
