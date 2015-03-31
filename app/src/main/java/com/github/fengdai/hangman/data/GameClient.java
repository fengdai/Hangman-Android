package com.github.fengdai.hangman.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.github.fengdai.hangman.data.api.HangmanService;
import com.github.fengdai.hangman.data.api.requests.GetResultRequest;
import com.github.fengdai.hangman.data.api.requests.GuessWordRequest;
import com.github.fengdai.hangman.data.api.requests.NextWordRequest;
import com.github.fengdai.hangman.data.api.requests.StartGameRequest;
import com.github.fengdai.hangman.data.api.requests.SubmitResultRequest;
import com.github.fengdai.hangman.data.api.responses.GetResultResponse;
import com.github.fengdai.hangman.data.api.responses.GuessWordResponse;
import com.github.fengdai.hangman.data.api.responses.NextWordResponse;
import com.github.fengdai.hangman.data.api.responses.StartGameResponse;
import com.github.fengdai.hangman.data.api.responses.SubmitResultResponse;

import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import rx.Observable;

public class GameClient {
    public static final String API_URL = "https://strikingly-hangman.herokuapp.com/game/on";
    final Context application;
    final HangmanService hangmanService;
    final Handler handler = new Handler(Looper.getMainLooper());

    class MyErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(final RetrofitError cause) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application, cause.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return cause;
        }
    }

    public GameClient(Context context) {
        this.application = context.getApplicationContext();
        RestAdapter restAdapter = new RestAdapter.Builder() //
                .setClient(new OkClient()) //
                .setEndpoint(Endpoints.newFixedEndpoint(API_URL)) //
                .setErrorHandler(new MyErrorHandler()) //
                .build();
        hangmanService = restAdapter.create(HangmanService.class);
    }

    public Observable<StartGameResponse> startGame(String playId) {
        return hangmanService.startGame(new StartGameRequest(playId));
    }

    public Observable<NextWordResponse> nextWord(String sessionId) {
        return hangmanService.nextWord(new NextWordRequest(sessionId));
    }

    public Observable<GuessWordResponse> guessWord(String sessionId, String guess) {
        return hangmanService.guessWord(new GuessWordRequest(sessionId, guess));
    }

    public Observable<GetResultResponse> getResult(String sessionId) {
        return hangmanService.getResult(new GetResultRequest(sessionId));
    }

    public Observable<SubmitResultResponse> submitResult(String sessionId) {
        return hangmanService.submitResult(new SubmitResultRequest(sessionId));
    }
}
