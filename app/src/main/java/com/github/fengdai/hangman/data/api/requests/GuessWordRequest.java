package com.github.fengdai.hangman.data.api.requests;

public class GuessWordRequest extends SessionActionRequest {
    public final String guess;

    public GuessWordRequest(String sessionId, String guess) {
        super("guessWord", sessionId);
        this.guess = guess;
    }
}
