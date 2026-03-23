package dev.fisa.concurrency_coupon.domain.coupon.repository;

import dev.fisa.concurrency_coupon.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
