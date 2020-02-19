package com.simon.taps.server.controllers.coupons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.simon.taps.server.database.Coupon;
import com.simon.taps.server.database.CouponRepository;
import com.simon.taps.server.wrappers.CheckCoupon;

@Controller
public class WebController {

  @Autowired
  private CouponRepository couponRepository;

  @PostMapping("/activate-coupon")
  public String activateCoupon(@RequestParam(value = "couponStr") final String couponStr,
      final Model model) {

    if (!checkCouponExists(couponStr)) {
      return "check-coupon";
    }

    changeCouponActive(couponStr, true);

    model.addAttribute("active", true);
    model.addAttribute("couponId", couponStr);

    return "activation";
  }

  public void changeCouponActive(final String couponStr, final boolean active) {

    Coupon coupon = this.couponRepository.findById(couponStr).get();
    coupon.setActive(active);

    this.couponRepository.save(coupon);
  }

  @GetMapping("/check")
  public String checkCoupon(final Model model, final CheckCoupon checkCoupon) {

    model.addAttribute("checkCoupon", new CheckCoupon());

    return "check-coupon";
  }

  public boolean checkCouponExists(String checkCoupon) {

    checkCoupon = checkCoupon.toUpperCase();

    return this.couponRepository.existsById(checkCoupon);
  }

  @PostMapping("/check")
  public String checkCouponSubmit(@ModelAttribute final CheckCoupon checkCoupon,
      final Model model) {

    model.addAttribute("changedCoupon", new CheckCoupon());

    if (!checkCouponExists(checkCoupon.getCoupon())) {
      return "check-coupon";
    }

    Coupon coupon = this.couponRepository.findById(checkCoupon.getCoupon()).get();

    if (coupon.getActive()) {

      model.addAttribute("active", true);
    } else {

      model.addAttribute("active", false);
    }

    return "result";
  }

  @PostMapping("/inactivate-coupon")
  public String inActivateCoupon(@RequestParam(value = "couponStr") final String couponStr,
      final Model model) {

    if (!checkCouponExists(couponStr)) {
      return "check-coupon";
    }

    changeCouponActive(couponStr, false);

    model.addAttribute("active", false);
    model.addAttribute("couponId", couponStr);

    return "activation";
  }

}
