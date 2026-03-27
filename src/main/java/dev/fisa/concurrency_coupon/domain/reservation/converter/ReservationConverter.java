package dev.fisa.concurrency_coupon.domain.reservation.converter;

import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.CreateResponse;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse.SeatStatusResponse;
import dev.fisa.concurrency_coupon.domain.reservation.entity.Reservation;
import dev.fisa.concurrency_coupon.domain.seat.entity.Seat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationConverter {

    public static CreateResponse toCreateResponse(Reservation reservation, ReservationRequest request) {
        return CreateResponse.builder()
            .reservationId(reservation.getId())
            .customerName(reservation.getCustomerName())
            .performanceTitle(reservation.getPerformance().getTitle())
            .seatIds(request.seatIds())
            .status(reservation.getStatus().name())
            .build();
    }

    public static SeatStatusResponse toSeatStatusResponse(Seat seat) {
        return SeatStatusResponse.builder()
            .seatId(seat.getId())
            .section(seat.getSection().name())
            .rowNo(seat.getRowNo())
            .colNo(seat.getColNo())
            .status(seat.getStatus().name())
            .build();
    }
}
