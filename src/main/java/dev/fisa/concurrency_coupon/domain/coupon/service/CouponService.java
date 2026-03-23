package dev.fisa.concurrency_coupon.domain.coupon.service;

import static dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.*;

import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.CreateCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetIssueCountResponse;

public interface CouponService {

    CouponResponse.CreateCouponResponse createCoupon(CreateCouponRequest request);

    void issue(Long couponId, IssueCouponRequest request);

    GetCouponResponse getCoupon(Long couponId);

    GetIssueCountResponse getIssueCount(Long couponId);
}
