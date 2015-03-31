package com.github.fengdai.hangman.data;

import android.text.TextUtils;

import com.github.fengdai.hangman.data.api.responses.GetResultResponse;
import com.github.fengdai.hangman.data.api.responses.GuessWordResponse;
import com.github.fengdai.hangman.data.api.responses.NextWordResponse;
import com.github.fengdai.hangman.data.api.responses.StartGameResponse;
import com.github.fengdai.hangman.data.api.responses.SubmitResultResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GameClientTest {
    public static final String PLAY_ID = "daifeng4325416@163.com";
    private GameClient client;

    @Before
    public void setUp() {
        client = new GameClient(Robolectric.application);
    }

    @Test
    public void testStartGame() {
        StartGameResponse response = startGame();
        assertEquals(response.message, "THE GAME IS ON");
        assertNotNull(response.sessionId);
        assertNotNull(response.data);
        assertEquals("80", response.data.numberOfWordsToGuess);
        assertEquals("10", response.data.numberOfGuessAllowedForEachWord);
    }

    @Test
    public void testNextWord() {
        NextWordResponse response = nextWord(startGame().sessionId);
        assertNotNull(response.sessionId);
        assertNotNull(response.data);
        assertNotNull(response.data.word);
        assertTrue(response.data.word.length() <= 5);
    }

    @Test
    public void testGuessWord() {
        String sessionId = startGame().sessionId;
        NextWordResponse nextWord = nextWord(sessionId);
        String wordToGuess = nextWord.data.word;
        GuessWordResponse guess = guessWord(sessionId, "P");
        String wordResponse = guess.data.word;
        assertTrue(wordToGuess.length() == wordResponse.length());
        System.out.println("wordToGuess: " + wordToGuess + ", " + "result: " + wordResponse);
        if (TextUtils.equals(wordToGuess, wordResponse)) {
            // Wrong
            System.out.println("Wrong!");
            assertTrue("1".equals(guess.data.wrongGuessCountOfCurrentWord));
        } else {
            // Right
            System.out.println("Right!");
            assertTrue("0".equals(guess.data.wrongGuessCountOfCurrentWord));
        }
    }

    @Test
    public void testGetResult() {
        String sessionId = startGame().sessionId;
        GetResultResponse.Data result = getResult(sessionId).data;
        System.out.println("totalWordCount: " + result.totalWordCount +
                ", correctWordCount: " + result.correctWordCount +
                ", totalWrongGuessCount: " + result.totalWrongGuessCount +
                ", score: " + result.score);
    }

    @Test
    public void testSubmitResult() {
        String sessionId = startGame().sessionId;
        SubmitResultResponse.Data result = submitResult(sessionId).data;
        assertEquals(PLAY_ID, result.playerId);
        assertEquals(sessionId, result.sessionId);
        System.out.println("totalWordCount: " + result.totalWordCount +
                ", correctWordCount: " + result.correctWordCount +
                ", totalWrongGuessCount: " + result.totalWrongGuessCount +
                ", score: " + result.score +
                ", datetime: " + result.datetime);
    }

    private StartGameResponse startGame() {
        return client.startGame(PLAY_ID).toBlocking().single();
    }

    private NextWordResponse nextWord(String sessionId) {
        return client.nextWord(sessionId).toBlocking().single();
    }

    private GuessWordResponse guessWord(String sessionId, String guess) {
        return client.guessWord(sessionId, guess).toBlocking().single();
    }

    private GetResultResponse getResult(String sessionId) {
        return client.getResult(sessionId).toBlocking().single();
    }

    private SubmitResultResponse submitResult(String sessionId) {
        return client.submitResult(sessionId).toBlocking().single();
    }
}
