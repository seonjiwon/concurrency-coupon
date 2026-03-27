package dev.fisa.concurrency_coupon.domain.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record ReservationRequest(
    @NotNull(message = "공연 ID는 필수입니다.")
    Long performanceId,

    @NotEmpty(message = "좌석을 1개 이상 선택해야 합니다.")
    List<Long> seatIds,

    @NotBlank(message = "예매자 이름은 필수입니다.")
    String customerName
) {
}
