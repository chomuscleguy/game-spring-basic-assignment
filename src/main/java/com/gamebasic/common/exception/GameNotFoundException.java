package com.gamebasic.common.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("게임을 찾을 수 없습니다. id=" + gameId);
    }
}
