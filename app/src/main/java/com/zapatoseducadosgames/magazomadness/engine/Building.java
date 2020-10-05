package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.R;

public class Building extends AppCompatImageView {
    public static final int ORIGINAL_WIDTH = 100;
    public static final int ORIGINAL_HEIGHT = 100;
    public static final int CITY_MAX_LEVELS = 7;
    public static final int CITY_MAX_NUMBER_OF_BUILDINGS = 7;
    private float xPos,yPos;
    private int buildingWidth,buildingHeight,screenWidth,screenHeight;

    /**
     * In the constructor, the specific sprites are set.
     * @param context - Context for View
     * @param building - Contains the architecture style.
     * @param verticalPosition - Indicates the height of the cityBlock, so we can choose between
     *                         base, middle or top
     */
    public Building(Context context,String building,int verticalPosition){
        super(context);

        if(building.equals(AppConstants.DARK_WET_BUILDING)){
            if(verticalPosition == 1){//This means the base of the building id being created.
                setImageResource(R.drawable.dark_wet_building_base);
            }else if(verticalPosition == CITY_MAX_LEVELS){// This means the top of the building is being created.
                setImageResource(R.drawable.dark_wet_building_top);
            }else{//This means the half of the building is being created
                setImageResource(R.drawable.dark_wet_building_half);
            }
        }
    }

    /**
     * Sets the screenWidth and the screenHeight of the Android device being used
     * @param screenWidth - Integer, screenWidth
     * @param screenHeight - Integer, screenHeight
     */
    public void setScreenWidthAndHeight(int screenWidth,int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * Sets the width and height of the cityBlocks
     * @param buildingWidth - Integer, building width
     * @param buildingHeight - Integer, building height
     */
    private void setBuildingWidthAndHeight(int buildingWidth,int buildingHeight){
        this.requestLayout();
        this.buildingWidth = buildingWidth;
        this.buildingHeight = buildingHeight;
        this.setLayoutParams(new RelativeLayout.LayoutParams(this.buildingWidth,this.buildingHeight));
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    /**
     * Sets the x and y coordinates of the cityBlock taking in consideration the vertical and
     * horizontal positions.
     * @param horizontalPosition - CityBlock horizontal position.
     * @param verticalHeight - CityBlock vertical position.
     * @param screenHeight - Screen height of the Android device being used.
     */
    public void setCoordinates(int horizontalPosition,int verticalHeight,float screenHeight){
        float cityHeight = screenHeight-(screenHeight/2);
        float cityIndividualLevel = cityHeight/CITY_MAX_LEVELS;

        float cityIndividualWidth = screenWidth/CITY_MAX_NUMBER_OF_BUILDINGS;
        setBuildingWidthAndHeight((int)cityIndividualWidth,
                (int)(cityIndividualLevel));
        //Setting yPos
        setYPos(screenHeight-(cityIndividualLevel*verticalHeight));

        //Setting xPos
        setXPos((cityIndividualWidth*(horizontalPosition-1)));
    }

    public void setXPos(float xPos){
        this.xPos = xPos;
        this.setX(xPos);
    }

    public float getXPos(){
        return xPos;
    }

    public void setYPos(float yPos){
        this.yPos = yPos;
        this.setY(yPos);
    }

    public float getYPos(){
        return yPos;
    }

    public int getBuildingWidth(){
        return buildingWidth;
    }

    public int getBuildingHeight(){
        return buildingHeight;
    }

    public void update(){

    }

    public void render(){

    }
}

