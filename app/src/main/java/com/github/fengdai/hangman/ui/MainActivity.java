package com.github.fengdai.hangman.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.fengdai.hangman.R;
import com.github.fengdai.hangman.data.GameClient;
import com.github.fengdai.hangman.data.api.responses.GetResultResponse;
import com.github.fengdai.hangman.data.api.responses.SubmitResultResponse;
import com.github.fengdai.hangman.game.HangmanGame;
import com.github.fengdai.hangman.game.Round;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity
        implements KeyBoardView.OnKeyClickListener, HangmanGame.Callback {
    public static final String PLAY_ID = "daifeng4325416@163.com";
    private HangmanGame hangmanGame;

    // Views
    @InjectView(R.id.currentWord)
    TextView currentWordView;

    @InjectView(R.id.keyBoard)
    KeyBoardView keyBoardView;

    @InjectView(R.id.round)
    TextView roundView;

    @InjectView(R.id.chance)
    TextView chanceView;

    @InjectView(R.id.correctWordCount)
    TextView correctWordCountView;

    @InjectView(R.id.score)
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        keyBoardView.setOnKeyClickListener(this);
        startNewGame();
    }

    private void startNewGame() {
        hangmanGame = new HangmanGame(new GameClient(this), PLAY_ID);
        hangmanGame.setCallback(this);
        hangmanGame.start();
    }

    private void showCurrentWord(Round round) {
        // Replace "*" with "_" and inset space between each letter.
        StringBuilder sb = new StringBuilder();
        for (char letter : round.getCurrentWord().toCharArray()) {
            letter = letter == '*' ? '_' : letter;
            sb.append(letter).append("  ");
        }
        currentWordView.setText(sb);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hangmanGame.setCallback(null);
    }

    @Override
    public boolean onKeyClicked(TextView keyView) {
        Round currentRound = hangmanGame.getCurrentRound();
        String guess = keyView.getText().toString();
        boolean canGuess = currentRound != null && currentRound.canGuess(guess);
        if (canGuess) {
            currentRound.guess(guess);
        }
        return canGuess;
    }

    @Override
    public void onGameStarted(HangmanGame game) {
        invalidateOptionsMenu();
        showHud(game);
        score.setText("0");
    }

    @Override
    public void onRoundStarted(HangmanGame game, Round round) {
        showHud(game);
        showCurrentWord(round);
    }

    private void showHud(HangmanGame game) {
        roundView.setText(game.getCurrentWordIndex() + "/" + game.getTotalRound());
        keyBoardView.reset();
        chanceView.setText(game.chancePerWord() + "");
        correctWordCountView.setText(game.getCorrectWordCount() + "/" + game.getFinishedRoundCount());
    }

    @Override
    public void onGuessResult(HangmanGame game, Round round, boolean correct) {
        if (!correct) {
            chanceView.setText((game.chancePerWord() - round.getIncorrectGuessCount()) + "");
        }
        game.getResult();
        showCurrentWord(round);
    }

    @Override
    public void onRoundFinished(final HangmanGame game, Round round, boolean win) {
        showHud(game);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Round Over")
                .setMessage(win ? "YOU WIN!!!!!" : "YOU LOSE...")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Next Word", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.nextRound();
                    }
                })
                .setNeutralButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGame();
                    }
                })
                .create().show();
    }

    @Override
    public void onGameFinished(HangmanGame game) {
        this.hangmanGame.getResult();
    }

    @Override
    public void onResultReceived(final HangmanGame game, GetResultResponse result) {
        GetResultResponse.Data data = result.data;
        score.setText(String.valueOf(data.score));
        if (!game.isOver()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Submit you score?")
                .setMessage("Correct: " + data.correctWordCount + "/" + data.totalWordCount + "\n\n Score: " + data.score)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.submitResult();
                    }
                })
                .setNeutralButton("New game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGame();
                    }
                })
                .create().show();
    }

    @Override
    public void onResultSubmitted(final HangmanGame game, SubmitResultResponse submitResultResponse) {
        SubmitResultResponse.Data result = submitResultResponse.data;
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Submitted")
                .setMessage("Correct: " + result.correctWordCount + "/" + result.totalWordCount
                        + "\nTotal Wrong Guess Count: " + result.totalWrongGuessCount +
                        "\nScore: " + result.score)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNewGame();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(hangmanGame.hasStarted() ?
                R.menu.menu_game_started : R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_game:
                startNewGame();
                return true;
            case R.id.action_next_word:
                hangmanGame.nextRound();
                return true;
            case R.id.action_submit_result:
                hangmanGame.submitResult();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
