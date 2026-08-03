package com.sparta.memberprofileservice.member.service;

import com.sparta.memberprofileservice.member.dto.MemberRequest;
import com.sparta.memberprofileservice.member.dto.MemberResponse;
import com.sparta.memberprofileservice.member.entity.Member;
import com.sparta.memberprofileservice.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        Member member = new Member(
                request.getName(),
                request.getAge(),
                request.getMbti()
        );

        Member saved = memberRepository.save(member);

        log.info(
                "[API - LOG] 팀원 저장 완료 id={}, name={}",
                saved.getId(),
                saved.getName()
        );

        return new MemberResponse(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = findMember(id);

        log.info("[API - LOG] 팀원 조회 성공 id={}", id);

        return new MemberResponse(member);
    }

    /**
     * 프로필 이미지를 S3에 업로드하고,
     * 업로드한 객체의 Key를 Member에 저장한다.
     */
    @Transactional
    public String uploadProfileImage(Long id, MultipartFile file) {
        Member member = findMember(id);

        validateImage(file);

        String originalFilename = file.getOriginalFilename();
        String safeFilename = createSafeFilename(originalFilename);

        String key = "profile-images/"
                + id
                + "/"
                + UUID.randomUUID()
                + "-"
                + safeFilename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "이미지 업로드에 실패했습니다.",
                    e
            );
        }

        // Presigned URL이 아닌 S3 객체 Key를 DB에 저장한다.
        member.updateProfileImageKey(key);

        log.info(
                "[API - LOG] 프로필 이미지 업로드 완료 id={}, key={}",
                id,
                key
        );

        return key;
    }

    /**
     * DB에 저장된 S3 객체 Key를 이용해
     * 유효기간 7일의 Presigned URL을 생성한다.
     */
    @Transactional(readOnly = true)
    public String getProfileImagePresignedUrl(Long id) {
        Member member = findMember(id);

        String key = member.getProfileImageKey();

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "등록된 프로필 이미지가 없습니다. id=" + id
            );
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofDays(7))
                        .getObjectRequest(getObjectRequest)
                        .build();

        String presignedUrl = s3Presigner
                .presignGetObject(presignRequest)
                .url()
                .toString();

        log.info(
                "[API - LOG] Presigned URL 발급 완료 id={}, 유효기간=7일",
                id
        );

        return presignedUrl;
    }

    /**
     * 반복되는 회원 조회 로직을 한곳에 모은다.
     */
    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "해당 팀원이 없습니다. id=" + id
                        )
                );
    }

    /**
     * 빈 파일과 이미지가 아닌 파일을 차단한다.
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "업로드할 이미지 파일이 없습니다."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "이미지 파일만 업로드할 수 있습니다."
            );
        }
    }

    /**
     * 파일명에 경로 문자나 특수 문자가 포함되지 않도록 정리한다.
     */
    private String createSafeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "image";
        }

        return originalFilename.replaceAll(
                "[^a-zA-Z0-9._-]",
                "_"
        );
    }
}