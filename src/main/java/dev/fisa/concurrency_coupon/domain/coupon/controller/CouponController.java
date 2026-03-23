package dev.fisa.concurrency_coupon.domain.coupon.controller;

import static dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.CreateCouponRequest;
import static dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.IssueCouponRequest;
import static dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetCouponResponse;
import static dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetIssueCountResponse;

import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.CreateCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<?> createCoupon(
        @RequestBody CreateCouponRequest request
    ) {
        CreateCouponResponse response = couponService.createCoupon(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{couponId}/issue")
    public ResponseEntity<?> issue(
        @PathVariable Long couponId,
        @RequestBody IssueCouponRequest request
    ) {
        couponService.issue(couponId, request);

        return ResponseEntity.ok("발급 성공");
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<?> getCoupon(
        @PathVariable Long couponId
    ) {
        GetCouponResponse response = couponService.getCoupon(couponId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{couponId}/issue-count")
    public ResponseEntity<?> getIssueCount(
        @PathVariable Long couponId
    ) {
        GetIssueCountResponse response = couponService.getIssueCount(couponId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
