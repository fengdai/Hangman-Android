package com.github.fengdai.hangman.data.api.requests;

public class SessionActionRequest extends ActionRequest {
    public final String sessionId;

    public SessionActionRequest(String action, String sessionId) {
        super(action);
        this.sessionId = sessionId;
    }
}
