package com.sparta.memberprofileservice.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class MemberRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "나이는 필수입니다.")
    @Positive(message = "나이는 1 이상이어야 합니다.")
    private Integer age;

    @NotBlank(message = "MBTI는 필수입니다.")
    private String mbti;
}