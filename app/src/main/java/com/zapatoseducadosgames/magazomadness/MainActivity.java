package com.zapatoseducadosgames.magazomadness;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zapatoseducadosgames.magazomadness.engine.AppConstants;
import com.zapatoseducadosgames.magazomadness.engine.FilesHandler;
import com.zapatoseducadosgames.magazomadness.engine.GameObject2D;
import com.zapatoseducadosgames.magazomadness.engine.GameScreenManager;

public class MainActivity extends AppCompatActivity{
    private RelativeLayout layout;
    private int screenWidth,screenHeight;
    private GameObject2D magazoMadnessTitle;
    private String state;
    private GameScreenManager gameScreenManager;

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
        gameScreenManager = new GameScreenManager(screenWidth,screenHeight,this,layout);

        // Setting up the Title
        int titleWidth = screenWidth-(screenWidth/10);
        int titleHeight = (titleWidth*AppConstants.TITLE_ORIGINAL_HEIGHT)/AppConstants.TITLE_ORIGINAL_WIDTH;
        int[] images = {R.drawable.magazo_madness_title};
        magazoMadnessTitle = new GameObject2D(this,(screenWidth/2)-(titleWidth/2),
                (screenHeight/6)*1, titleWidth,titleHeight,images);
        layout.addView(magazoMadnessTitle);

        // Setting the initial state
        state = AppConstants.INITIAL_SCREEN_STATE;

        hideSystemUI();
        startGame();
    }

    private void startGame(){
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
                    break;
                    case AppConstants.GAME_STATE:
                        gameScreenManager.update();
                    break;
                    case AppConstants.EXPLOSION_STATE:
                    break;
                    case AppConstants.GAME_OVER_STATE:
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
                    case AppConstants.EXPLOSION_STATE:
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
                state = AppConstants.GAME_STATE;
                gameScreenManager.setArchitectureStyle();
            break;
            case AppConstants.GAME_STATE:
            break;
            case AppConstants.EXPLOSION_STATE:
            break;
            case AppConstants.GAME_OVER_STATE:
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
