package com.gamebasic.runcard.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class RunCardRequest {
    // TODO (Lv 5): API 명세의 카드 필드 제약을 Bean Validation 어노테이션으로 붙이세요.

    @NotBlank()
    private String cardType;

    @Min(value = 0)
    @Max(value = 10)
    private Integer acquiredFloor;
}
