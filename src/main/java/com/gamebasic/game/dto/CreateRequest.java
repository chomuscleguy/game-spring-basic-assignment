package com.gamebasic.game.dto;

import com.gamebasic.runcard.dto.RunCardRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

// 완성된 코드입니다. 수정하지 마세요.
@Getter
public class CreateRequest {
    @NotBlank
    @Size(min = 2, max = 12)
    private String playerName;

    @NotEmpty
    private List<@Valid RunCardRequest> deck;
}
