package com.zapatoseducadosgames.magazomadness;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.engine.AppConstants;
import com.zapatoseducadosgames.magazomadness.engine.GameOverScreenManager;
import com.zapatoseducadosgames.magazomadness.engine.GameScreenManager;
import com.zapatoseducadosgames.magazomadness.engine.InitialScreenState;

public class MainActivity extends AppCompatActivity{
    private RelativeLayout layout;
    private int screenWidth,screenHeight;
    private String state;
    private GameScreenManager gameScreenManager;
    private GameOverScreenManager gameOverScreenManager;
    private InitialScreenState initialScreenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        layout = findViewById(R.id.relative_layout);

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
        initialScreenManager = new InitialScreenState(this,screenWidth,screenHeight,layout);

        // Setting the initial state
        state = AppConstants.INITIAL_SCREEN_STATE;

        // Set up the files
        initialScreenManager.setHighestScore();

        hideSystemUI();
        startGame();
    }

    private void startGame(){
        initialScreenManager.makeMagazoMadnessTitleStartAnimation();
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
                initialScreenManager.setState(state);
                switch(state){
                    case AppConstants.INITIAL_SCREEN_STATE:
                        initialScreenManager.updateSecondsPassed();
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
                state = initialScreenManager.checkIfGameCanAlreadyStart();
                gameScreenManager.setArchitectureStyle();
                gameScreenManager.setHighestScore(initialScreenManager.getHighestScoreStr());
            break;
            case AppConstants.GAME_STATE:
            break;
            case AppConstants.GAME_OVER_STATE:
                if(gameOverScreenManager.isReadyToGoBackToInitialScreen()){
                    state = AppConstants.INITIAL_SCREEN_STATE;
                    initialScreenManager.makeMagazoMadnessTitleVisible();
                    initialScreenManager.setHighestScore();
                    gameOverScreenManager.reset();
                }
            break;
            default:
                System.out.println("******** There was an error reading the game state " +
                        " in onTouchEvent method **********");
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {// Windows has focus
            hideSystemUI();

        }else{// Windows does not have focus
            if(state.equals(AppConstants.GAME_STATE)){
                gameScreenManager.pauseGame();
            }
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
