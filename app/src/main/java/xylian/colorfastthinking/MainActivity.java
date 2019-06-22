package xylian.colorfastthinking;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import static xylian.colorfastthinking.Utils.randomWithRange;

public class MainActivity extends AppCompatActivity {

    final static String[][] colorPool = {
        { "Black", "#000000" },
        { "White", "#ffffff" },
        { "Red", "#ff0000" },
        { "Yellow", "#ffff00" },
        { "Purple", "#cc00cc" },
        { "Green", "#00cc00" },
        { "Orange", "#ff6600" },
        { "Blue", "#0000cc" }
    };

    int score = 0, highscore = 0;

    RelativeLayout colorMonitorBackground;
    TextView colorMonitorText;
    TextView scoreTextView;
    TextView highscoreTextView;
    TextView timerTextView;
    Button trueButton;
    Button falseButton;
    String randomText_colorPool, randomBackground_colorPool;
    int randomText_colorPoolIndex, randomBackground_colorPoolIndex, time = 1000, resetTime = 1000;

    private void randomColorMonitor() {
        randomText_colorPoolIndex = randomWithRange(0, colorPool.length -1);
        if (randomWithRange(0, 1) == 1) {
            randomBackground_colorPoolIndex = randomWithRange(0, colorPool.length -1);
        } else {
            randomBackground_colorPoolIndex = randomText_colorPoolIndex;
        }
        randomText_colorPool = colorPool[randomText_colorPoolIndex][0];
        randomBackground_colorPool = colorPool[randomBackground_colorPoolIndex][1];

        colorMonitorText.setText(randomText_colorPool);
        colorMonitorBackground.setBackgroundColor(Color.parseColor(randomBackground_colorPool));
    }

    private void buttonMethod(boolean bool) {
        if ( (bool && randomText_colorPoolIndex == randomBackground_colorPoolIndex)
                || (!bool && randomText_colorPoolIndex != randomBackground_colorPoolIndex) ) {
            // user choice was right, give points
            score++;
            if (highscore < score) {
                highscore = score;
            }
        } else {
            score = 0;
        }
        time = resetTime;

        scoreTextViews();
        savePreferences("SCORE", String.valueOf(score));
        savePreferences("HIGHSCORE", String.valueOf(highscore));
        randomColorMonitor();
    }

    @SuppressLint("SetTextI18n")
    private void scoreTextViews() {
        scoreTextView.setText("Score: " + score);
        highscoreTextView.setText("Highscore: " + highscore);
    }

    public void startCountdownTimer() {
        System.out.println("startCountdownTimer()");

        Thread countdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    System.out.println("startCountdownTimer() time: " + time);

                    if (time <= 0) {
                        time = resetTime;
                        score = 0;
                        savePreferences("SCORE", String.valueOf(score));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scoreTextViews();
                                randomColorMonitor();
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerTextView.setText(String.valueOf(( (double) time ) / 1000));
                        }
                    });

                    time -= 100;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }
            }
        });
        countdownThread.start();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        try {
            score = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("SCORE", "")));
        } catch (Exception e) {
            score = 0;
        }
        try {
            highscore = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString("HIGHSCORE", "")));
        } catch (Exception e) {
            highscore = 0;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
        setContentView(R.layout.activity_main);

        colorMonitorBackground = findViewById(R.id.colorMonitorBackground);
        colorMonitorText = findViewById(R.id.colorMonitorText);
        scoreTextView = findViewById(R.id.score);
        highscoreTextView = findViewById(R.id.highscore);
        timerTextView = findViewById(R.id.timer);
        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);

        scoreTextViews();
        randomColorMonitor();
        startCountdownTimer();

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonMethod(true);
            }
        });
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonMethod(false);
            }
        });


    }
}
