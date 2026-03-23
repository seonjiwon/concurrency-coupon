package dev.fisa.concurrency_coupon.global;

import dev.fisa.concurrency_coupon.domain.coupon.entity.Coupon;
import dev.fisa.concurrency_coupon.domain.coupon.repository.CouponRepository;
import dev.fisa.concurrency_coupon.domain.member.entity.Member;
import dev.fisa.concurrency_coupon.domain.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        // 쿠폰 1개
        couponRepository.save(Coupon.builder()
                                    .title("선착순 쿠폰")
                                    .totalStock(100)
                                    .availableStock(100)
                                    .build());

        // 멤버 1000명
        for (int i = 1; i <= 1000; i++) {
            memberRepository.save(Member.builder()
                                        .nickname("user" + i)
                                        .build());
        }
    }
}
