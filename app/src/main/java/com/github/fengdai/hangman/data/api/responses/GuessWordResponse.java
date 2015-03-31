package com.github.fengdai.hangman.data.api.responses;

public class GuessWordResponse extends BaseResponse {
    public final Data data;

    public GuessWordResponse(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * gives you the word you need to guess.
         */
        public String word;

        /**
         * tells you the number of words that you have tried.
         */
        public String totalWordCount;

        /**
         * tells you the number of wrong guess you already made on this word.
         */
        public String wrongGuessCountOfCurrentWord;
    }
}
