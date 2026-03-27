package dev.fisa.concurrency_coupon.domain.reservation.controller;

import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationRequest;
import dev.fisa.concurrency_coupon.domain.reservation.dto.ReservationResponse;
import dev.fisa.concurrency_coupon.domain.reservation.service.ReservationFacade;
import dev.fisa.concurrency_coupon.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ReservationController {

    private final ReservationFacade reservationFacade;
    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<?> reserve(
        @Valid @RequestBody ReservationRequest request
    ) {
        ReservationResponse.CreateResponse response = reservationFacade.reserve(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/performances/{performanceId}/seats")
    public ResponseEntity<?> getSeatStatus(
        @PathVariable Long performanceId
    ) {
        List<ReservationResponse.SeatStatusResponse> response = reservationService.getSeatStatus(performanceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/performances/{performanceId}/stats")
    public ResponseEntity<?> getReservationCount(
        @PathVariable Long performanceId
    ) {
        ReservationResponse.ReservationCountResponse response = reservationService.getReservationCount(performanceId);
        return ResponseEntity.ok(response);
    }
}
