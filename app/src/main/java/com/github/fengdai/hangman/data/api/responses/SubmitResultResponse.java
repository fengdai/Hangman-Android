package com.github.fengdai.hangman.data.api.responses;

public class SubmitResultResponse extends BaseResponse {
    public final Data data;

    public SubmitResultResponse(Data data) {
        this.data = data;
    }

    public static class Data {
        public String playerId;
        public String sessionId;
        public String totalWordCount;
        public String correctWordCount;
        public String totalWrongGuessCount;
        public String score;
        public String datetime;
    }
}
