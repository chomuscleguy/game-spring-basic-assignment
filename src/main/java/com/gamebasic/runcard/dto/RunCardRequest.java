package com.gamebasic.runcard.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class RunCardRequest {
    @NotBlank()
    private String cardType;

    @Min(value = 0)
    @Max(value = 10)
    private Integer acquiredFloor;
}
