package com.github.fengdai.hangman.data.api.responses;

public class GetResultResponse extends BaseResponse {
    public final Data data;

    public GetResultResponse(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * the total number of words you tried.
         */
        public String totalWordCount;

        /**
         * the total number of words you guess correctly.
         */
        public String correctWordCount;

        /**
         * the total number of Wrong guess you have made.
         */
        public String totalWrongGuessCount;

        /**
         * your score!
         */
        public String score;
    }
}
