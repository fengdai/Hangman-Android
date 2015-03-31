package com.github.fengdai.hangman.data.api;

import com.github.fengdai.hangman.data.api.requests.GetResultRequest;
import com.github.fengdai.hangman.data.api.requests.GuessWordRequest;
import com.github.fengdai.hangman.data.api.requests.NextWordRequest;
import com.github.fengdai.hangman.data.api.requests.SessionActionRequest;
import com.github.fengdai.hangman.data.api.requests.StartGameRequest;
import com.github.fengdai.hangman.data.api.responses.GetResultResponse;
import com.github.fengdai.hangman.data.api.responses.GuessWordResponse;
import com.github.fengdai.hangman.data.api.responses.NextWordResponse;
import com.github.fengdai.hangman.data.api.responses.StartGameResponse;
import com.github.fengdai.hangman.data.api.responses.SubmitResultResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface HangmanService {
    @POST("/")
    Observable<StartGameResponse> startGame(@Body StartGameRequest startGameRequest);

    @POST("/")
    Observable<NextWordResponse> nextWord(@Body NextWordRequest giveMeAWordRequest);

    @POST("/")
    Observable<GuessWordResponse> guessWord(@Body GuessWordRequest makeAGuessRequest);

    @POST("/")
    Observable<GetResultResponse> getResult(@Body GetResultRequest getResultRequest);

    @POST("/")
    Observable<SubmitResultResponse> submitResult(@Body SessionActionRequest submitResultRequest);
}
