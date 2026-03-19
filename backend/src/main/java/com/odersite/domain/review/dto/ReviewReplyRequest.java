package com.odersite.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewReplyRequest {

    @NotBlank
    @Size(max = 1000)
    private String content;
}
