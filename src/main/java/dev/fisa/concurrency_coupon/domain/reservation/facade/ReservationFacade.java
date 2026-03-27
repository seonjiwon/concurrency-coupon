package dev.fisa.concurrency_coupon.domain.reservation.facade;

import dev.fisa.concurrency_coupon.domain.reservation.converter.ReservationConverter;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.CreateResponse;
import dev.fisa.concurrency_coupon.domain.reservation.entity.Reservation;
import dev.fisa.concurrency_coupon.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationFacade {

    private final ReservationService reservationService;

    public CreateResponse reserve(ReservationRequest request) {
        // 1. 좌석 선점 + 예약 생성 (트랜잭션 1)
        Reservation reservation = reservationService.holdSeats(request);

        // 2. 결제 처리 (트랜잭션 밖)
        processPayment();

        // 3. 확정 (트랜잭션 2)
        reservationService.confirmReservation(reservation.getId(), request.seatIds());

        return ReservationConverter.toCreateResponse(reservation, request);
    }

    private void processPayment() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
