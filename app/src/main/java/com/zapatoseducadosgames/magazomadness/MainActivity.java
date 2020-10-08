package com.zapatoseducadosgames.magazomadness;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zapatoseducadosgames.magazomadness.engine.AppConstants;
import com.zapatoseducadosgames.magazomadness.engine.FilesHandler;
import com.zapatoseducadosgames.magazomadness.engine.GameObject2D;
import com.zapatoseducadosgames.magazomadness.engine.GameOverScreenManager;
import com.zapatoseducadosgames.magazomadness.engine.GameScreenManager;

public class MainActivity extends AppCompatActivity{
    private RelativeLayout layout;
    private int screenWidth,screenHeight,secondsPassed;
    private GameObject2D magazoMadnessTitle;
    private TextView highestScoreView;
    private String state,highestScoreStr;
    private GameScreenManager gameScreenManager;
    private GameOverScreenManager gameOverScreenManager;
    private Animation scaleAnimation,reverseScaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        layout = findViewById(R.id.relative_layout);
        secondsPassed = 0;

        // Setting the background image
        getWindow().setBackgroundDrawableResource(R.drawable.background);

        // Getting the screen's resolution of the current device
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        // Setting up the managers for each game state
        gameScreenManager = new GameScreenManager(screenWidth,screenHeight,this,layout,this);
        gameOverScreenManager = new GameOverScreenManager(this,screenWidth,screenHeight,layout);

        // Setting up the Title
        int titleWidth = screenWidth-(screenWidth/10);
        int titleHeight = (titleWidth*AppConstants.TITLE_ORIGINAL_HEIGHT)/AppConstants.TITLE_ORIGINAL_WIDTH;
        int[] images = {R.drawable.magazo_madness_title};
        magazoMadnessTitle = new GameObject2D(this,(screenWidth/2)-(titleWidth/2),
                (screenHeight/6)*1, titleWidth,titleHeight,images);
        setAnimations();
        layout.addView(magazoMadnessTitle);

        // Setting the initial state
        state = AppConstants.INITIAL_SCREEN_STATE;

        // Set up the files
        setHighestScore();

        hideSystemUI();
        startGame();
    }

    private void startGame(){
        magazoMadnessTitle.startAnimation(scaleAnimation);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{Thread.sleep(AppConstants.DELTA_TIME);}catch(InterruptedException e){e.printStackTrace();}
                    update();
                    render();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void update(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(state){
                    case AppConstants.INITIAL_SCREEN_STATE:
                        secondsPassed += AppConstants.DELTA_TIME;
                    break;
                    case AppConstants.GAME_STATE:
                        state = gameScreenManager.update();
                    break;
                    case AppConstants.GAME_OVER_STATE:
                        gameOverScreenManager.update();
                    break;
                    default:
                        System.out.println("******** There was an error reading the game state " +
                                " in update method **********");
                }
            }
        });
    }

    private void render(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(state){
                    case AppConstants.INITIAL_SCREEN_STATE:
                    break;
                    case AppConstants.GAME_STATE:
                    break;
                    case AppConstants.GAME_OVER_STATE:
                    break;
                    default:
                        System.out.println("******** There was an error reading the game state " +
                                " in render method **********");
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        switch(state){
            case AppConstants.INITIAL_SCREEN_STATE:
                if(secondsPassed >= AppConstants.REQUIRED_SECONDS_FOR_INITIAL_SCREEN_STATE){
                    state = AppConstants.GAME_STATE;
                    magazoMadnessTitle.clearAnimation();
                    magazoMadnessTitle.setVisibility(View.INVISIBLE);
                    highestScoreView.setVisibility(View.INVISIBLE);
                    layout.removeView(highestScoreView);
                    gameScreenManager.setArchitectureStyle();
                    gameScreenManager.setHighestScore(highestScoreStr);
                    secondsPassed = 0;
                }
            break;
            case AppConstants.GAME_STATE:
            break;
            case AppConstants.GAME_OVER_STATE:
                if(gameOverScreenManager.isReadyToGoBackToInitialScreen()){
                    state = AppConstants.INITIAL_SCREEN_STATE;
                    magazoMadnessTitle.setVisibility(View.VISIBLE);
                    magazoMadnessTitle.startAnimation(scaleAnimation);
                    setHighestScore();
                    gameOverScreenManager.reset();
                }
            break;
            default:
                System.out.println("******** There was an error reading the game state " +
                        " in onTouchEvent method **********");
        }
        return true;
    }

    private void setHighestScore(){
        highestScoreStr = FilesHandler.readFile(AppConstants.HIGHEST_SCORE_FILE_NAME,this);

        if(highestScoreStr.isEmpty()){
            FilesHandler.writeFile(AppConstants.HIGHEST_SCORE_FILE_NAME,"0",this);
            highestScoreStr = "0";
        }

        highestScoreView = new TextView(this);
        highestScoreView.setText(highestScoreStr);
        highestScoreView.setTypeface(Typeface.SANS_SERIF);
        highestScoreView.setTextSize(AppConstants.HIGHEST_SCORE_TEXT_SIZE);
        highestScoreView.setTextColor(Color.parseColor(AppConstants.HIGHEST_SCORE_TEXT_COLOR));
        highestScoreView.measure(0,0);
        highestScoreView.setX((screenWidth/2)-(highestScoreView.getMeasuredWidth()/2));
        highestScoreView.setY((screenHeight/2)-(highestScoreView.getMeasuredHeight()/2));

        layout.addView(highestScoreView);
    }

    /**
     * Sets the animations objects that will be used in the magazoMadnessTitle,
     * The magazoMadnessTitle will grow and shrink constantly.
     */
    private void setAnimations(){
        scaleAnimation = AnimationUtils.loadAnimation(this,R.anim.scale_main_title);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                magazoMadnessTitle.startAnimation(reverseScaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //
        reverseScaleAnimation = AnimationUtils.loadAnimation(this,R.anim.reverse_scale_main_title);
        reverseScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                magazoMadnessTitle.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {// Windows has focus
            hideSystemUI();

        }else{// Windows does not have focus

        }
    }

    // Hides the systemsUI
    private void hideSystemUI(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
