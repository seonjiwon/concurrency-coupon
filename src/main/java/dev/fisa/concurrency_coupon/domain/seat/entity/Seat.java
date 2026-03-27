package dev.fisa.concurrency_coupon.domain.seat.entity;

import dev.fisa.concurrency_coupon.domain.performance.entity.Performance;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Enumerated(EnumType.STRING)
    private Section section;

    private int rowNo;

    private int colNo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    public void hold() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("예매할 수 없는 좌석입니다. (좌석 ID: " + this.id + ", 상태: " + this.status + ")");
        }
        this.status = SeatStatus.HELD;
    }

    public void sold() {
        this.status = SeatStatus.SOLD;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }
}
