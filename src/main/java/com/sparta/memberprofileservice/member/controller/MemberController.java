package com.sparta.memberprofileservice.member.controller;

import com.sparta.memberprofileservice.member.dto.MemberRequest;
import com.sparta.memberprofileservice.member.dto.MemberResponse;
import com.sparta.memberprofileservice.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        log.info(
                "[API - LOG] POST /api/members 요청 name={}",
                request.getName()
        );

        MemberResponse response =
                memberService.createMember(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get(
            @PathVariable Long id
    ) {
        log.info(
                "[API - LOG] GET /api/members/{}",
                id
        );

        return ResponseEntity.ok(
                memberService.getMember(id)
        );
    }

    /**
     * MultipartFile로 이미지를 전달받아 S3에 업로드한다.
     */
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        log.info(
                "[API - LOG] POST /api/members/{}/profile-image filename={}",
                id,
                file.getOriginalFilename()
        );

        String profileImageKey =
                memberService.uploadProfileImage(id, file);

        return ResponseEntity.ok(profileImageKey);
    }

    /**
     * S3 객체를 내려받을 수 있는 7일짜리 Presigned URL을 반환한다.
     */
    @GetMapping("/{id}/profile-image")
    public ResponseEntity<String> getProfileImage(
            @PathVariable Long id
    ) {
        log.info(
                "[API - LOG] GET /api/members/{}/profile-image",
                id
        );

        String presignedUrl =
                memberService.getProfileImagePresignedUrl(id);

        return ResponseEntity.ok(presignedUrl);
    }
}