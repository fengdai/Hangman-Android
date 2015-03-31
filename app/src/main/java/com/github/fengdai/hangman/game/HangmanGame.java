package com.github.fengdai.hangman.game;

import android.text.TextUtils;

import com.github.fengdai.hangman.data.GameClient;
import com.github.fengdai.hangman.data.api.responses.GetResultResponse;
import com.github.fengdai.hangman.data.api.responses.NextWordResponse;
import com.github.fengdai.hangman.data.api.responses.StartGameResponse;
import com.github.fengdai.hangman.data.api.responses.SubmitResultResponse;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HangmanGame {
    final GameClient gameClient;
    final String playId;
    String sessionId = null;

    // Rule of the Game
    int numberOfWordsToGuess;
    int numberOfGuessAllowedForEachWord;

    // States
    int currentWordIndex = 0; // Index of current currentRound.
    Round currentRound; // Current currentRound
    int correctWordCount = 0;
    int finishedRoundCount = 0;

    // Callback
    private Callback callback;

    public HangmanGame(GameClient gameClient, String playId) {
        this.gameClient = gameClient;
        this.playId = playId;
    }

    /**
     * Start a game
     */
    public void start() {
        if (hasStarted()) {
            throw new IllegalStateException("Game is already on!");
        }
        gameClient.startGame(playId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StartGameResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(StartGameResponse startGameResponse) {
                        onGameStarted(startGameResponse);
                    }
                });
    }

    void onGameStarted(StartGameResponse startGameResponse) {
        sessionId = startGameResponse.sessionId;
        numberOfWordsToGuess = Integer.parseInt(startGameResponse.data.numberOfWordsToGuess);
        numberOfGuessAllowedForEachWord = Integer.parseInt(startGameResponse.data.numberOfGuessAllowedForEachWord);
        if (callback != null) {
            callback.onGameStarted(this);
        }
        nextRound();
    }

    /**
     * Start a new round.
     */
    public void nextRound() {
        if (!hasStarted() || isOver()) {
            return;
        }
        gameClient.nextWord(sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NextWordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(NextWordResponse nextWordResponse) {
                        onNewRound(nextWordResponse);
                    }
                });
    }

    void onNewRound(NextWordResponse nextWordResponse) {
        currentRound = new Round(this, nextWordResponse.data.word);
        currentWordIndex++;
        if (callback != null) {
            callback.onRoundStarted(this, currentRound);
        }
    }

    /**
     * Make a guess
     */
    public void guess(String guess) {
        if (currentRound == null) return;
        currentRound.guess(guess);
    }

    void onGuessResult(Round round, boolean correct) {
        if (callback != null) {
            callback.onGuessResult(this, round, correct);
        }
    }

    void onRoundFinished(Round currentRound, boolean win) {
        finishedRoundCount++;
        if (win) correctWordCount++;
        if (callback != null) {
            callback.onRoundFinished(this, currentRound, win);
        }
        if (currentWordIndex == numberOfWordsToGuess) {
            onGameFinished();
        }
    }

    void onGameFinished() {
        if (callback != null) {
            callback.onGameFinished(this);
        }
    }

    public void getResult() {
        gameClient.getResult(sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetResultResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(GetResultResponse getResultResponse) {
                        onResultReceived(getResultResponse);
                    }
                });
    }

    private void onResultReceived(GetResultResponse result) {
        if (callback != null) {
            callback.onResultReceived(this, result);
        }
    }

    public void submitResult() {
        gameClient.submitResult(sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SubmitResultResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(SubmitResultResponse submitResultResponse) {
                        onResultSubmitted(submitResultResponse);
                    }
                });
    }

    private void onResultSubmitted(SubmitResultResponse submitResultResponse) {
        if (callback != null) {
            callback.onResultSubmitted(this, submitResultResponse);
        }
    }

    public int getTotalRound() {
        return numberOfWordsToGuess;
    }

    public int getCurrentWordIndex() {
        return currentWordIndex;
    }

    public int chancePerWord() {
        return numberOfGuessAllowedForEachWord;
    }

    public int getCorrectWordCount() {
        return correctWordCount;
    }

    public int getFinishedRoundCount() {
        return finishedRoundCount;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public boolean hasStarted() {
        return !TextUtils.isEmpty(sessionId);
    }

    public boolean isOver() {
        return currentWordIndex == numberOfWordsToGuess;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onGameStarted(HangmanGame game);

        void onRoundStarted(HangmanGame game, Round round);

        void onGuessResult(HangmanGame game, Round round, boolean correct);

        void onRoundFinished(HangmanGame game, Round round, boolean win);

        void onGameFinished(HangmanGame game);

        void onResultReceived(HangmanGame game, GetResultResponse result);

        void onResultSubmitted(HangmanGame game, SubmitResultResponse submitResultResponse);
    }
}
