package dev.fisa.concurrency_coupon.domain.coupon.service;

import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.CreateCouponRequest;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponRequest.IssueCouponRequest;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.CreateCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetCouponResponse;
import dev.fisa.concurrency_coupon.domain.coupon.dto.CouponResponse.GetIssueCountResponse;
import dev.fisa.concurrency_coupon.domain.coupon.entity.Coupon;
import dev.fisa.concurrency_coupon.domain.coupon_issue.entity.CouponIssue;
import dev.fisa.concurrency_coupon.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceV2WithoutAop implements CouponService {

    private final EntityManagerFactory emf;

    @Override
    public CreateCouponResponse createCoupon(CreateCouponRequest request) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Coupon coupon = Coupon.builder()
                                  .title(request.title())
                                  .totalStock(request.totalStock())
                                  .availableStock(request.totalStock())
                                  .build();
            em.persist(coupon);
            tx.commit();

            return CreateCouponResponse.builder()
                                       .title(coupon.getTitle())
                                       .totalStock(coupon.getTotalStock())
                                       .build();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public synchronized void issue(Long couponId, IssueCouponRequest request) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. 읽기
            Coupon coupon = em.find(Coupon.class, couponId);
            if (coupon == null) {
                throw new IllegalArgumentException("쿠폰이 존재하지 않습니다.");
            }

            log.info("[{}] 현재 재고: {}", Thread.currentThread().getName(),
                coupon.getAvailableStock());

            // 2. 쿠폰 재고 감소 (수정)
            coupon.decreaseStock();

            Member member = em.find(Member.class, request.memberId());
            if (member == null) {
                throw new IllegalArgumentException("회원이 존재하지 않습니다.");
            }

            CouponIssue couponIssue = CouponIssue.builder()
                                                 .member(member)
                                                 .coupon(coupon)
                                                 .build();
            em.persist(couponIssue);

            // 3. 쓰기
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public GetCouponResponse getCoupon(Long couponId) {
        EntityManager em = emf.createEntityManager();
        try {
            Coupon coupon = em.find(Coupon.class, couponId);
            if (coupon == null) {
                throw new IllegalArgumentException("쿠폰이 존재하지 않습니다.");
            }

            return GetCouponResponse.builder()
                                    .id(coupon.getId())
                                    .title(coupon.getTitle())
                                    .totalStock(coupon.getTotalStock())
                                    .availableStock(coupon.getAvailableStock())
                                    .build();
        } finally {
            em.close();
        }
    }

    @Override
    public GetIssueCountResponse getIssueCount(Long couponId) {
        EntityManager em = emf.createEntityManager();
        try {
            Coupon coupon = em.find(Coupon.class, couponId);
            if (coupon == null) {
                throw new IllegalArgumentException("쿠폰이 존재하지 않습니다.");
            }

            long issuedCount = em.createQuery(
                                     "SELECT COUNT(ci) FROM CouponIssue ci WHERE ci.coupon.id = :couponId", Long.class)
                                 .setParameter("couponId", couponId)
                                 .getSingleResult();

            return GetIssueCountResponse.builder()
                                        .id(coupon.getId())
                                        .issuedCount(issuedCount)
                                        .availableStock(coupon.getAvailableStock())
                                        .build();
        } finally {
            em.close();
        }
    }
}
