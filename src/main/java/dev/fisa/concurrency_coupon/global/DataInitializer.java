package dev.fisa.concurrency_coupon.global;

import dev.fisa.concurrency_coupon.domain.performance.entity.Performance;
import dev.fisa.concurrency_coupon.domain.performance.repository.PerformanceRepository;
import dev.fisa.concurrency_coupon.domain.seat.entity.Seat;
import dev.fisa.concurrency_coupon.domain.seat.entity.Section;
import dev.fisa.concurrency_coupon.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 이미 데이터가 존재하면 스킵 (멀티 WAS 환경에서 중복 생성 방지)
        if (performanceRepository.count() > 0) {
            log.info("[DataInitializer] 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        Performance performance = performanceRepository.save(
            Performance.builder()
                .title("2026 봄 콘서트")
                .venue("올림픽홀")
                .build()
        );

        int seatCount = 0;

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
                seatCount++;
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
                seatCount++;
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
                seatCount++;
            }
        }

        log.info("[DataInitializer] 공연 1개, 좌석 {}개 초기화 완료", seatCount);
    }
}
