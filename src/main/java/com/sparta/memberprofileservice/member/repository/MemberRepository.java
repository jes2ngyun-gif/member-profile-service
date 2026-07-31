package com.sparta.memberprofileservice.member.repository;

import com.sparta.memberprofileservice.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}