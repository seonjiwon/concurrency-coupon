package dev.fisa.concurrency_coupon.domain.reservation.service;

import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse;
import dev.fisa.concurrency_coupon.domain.reservation.entity.Reservation;

import java.util.List;

public interface ReservationService {

    Reservation holdSeats(ReservationRequest request);

    void confirmReservation(Long reservationId, List<Long> seatIds);

    List<ReservationResponse.SeatStatusResponse> getSeatStatus(Long performanceId);

    ReservationResponse.ReservationCountResponse getReservationCount(Long performanceId);
}
