package dev.fisa.concurrency_coupon.domain.member.repository;

import dev.fisa.concurrency_coupon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
