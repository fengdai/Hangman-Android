package com.github.fengdai.hangman.data.api.responses;

public class StartGameResponse extends BaseResponse {
    public final Data data;

    public StartGameResponse(Data data) {
        this.data = data;
    }

    public static class Data {
        /**
         * tells you how many words you will have to guess to finish the game.
         */
        public String numberOfWordsToGuess;

        /**
         * tells you how many INCORRECT guess you may have for each word.
         */
        public String numberOfGuessAllowedForEachWord;
    }
}
