package dev.fisa.concurrency_coupon.domain.coupon.dto;

import lombok.Builder;

public record CouponRequest() {

    @Builder
    public record CreateCouponRequest(
        String title,
        int totalStock
    ) {
    }

    @Builder
    public record IssueCouponRequest(
        Long memberId
    ) {

    }
}
