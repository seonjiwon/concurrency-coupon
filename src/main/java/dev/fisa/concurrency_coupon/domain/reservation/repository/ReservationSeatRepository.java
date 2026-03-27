package dev.fisa.concurrency_coupon.domain.reservation.repository;

import dev.fisa.concurrency_coupon.domain.reservation.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
}
