package com.gamebasic.runcard.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CardResponse {
    // TODO (Lv 5): API 명세의 카드 응답 JSON에 맞게 필드를 만들고 생성자에서 채우세요.
    private final Long id;
    private final String cardType;
    private final Integer acquiredFloor;

//    public CardResponse(Long id, String cardType, int acquiredFloor) {
//        this.id = id;
//        this.cardType = cardType;
//        this.acquiredFloor = acquiredFloor;
//    }
}
