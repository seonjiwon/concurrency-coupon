package dev.fisa.concurrency_coupon.domain.coupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쿠폰 이름
    private String title;

    // 총 발행 개수
    private int totalStock;

    // 이용가능한 개수
    private int availableStock;

    public void decreaseStock() {
        if (this.availableStock <= 0) {
            throw new IllegalStateException("재고가 소진되었습니다.");
        }
        this.availableStock--;
    }
}
