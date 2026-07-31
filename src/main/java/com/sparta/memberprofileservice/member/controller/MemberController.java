package com.sparta.memberprofileservice.member.controller;

import com.sparta.memberprofileservice.member.dto.MemberRequest;
import com.sparta.memberprofileservice.member.dto.MemberResponse;
import com.sparta.memberprofileservice.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> create(
            @Valid @RequestBody MemberRequest request
    ) {
        log.info("[API - LOG] POST /api/members 요청 name={}", request.getName());

        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get(
            @PathVariable Long id
    ) {
        log.info("[API - LOG] GET /api/members/{}", id);
        return ResponseEntity.ok(memberService.getMember(id));
    }
}