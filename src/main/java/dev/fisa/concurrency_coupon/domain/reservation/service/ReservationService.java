package dev.fisa.concurrency_coupon.domain.reservation.service;

import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse.CreateResponse reserve(ReservationRequest request);

    List<ReservationResponse.SeatStatusResponse> getSeatStatus(Long performanceId);

    ReservationResponse.ReservationCountResponse getReservationCount(Long performanceId);
}
