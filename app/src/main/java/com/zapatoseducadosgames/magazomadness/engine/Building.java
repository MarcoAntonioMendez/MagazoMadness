package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.R;

public class Building extends AppCompatImageView {
    public static final int ORIGINAL_WIDTH = 100;
    public static final int ORIGINAL_HEIGHT = 100;
    public static final int CITY_MAX_LEVELS = 7;
    public static final int CITY_MAX_NUMBER_OF_BUILDINGS = 7;
    private float xPos,yPos;
    private int buildingWidth,buildingHeight,screenWidth,screenHeight;

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

    public void setScreenWidthAndHeight(int screenWidth,int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    private void setBuildingWidthAndHeight(int buildingWidth,int buildingHeight){
        this.requestLayout();
        this.buildingWidth = buildingWidth;
        this.buildingHeight = buildingHeight;
        this.setLayoutParams(new RelativeLayout.LayoutParams(this.buildingWidth,this.buildingHeight));
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

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

