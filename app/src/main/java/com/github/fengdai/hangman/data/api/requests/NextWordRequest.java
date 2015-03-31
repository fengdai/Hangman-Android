package com.github.fengdai.hangman.data.api.requests;

public class NextWordRequest extends SessionActionRequest {

    public NextWordRequest(String sessionId) {
        super("nextWord", sessionId);
    }
}
