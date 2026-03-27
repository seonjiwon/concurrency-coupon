package dev.fisa.concurrency_coupon.global;

import dev.fisa.concurrency_coupon.domain.performance.entity.Performance;
import dev.fisa.concurrency_coupon.domain.performance.repository.PerformanceRepository;
import dev.fisa.concurrency_coupon.domain.seat.entity.Seat;
import dev.fisa.concurrency_coupon.domain.seat.entity.Section;
import dev.fisa.concurrency_coupon.domain.seat.repository.SeatRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    @PostConstruct
    public void init() {
        // 공연 1개
        Performance performance = performanceRepository.save(
            Performance.builder()
                .title("2026 봄 콘서트")
                .venue("올림픽홀")
                .performanceDate(LocalDate.of(2026, 5, 1))
                .performanceTime(LocalTime.of(19, 0))
                .build()
        );

        // 좌석 100개: VIP 20석 + R 40석 + S 40석
        int seatNo = 0;

        // VIP 20석 (4행 × 5열)
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 5; col++) {
                seatRepository.save(
                    Seat.builder()
                        .performance(performance)
                        .section(Section.VIP)
                        .rowNo(row)
                        .colNo(col)
                        .build()
                );
                seatNo++;
            }
        }

        // R석 40석 (4행 × 10열)
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 10; col++) {
                seatRepository.save(
                    Seat.builder()
                        .performance(performance)
                        .section(Section.R)
                        .rowNo(row)
                        .colNo(col)
                        .build()
                );
                seatNo++;
            }
        }

        // S석 40석 (4행 × 10열)
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 10; col++) {
                seatRepository.save(
                    Seat.builder()
                        .performance(performance)
                        .section(Section.S)
                        .rowNo(row)
                        .colNo(col)
                        .build()
                );
                seatNo++;
            }
        }

        log.info("[DataInitializer] 공연 1개, 좌석 {}개 초기화 완료", seatNo);
    }
}
