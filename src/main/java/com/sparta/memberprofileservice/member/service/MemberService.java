package com.sparta.memberprofileservice.member.service;

import com.sparta.memberprofileservice.member.dto.MemberRequest;
import com.sparta.memberprofileservice.member.dto.MemberResponse;
import com.sparta.memberprofileservice.member.entity.Member;
import com.sparta.memberprofileservice.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        Member member = new Member(request.getName(), request.getAge(), request.getMbti());
        Member saved = memberRepository.save(member);
        log.info("[API - LOG] 팀원 저장 완료 id={}, name={}", saved.getId(), saved.getName());
        return new MemberResponse(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀원이 없습니다. id=" + id));
        log.info("[API - LOG] 팀원 조회 성공 id={}", id);
        return new MemberResponse(member);
    }
}