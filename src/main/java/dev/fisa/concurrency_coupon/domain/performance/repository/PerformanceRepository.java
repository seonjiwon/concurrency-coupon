package dev.fisa.concurrency_coupon.domain.performance.repository;

import dev.fisa.concurrency_coupon.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
