package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.R;

public class GameOverScreenManager{
    private Context context;
    private int screenWidth;
    private int screenHeight,secondsPassed;
    private GameObject2D gameOverImage;
    private boolean gameOverImageSet;
    private RelativeLayout layout;

    public GameOverScreenManager(Context context,int screenWidth,int screenHeight,RelativeLayout layout){
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.layout = layout;
        secondsPassed = 0;
        gameOverImageSet = false;
    }

    /**
     * Updates all the elements related to GameOverScreen
     */
    public void update(){
        secondsPassed += AppConstants.DELTA_TIME;
        if(!gameOverImageSet){
            setGameOverMessage();
        }
    }

    /**
     * Creates and places the image saying GameOver
     */
    private void setGameOverMessage(){
        int imageWidth = screenWidth-(screenWidth/10);
        int imageHeight = (imageWidth*AppConstants.GAME_OVER_IMAGE_ORIGINAL_HEIGHT)
                /AppConstants.GAME_OVER_IMAGE_ORIGINAL_WIDTH;
        int[] gameOverImages = {R.drawable.game_over_title};
        gameOverImage = new GameObject2D(context,((screenWidth/2)-(imageWidth/2)),
                ((screenHeight/2)-(imageHeight/2)),imageWidth,imageHeight,gameOverImages);

        layout.addView(gameOverImage);
        gameOverImageSet = true;
    }

    /**
     * Leaves everything ready for the next time GameOverScreen is activated
     */
    public void reset(){
        gameOverImageSet = false;
        secondsPassed = 0;
        layout.removeView(gameOverImage);
        gameOverImage = null;
    }

    /**
     * Checks if GameOverScreen is ready to move on to InitialScreen
     * @return True or False.
     */
    public boolean isReadyToGoBackToInitialScreen(){
        if(secondsPassed >= AppConstants.REQUIRED_SECONDS_FOR_GAME_OVER_SCREEN){
            return true;
        }
        return false;
    }
}
