package com.odersite.domain.review.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateReviewRequest {

    @NotNull
    private Integer productId;

    @NotNull
    private Integer orderItemId;

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 500, message = "리뷰는 500자 이내로 입력해주세요.")
    private String content;

    @Size(max = 5, message = "이미지는 최대 5장까지 등록 가능합니다.")
    private List<String> imageUrls;
}
