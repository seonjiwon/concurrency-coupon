package dev.fisa.concurrency_coupon.domain.reservation.repository;

import dev.fisa.concurrency_coupon.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    long countByPerformanceId(Long performanceId);
}
