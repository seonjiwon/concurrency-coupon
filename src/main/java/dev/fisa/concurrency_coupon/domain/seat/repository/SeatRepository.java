package dev.fisa.concurrency_coupon.domain.seat.repository;

import dev.fisa.concurrency_coupon.domain.seat.entity.Seat;
import dev.fisa.concurrency_coupon.domain.seat.entity.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Pessimistic Lock: 단일 좌석
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdForUpdate(@Param("id") Long id);

    // Pessimistic Lock: 다중 좌석 (정렬 없음 → 데드락 가능)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :ids")
    List<Seat> findAllByIdForUpdate(@Param("ids") List<Long> ids);

    // Pessimistic Lock: 다중 좌석 (ID 정렬 → 데드락 방지)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :ids ORDER BY s.id ASC")
    List<Seat> findAllByIdForUpdateOrderById(@Param("ids") List<Long> ids);

    // 공연별 좌석 목록
    List<Seat> findByPerformanceId(Long performanceId);

    // 공연별 SOLD 좌석 수
    long countByPerformanceIdAndStatus(Long performanceId, SeatStatus status);

    // 공연별 전체 좌석 수
    long countByPerformanceId(Long performanceId);
}
