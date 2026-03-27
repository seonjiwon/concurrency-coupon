package dev.fisa.concurrency_coupon.domain.reservation.dto;

import lombok.Builder;

import java.util.List;

public record ReservationResponse() {

    @Builder
    public record CreateResponse(
        Long reservationId,
        String customerName,
        String performanceTitle,
        List<Long> seatIds,
        String status
    ) {
    }

    @Builder
    public record SeatStatusResponse(
        Long seatId,
        String section,
        int rowNo,
        int colNo,
        String status
    ) {
    }

    @Builder
    public record ReservationCountResponse(
        Long performanceId,
        long totalSeatCount,
        long reservationCount,
        long soldSeatCount
    ) {
    }
}
