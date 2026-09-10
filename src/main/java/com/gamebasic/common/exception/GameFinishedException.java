package com.gamebasic.common.exception;

public class GameFinishedException extends RuntimeException {
    public GameFinishedException(Long gameId) {
        super("이미 끝난 여정은 진행을 저장할 수 없습니다. id=" + gameId);
    }
}
