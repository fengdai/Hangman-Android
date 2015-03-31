/*
 * 文件名: Round
 * 版    权：  Copyright Paitao Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: daifeng
 * 创建时间:15/3/10
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.github.fengdai.hangman.game;

import android.text.TextUtils;

import com.github.fengdai.hangman.data.api.responses.GuessWordResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A round of word guess.
 */
public class Round {
    final HangmanGame game;
    String currentWord;
    int wrongGuessCount = 0;
    boolean isGuessing = false;

    // Store letters that have been guess;
    List<String> guessedLetters = new ArrayList<String>();

    Round(HangmanGame game, String word) {
        this.game = game;
        currentWord = word;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getIncorrectGuessCount() {
        return wrongGuessCount;
    }

    public void guess(final String letter) {
        if (!canGuess(letter)) return;
        isGuessing = true;
        game.gameClient.guessWord(game.sessionId, letter.toUpperCase(Locale.US))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuessWordResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(GuessWordResponse guessWordResponse) {
                        guessedLetters.add(letter);
                        onGuessResult(guessWordResponse);
                    }
                });
    }

    public boolean canGuess(String letter) {
        return !isOver() && !isGuessing && !hasGuessed(letter);
    }

    boolean hasGuessed(String letter) {
        return guessedLetters.contains(letter);
    }

    private void onGuessResult(GuessWordResponse guessWordResponse) {
        isGuessing = false;
        String guessResult = guessWordResponse.data.word;
        if (TextUtils.equals(guessResult, currentWord)) {
            wrongGuessCount++;
            game.onGuessResult(Round.this, false);
        } else {
            currentWord = guessResult;
            game.onGuessResult(Round.this, true);
        }
        if (isOver()) {
            game.onRoundFinished(this, !currentWord.contains("*"));
        }
    }

    boolean isOver() {
        return game.numberOfGuessAllowedForEachWord <= wrongGuessCount
                || !currentWord.contains("*");
    }
}
