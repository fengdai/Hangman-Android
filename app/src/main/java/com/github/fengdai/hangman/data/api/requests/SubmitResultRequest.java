package com.github.fengdai.hangman.data.api.requests;

public class SubmitResultRequest extends SessionActionRequest {

    public SubmitResultRequest(String sessionId) {
        super("submitResult", sessionId);
    }
}
