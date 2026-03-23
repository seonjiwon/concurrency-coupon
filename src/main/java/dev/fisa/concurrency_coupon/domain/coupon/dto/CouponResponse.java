package dev.fisa.concurrency_coupon.domain.coupon.dto;

import lombok.Builder;

public record CouponResponse() {

    @Builder
    public record CreateCouponResponse(
        String title,
        int totalStock
    ) {
    }

    @Builder
    public record GetCouponResponse(
        Long id,
        String title,
        int totalStock,
        int availableStock
    ) {
    }

    @Builder
    public record GetIssueCountResponse(
        Long id,
        long issuedCount,
        int availableStock
    ) {
    }
}
