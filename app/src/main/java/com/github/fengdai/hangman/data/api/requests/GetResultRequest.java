package com.github.fengdai.hangman.data.api.requests;

public class GetResultRequest extends SessionActionRequest {

    public GetResultRequest(String sessionId) {
        super("getResult", sessionId);
    }
}
