package dev.fisa.concurrency_coupon.domain.reservation.service;

import dev.fisa.concurrency_coupon.domain.performance.entity.Performance;
import dev.fisa.concurrency_coupon.domain.performance.repository.PerformanceRepository;
import dev.fisa.concurrency_coupon.domain.reservation.converter.ReservationConverter;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.ReservationCountResponse;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.SeatStatusResponse;
import dev.fisa.concurrency_coupon.domain.reservation.entity.Reservation;
import dev.fisa.concurrency_coupon.domain.reservation.entity.ReservationSeat;
import dev.fisa.concurrency_coupon.domain.reservation.repository.ReservationRepository;
import dev.fisa.concurrency_coupon.domain.reservation.repository.ReservationSeatRepository;
import dev.fisa.concurrency_coupon.domain.seat.entity.Seat;
import dev.fisa.concurrency_coupon.domain.seat.entity.SeatStatus;
import dev.fisa.concurrency_coupon.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Reservation holdSeats(ReservationRequest request) {
        Performance performance = getPerformance(request.performanceId());
        List<Seat> seats = getSeats(request.seatIds());

        // AVAILABLE → HELD
        seats.forEach(Seat::hold);

        // 예약 생성 (PENDING)
        Reservation reservation = createReservation(request.customerName(), performance);
        createReservationSeats(reservation, seats);

        return reservation;
    }

    @Override
    public void confirmReservation(Long reservationId, List<Long> seatIds) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalStateException("예약을 찾을 수 없습니다."));
        List<Seat> seats = seatRepository.findAllById(seatIds);

        // HELD → SOLD
        seats.forEach(Seat::sold);
        reservation.sold();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatStatusResponse> getSeatStatus(Long performanceId) {
        return seatRepository.findByPerformanceId(performanceId).stream()
            .map(ReservationConverter::toSeatStatusResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationCountResponse getReservationCount(Long performanceId) {
        return ReservationCountResponse.builder()
            .performanceId(performanceId)
            .totalSeatCount(seatRepository.countByPerformanceId(performanceId))
            .reservationCount(reservationRepository.countByPerformanceId(performanceId))
            .soldSeatCount(seatRepository.countByPerformanceIdAndStatus(performanceId, SeatStatus.SOLD))
            .build();
    }


    private Performance getPerformance(Long performanceId) {
        return performanceRepository.findById(performanceId)
            .orElseThrow(() -> new IllegalArgumentException("공연이 존재하지 않습니다."));
    }

    private List<Seat> getSeats(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 좌석이 포함되어 있습니다.");
        }
        return seats;
    }

    private Reservation createReservation(String customerName, Performance performance) {
        return reservationRepository.save(
            Reservation.builder()
                .customerName(customerName)
                .performance(performance)
                .build()
        );
    }

    private void createReservationSeats(Reservation reservation, List<Seat> seats) {
        for (Seat seat : seats) {
            reservationSeatRepository.save(
                ReservationSeat.builder()
                    .reservation(reservation)
                    .seat(seat)
                    .build()
            );
        }
    }
}
