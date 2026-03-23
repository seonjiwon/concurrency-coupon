package dev.fisa.concurrency_coupon.domain.coupon_issue.repository;

import dev.fisa.concurrency_coupon.domain.coupon_issue.entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    long countByCouponId(Long couponId);
}
