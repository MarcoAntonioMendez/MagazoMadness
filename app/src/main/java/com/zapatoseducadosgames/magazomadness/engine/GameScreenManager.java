package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.widget.RelativeLayout;

import java.util.Random;

public class GameScreenManager {
    private String architectureStyle;
    private String[] allArchitectureStyles = {AppConstants.DARK_WET_BUILDING};
    private Random random;
    private City city;
    private int secondsPassed;
    private RelativeLayout layout;

    public GameScreenManager(int screenWidth,int screenHeight,Context context,RelativeLayout layout){
        architectureStyle = AppConstants.DARK_WET_BUILDING;
        this.layout = layout;

        city = new City(screenWidth,screenHeight,context);
        secondsPassed = 0;

        random = new Random();
    }

    /**
     * Chooses one style randomly for the buildings style.
     */
    public void setArchitectureStyle(){
        architectureStyle = allArchitectureStyles[random.nextInt(allArchitectureStyles.length)];
    }

    /**
     * Updates all objects that are active during the game state
     */
    public void update(){
        secondsPassed += AppConstants.DELTA_TIME;

        int newCityBlockTimeStamp = city.getCity().size()*AppConstants.NEW_CITY_BLOCK_TIME_INTERVAL;
        int cityBlockHorizontalPosition,cityBlockVerticalPosition;
        Building newCityBlock = null;

        if(secondsPassed >= newCityBlockTimeStamp){// If this condition is met, a new CityBlock will be created
            // Selecting cityBlock positions
            cityBlockHorizontalPosition = city.chooseHorizontalPosition();
            cityBlockVerticalPosition = city.chooseVerticalPosition(cityBlockHorizontalPosition);

            // Creating the cityBlock
            // If cityBlockVerticalPosition is -1, it means the tower is full
            if(cityBlockVerticalPosition != -1){
                newCityBlock = city.createCityBlock(cityBlockHorizontalPosition+1,
                        City.CITY_HEIGHT-cityBlockVerticalPosition,
                        architectureStyle);
                layout.addView(newCityBlock);
            }
        }
    }

    public void render(){}
}
