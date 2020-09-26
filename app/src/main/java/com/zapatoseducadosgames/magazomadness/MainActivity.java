package com.zapatoseducadosgames.magazomadness;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zapatoseducadosgames.magazomadness.engine.AppConstants;
import com.zapatoseducadosgames.magazomadness.engine.FilesHandler;
import com.zapatoseducadosgames.magazomadness.engine.GameObject2D;

public class MainActivity extends AppCompatActivity{
    private RelativeLayout layout;
    private int screenWidth,screenHeight;
    private GameObject2D magazoMadnessTitle;
    private TextView highestScoreTextView;

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

        // Setting up the Title
        int titleWidth = screenWidth-(screenWidth/10);
        int titleHeight = (titleWidth*AppConstants.TITLE_ORIGINAL_HEIGHT)/AppConstants.TITLE_ORIGINAL_WIDTH;
        int[] images = {R.drawable.magazo_madness_title};
        magazoMadnessTitle = new GameObject2D(this,(screenWidth/2)-(titleWidth/2),
                (screenHeight/6)*1, titleWidth,titleHeight,images);
        layout.addView(magazoMadnessTitle);

        //Setting up the score
        setScore();

        hideSystemUI();
    }

    // Methods only called at the beginning
    private void setScore(){
        highestScoreTextView = new TextView(this);

        String highestScore = FilesHandler.readFile(AppConstants.HIGHEST_SCORE_FILE_NAME,this);

        if(highestScore.isEmpty()){
            // This means the user is playing for the first time, thus we need to create the file
            FilesHandler.writeFile(AppConstants.HIGHEST_SCORE_FILE_NAME,"0",this);
        }

        highestScore = FilesHandler.readFile(AppConstants.HIGHEST_SCORE_FILE_NAME,this);
        highestScoreTextView.setText(highestScore);
        highestScoreTextView.measure(0,0);
        highestScoreTextView.setX((screenWidth/2)-(highestScoreTextView.getMeasuredWidth()/2));
        highestScoreTextView.setY((screenHeight/2)+highestScoreTextView.getMeasuredHeight());

        layout.addView(highestScoreTextView);
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
