package dev.fisa.concurrency_coupon.domain.reservation.service;

import dev.fisa.concurrency_coupon.domain.performance.entity.Performance;
import dev.fisa.concurrency_coupon.domain.performance.repository.PerformanceRepository;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.CreateResponse;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.ReservationCountResponse;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.SeatStatusResponse;
import dev.fisa.concurrency_coupon.domain.reservation.entity.Reservation;
import dev.fisa.concurrency_coupon.domain.reservation.entity.ReservationSeat;
import dev.fisa.concurrency_coupon.domain.reservation.repository.ReservationRepository;
import dev.fisa.concurrency_coupon.domain.reservation.repository.ReservationSeatRepository;
import dev.fisa.concurrency_coupon.domain.seat.entity.Seat;
import dev.fisa.concurrency_coupon.domain.seat.entity.SeatStatus;
import dev.fisa.concurrency_coupon.domain.seat.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * v0: 락 없는 기본 버전
 * - 동시 요청 시 같은 좌석에 대해 Read-Modify-Write race condition 발생
 * - 여러 트랜잭션이 동시에 AVAILABLE 상태를 읽고 SOLD로 변경 → 중복 예매
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceV0Basic implements ReservationService {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    public CreateResponse reserve(ReservationRequest request) {
        // 1. 공연 존재 확인
        Performance performance = performanceRepository.findById(request.performanceId())
            .orElseThrow(() -> new IllegalArgumentException("공연이 존재하지 않습니다."));

        // 2. 좌석 조회
        List<Seat> seats = seatRepository.findAllById(request.seatIds());

        if (seats.size() != request.seatIds().size()) {
            throw new IllegalArgumentException("존재하지 않는 좌석이 포함되어 있습니다.");
        }

        // 3. 좌석 상태 확인 및 변경 (Read → Check → Modify)
        for (Seat seat : seats) {
            log.info("[{}] 좌석 {} 현재 상태: {}", Thread.currentThread().getName(), seat.getId(), seat.getStatus());
            seat.markSold();  // AVAILABLE → SOLD (이미 SOLD면 예외)
        }

        // 4. 예약 생성
        Reservation reservation = Reservation.builder()
            .customerName(request.customerName())
            .performance(performance)
            .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. 예약-좌석 매핑
        for (Seat seat : seats) {
            ReservationSeat reservationSeat = ReservationSeat.builder()
                .reservation(savedReservation)
                .seat(seat)
                .build();
            reservationSeatRepository.save(reservationSeat);
        }

        // 6. 예약 완료
        savedReservation.complete();

        return CreateResponse.builder()
            .reservationId(savedReservation.getId())
            .customerName(savedReservation.getCustomerName())
            .performanceTitle(performance.getTitle())
            .seatIds(request.seatIds())
            .status(savedReservation.getStatus().name())
            .build();
    }

    @Override
    public List<SeatStatusResponse> getSeatStatus(Long performanceId) {
        return seatRepository.findByPerformanceId(performanceId).stream()
            .map(seat -> SeatStatusResponse.builder()
                .seatId(seat.getId())
                .section(seat.getSection().name())
                .rowNo(seat.getRowNo())
                .colNo(seat.getColNo())
                .status(seat.getStatus().name())
                .build())
            .toList();
    }

    @Override
    public ReservationCountResponse getReservationCount(Long performanceId) {
        long reservationCount = reservationRepository.countByPerformanceId(performanceId);
        long soldSeatCount = seatRepository.countByPerformanceIdAndStatus(performanceId, SeatStatus.SOLD);

        return ReservationCountResponse.builder()
            .performanceId(performanceId)
            .reservationCount(reservationCount)
            .soldSeatCount(soldSeatCount)
            .build();
    }
}
