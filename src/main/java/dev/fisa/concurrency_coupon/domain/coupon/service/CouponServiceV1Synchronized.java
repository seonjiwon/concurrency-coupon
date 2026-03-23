package dev.fisa.concurrency_coupon.domain.coupon.service;

import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.CreateCouponRequest;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.IssueCouponRequest;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.CreateCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetIssueCountResponse;
import dev.fisa.concurrency_coupon.domain.coupon.entity.Coupon;
import dev.fisa.concurrency_coupon.domain.coupon.repository.CouponRepository;
import dev.fisa.concurrency_coupon.domain.coupon_issue.entity.CouponIssue;
import dev.fisa.concurrency_coupon.domain.coupon_issue.repository.CouponIssueRepository;
import dev.fisa.concurrency_coupon.domain.member.entity.Member;
import dev.fisa.concurrency_coupon.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CouponServiceV1Synchronized implements CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Override
    public CreateCouponResponse createCoupon(CreateCouponRequest request) {
        Coupon coupon = Coupon.builder()
                              .title(request.title())
                              .totalStock(request.totalStock())
                              .availableStock(request.totalStock())
                              .build();
        Coupon savedCoupon = couponRepository.save(coupon);

        return CreateCouponResponse.builder()
                                   .title(savedCoupon.getTitle())
                                   .totalStock(savedCoupon.getTotalStock())
                                   .build();
    }

    @Override
    public synchronized void issue(Long couponId, IssueCouponRequest request) {
        // 1. 읽기
        Coupon coupon = couponRepository.findById(couponId)
                                        .orElseThrow(
                                            () -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        log.info("[{}] 현재 재고: {}", Thread.currentThread().getName(), coupon.getAvailableStock());

        // 2. 쿠폰 재고 감소 (수정)
        coupon.decreaseStock();

        Member member = memberRepository.findById(request.memberId())
                                        .orElseThrow(
                                            () -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        CouponIssue couponIssue = CouponIssue.builder()
                                             .member(member)
                                             .coupon(coupon)
                                             .build();

        // 3. 쓰기
        couponIssueRepository.save(couponIssue);
    }

    @Override
    public GetCouponResponse getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                                        .orElseThrow(
                                            () -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        return GetCouponResponse.builder()
                                .id(coupon.getId())
                                .title(coupon.getTitle())
                                .totalStock(coupon.getTotalStock())
                                .availableStock(coupon.getAvailableStock())
                                .build();

    }

    @Override
    public GetIssueCountResponse getIssueCount(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                                        .orElseThrow(
                                            () -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        long issuedCount = couponIssueRepository.countByCouponId(couponId);

        return GetIssueCountResponse.builder()
                                    .id(coupon.getId())
                                    .issuedCount(issuedCount)
                                    .availableStock(coupon.getAvailableStock())
                                    .build();
    }
}
