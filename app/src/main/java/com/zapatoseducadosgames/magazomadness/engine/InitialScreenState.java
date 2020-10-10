package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zapatoseducadosgames.magazomadness.R;

public class InitialScreenState {
    private Context context;
    private int screenWidth,screenHeight,secondsPassed;
    private RelativeLayout layout;
    private GameObject2D magazoMadnessTitle;
    private TextView highestScoreView;
    private String highestScoreStr,state;
    private Animation scaleAnimation,reverseScaleAnimation;

    public InitialScreenState(Context context,int screenWidth,int screenHeight,RelativeLayout layout){
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.layout = layout;
        secondsPassed = 0;

        // Setting the Magazo Madness Title
        // Setting up the Title
        int titleWidth = screenWidth-(screenWidth/10);
        int titleHeight = (titleWidth*AppConstants.TITLE_ORIGINAL_HEIGHT)/AppConstants.TITLE_ORIGINAL_WIDTH;
        int[] images = {R.drawable.magazo_madness_title};
        magazoMadnessTitle = new GameObject2D(context,(screenWidth/2)-(titleWidth/2),
                (screenHeight/6)*1, titleWidth,titleHeight,images);
        setAnimations();
        layout.addView(magazoMadnessTitle);
    }

    /**
     * Updates the secondsPassed variable, a variable important to know if the InitialScreen already
     * was displayed the required amount of time.
     */
    public void updateSecondsPassed(){
        secondsPassed += AppConstants.DELTA_TIME;
    }

    /**
     *
     */
    public String checkIfGameCanAlreadyStart(){
        if(secondsPassed >= AppConstants.REQUIRED_SECONDS_FOR_INITIAL_SCREEN_STATE){
            state = AppConstants.GAME_STATE;
            magazoMadnessTitle.clearAnimation();
            magazoMadnessTitle.setVisibility(View.INVISIBLE);
            highestScoreView.setVisibility(View.INVISIBLE);
            layout.removeView(highestScoreView);
            secondsPassed = 0;
            return state;
        }
        return AppConstants.INITIAL_SCREEN_STATE;
    }

    public void makeMagazoMadnessTitleVisible(){
        magazoMadnessTitle.setVisibility(View.VISIBLE);
        magazoMadnessTitle.startAnimation(scaleAnimation);
    }

    public String getHighestScoreStr(){
        return highestScoreStr;
    }

    public void setState(String state){
        this.state = state;
    }

    /**
     * Starts tha animation of the title Magazo Madness
     */
    public void makeMagazoMadnessTitleStartAnimation(){
        magazoMadnessTitle.startAnimation(scaleAnimation);
    }

    /**
     * Reads the highestScore from file and places it in the TextView highestScoreView.
     */
    public void setHighestScore(){
        highestScoreStr = FilesHandler.readFile(AppConstants.HIGHEST_SCORE_FILE_NAME,context);

        if(highestScoreStr.isEmpty()){
            FilesHandler.writeFile(AppConstants.HIGHEST_SCORE_FILE_NAME,"0",context);
            highestScoreStr = "0";
        }

        highestScoreView = new TextView(context);
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
        scaleAnimation = AnimationUtils.loadAnimation(context,R.anim.scale_main_title);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(state.equals(AppConstants.INITIAL_SCREEN_STATE)){
                    magazoMadnessTitle.startAnimation(reverseScaleAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //
        reverseScaleAnimation = AnimationUtils.loadAnimation(context,R.anim.reverse_scale_main_title);
        reverseScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(state.equals(AppConstants.INITIAL_SCREEN_STATE)){
                    magazoMadnessTitle.startAnimation(scaleAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
