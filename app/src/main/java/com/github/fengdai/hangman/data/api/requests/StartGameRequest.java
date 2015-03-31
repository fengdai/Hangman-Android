package com.github.fengdai.hangman.data.api.requests;

public class StartGameRequest extends ActionRequest {
    public final String playerId;

    public StartGameRequest(String playerId) {
        super("startGame");
        this.playerId = playerId;
    }
}
