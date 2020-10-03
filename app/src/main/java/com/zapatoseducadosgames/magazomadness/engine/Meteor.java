package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.R;

import java.util.Random;

public class Meteor extends AppCompatImageView {
    // Constants
    public static final String VERTICAL_MOVEMENT = "VERTICAL_MOVEMENT";
    public static final String SIN_MOVEMENT = "SIN_MOVEMENT";
    public static final String SPIRAL_MOVEMENT = "SPIRAL_MOVEMENT";
    public static final String STATIC_ZIG_ZAG_MOVEMENT = "STATIC_ZIG_ZAG_MOVEMENT";
    public static final String CHANGING_ZIG_ZAG_MOVEMENT = "CHANGING_ZIG_ZAG_MOVEMENT";
    public static final String[] MOVEMENTS_ID = {VERTICAL_MOVEMENT,SIN_MOVEMENT};
    public static final int SLOW_VELOCITY = 1;
    public static final float ORIGINAL_X_SLOW_VELOCITY = 3f;
    public static final float ORIGINAL_Y_SLOW_VELOCITY = 3f;
    public static final int MEDIUM_VELOCITY = 2;
    public static final float ORIGINAL_X_MEDIUM_VELOCITY = 4f;
    public static final float ORIGINAL_Y_MEDIUM_VELOCITY = 4f;
    public static final int FAST_VELOCITY = 3;
    public static final float ORIGINAL_X_FAST_VELOCITY = 5f;
    public static final float ORIGINAL_Y_FAST_VELOCITY = 5f;
    public static final int VERY_FAST_VELOCITY = 4;
    public static final float ORIGINAL_X_VERY_FAST_VELOCITY = 3.1f;
    public static final float ORIGINAL_Y_VERY_FAST_VELOCITY = 3.1f;
    public static final int MAX_SIZE = 3;
    public static final int MEDIUM_SIZE = 2;
    public static final int SMALL_SIZE = 1;
    public static final int SCREEN_BEGINNING = 1;
    public static final int SCREEN_BETWEEN_BEGINNING_AND_HALF = 2;
    public static final int SCREEN_HALF = 3;
    public static final int SCREEN_BETWEEN_HALF_AND_END = 4;
    public static final int SCREE_END = 5;
    public static final int ORIGINAL_WIDTH = 120;
    public static final int ORIGINAL_HEIGHT = 100;
    public static final int ORIGINAL_INCREMENT = 20;
    public static final float ORIGINAL_DEGREES_INCREMENT = 0.1f;
    public static final float ORIGINAL_AMPLITUDE = 10f;
    public static final float ORIGINAL_MAX_DEGREES = 360f;
    public static final int MAX_POSSIBLE_POSITIONS = 7;

    // Variables
    private int meteorWidth,meteorHeight,size,calculatedIncrement;
    private float meteorXPos,meteorYPos,xVelocity,yVelocity,appearancePosition,degrees;
    private float degreesIncrement,amplitude,maxDegrees;
    private String movement;
    private volatile boolean isActive,isDeath;


    public Meteor(Context context,String movement,int size,
                  int velocity, int position,int screenWidth,int screenHeight){
        super(context);
        isActive = false;
        isDeath = false;
        //this.setVisibility(View.GONE);
        degrees = 0.0f;
        degreesIncrement = (((float)screenHeight)*ORIGINAL_DEGREES_INCREMENT)/AppConstants.ORIGINAL_SCREEN_HEIGHT;
        amplitude = (((float)screenWidth)*ORIGINAL_AMPLITUDE)/AppConstants.ORIGINAL_SCREEN_WIDTH;
        maxDegrees = (((float)screenWidth)*ORIGINAL_MAX_DEGREES)/AppConstants.ORIGINAL_SCREEN_WIDTH;

        // Setting the size of the meteor (3 = Max size, 2 = Medium size, 1 = Small size).
        this.size = size;

        // Setting the sprites for the bad entity
        setSprites();

        // Calculate and actual width and Height of the meteor based on screen size
        calculateWidthAndHeight(screenWidth,screenHeight);

        // Setting the movement that the meteor will follow (sey vertical, sin, spiral etc.).
        this.movement = movement;

        // The meteor could BE SLOW, MEDIUM, FAST OR VERY FAST
        setVelocity(velocity,screenHeight,screenWidth);

        // Setting the position
        setPosition(position,screenWidth);
    }

    public void receiveAttack(){
        decreaseSize();
    }

    private void decreaseSize(){
        size--;
        setWidthAndHeight((meteorWidth-calculatedIncrement),(meteorHeight-calculatedIncrement));
        if(size == 0){
            isDeath = true;
        }

        if(size == MEDIUM_SIZE){
            setImageResource(R.drawable.bad_entity_2);
        }else if(size == SMALL_SIZE){
            setImageResource(R.drawable.bad_entity_1);
        }
    }

    public boolean isDeath(){
        return isDeath;
    }

    public boolean onCollision(Building building){
        if(meteorXPos < (building.getXPos() + building.getBuildingWidth()) &&
                (meteorXPos + meteorWidth) > building.getXPos() &&
                meteorYPos < (building.getYPos() + building.getBuildingHeight()) &&
                (meteorYPos + meteorHeight) > building.getYPos()){
            return true;
        }
        return false;
    }

    public void update(int screenWidth){
        moveMeteor(screenWidth);
    }

    private void moveMeteor(int screenWidth){
        float trigonometricIncrement;

        if(movement.equals(VERTICAL_MOVEMENT)){
            meteorYPos += yVelocity;
        }else if(movement.equals(SIN_MOVEMENT)){
            trigonometricIncrement = (float)(Math.cos(degrees)*amplitude);
            if( (meteorXPos+trigonometricIncrement) >= 0 && (meteorXPos+trigonometricIncrement) <= screenWidth-meteorWidth){
                meteorXPos += trigonometricIncrement;
            }

            degrees += degreesIncrement;
            if(degrees >= maxDegrees){
                degrees = 0;
            }
            meteorYPos += yVelocity;
        }

        setMeteorXPos(meteorXPos);
        setMeteorYPos(meteorYPos);
    }

    private void setVelocity(int velocity,int screenHeight,int screenWidth){
        float calculatedXVelocity = 0f,calculatedYVelocity = 0f;
        float screenHeightFloat = (float)screenHeight;
        float screenWidthFloat = (float)screenWidth;
        float originalScreenHeight = (float)AppConstants.ORIGINAL_SCREEN_HEIGHT;
        float originalScreenWidth = (float)AppConstants.ORIGINAL_SCREEN_WIDTH;

        // Adjusting the xVelocity and the yVelocity according to the device screen
        if(velocity == SLOW_VELOCITY){
            calculatedXVelocity = (screenWidthFloat*ORIGINAL_X_SLOW_VELOCITY)/originalScreenWidth;
            calculatedYVelocity = (screenHeightFloat*ORIGINAL_Y_SLOW_VELOCITY)/originalScreenHeight;
        }else if(velocity == MEDIUM_VELOCITY){
            calculatedXVelocity = (screenWidthFloat*ORIGINAL_X_MEDIUM_VELOCITY)/originalScreenWidth;
            calculatedYVelocity = (screenHeightFloat*ORIGINAL_Y_MEDIUM_VELOCITY)/originalScreenHeight;
        }else if(velocity == FAST_VELOCITY){
            calculatedXVelocity = (screenWidthFloat*ORIGINAL_X_FAST_VELOCITY)/originalScreenWidth;
            calculatedYVelocity = (screenHeightFloat*ORIGINAL_Y_FAST_VELOCITY)/originalScreenHeight;
        }else if(velocity == VERY_FAST_VELOCITY){
            calculatedXVelocity = (screenWidthFloat*ORIGINAL_X_VERY_FAST_VELOCITY)/originalScreenWidth;
            calculatedYVelocity = (screenHeightFloat*ORIGINAL_Y_VERY_FAST_VELOCITY)/originalScreenHeight;
        }

        // Setting all of velocities according to the movement
        if(movement.equals(Meteor.VERTICAL_MOVEMENT)){
            xVelocity = 0f;
            yVelocity = calculatedYVelocity;
        }else if(movement.equals(Meteor.SIN_MOVEMENT)){
            xVelocity = calculatedXVelocity;
            yVelocity = calculatedYVelocity;
        }else if(movement.equals(Meteor.SPIRAL_MOVEMENT)){
            xVelocity = calculatedXVelocity;
            yVelocity = calculatedYVelocity;
        }else if(movement.equals(Meteor.STATIC_ZIG_ZAG_MOVEMENT)){
            xVelocity = calculatedXVelocity;
            yVelocity = calculatedYVelocity;
        }else if(movement.equals(Meteor.CHANGING_ZIG_ZAG_MOVEMENT)){
            xVelocity = calculatedXVelocity;
            yVelocity = calculatedYVelocity;
        }
    }

    public void setMeteorXPos(float meteorXPos){
        this.meteorXPos = meteorXPos;
        this.setX(this.meteorXPos);
    }

    public float getMeteorXPos(){
        return meteorXPos;
    }

    public void setMeteorYPos(float meteorYPos){
        this.meteorYPos = meteorYPos;
        this.setY(this.meteorYPos);
    }

    public float getMeteorYPos(){
        return meteorYPos;
    }

    public int getMeteorWidth(){
        return meteorWidth;
    }

    public int getMeteorHeight(){
        return meteorHeight;
    }

    private void setSprites(){
        switch(size){
            case MAX_SIZE:
                setImageResource(R.drawable.bad_entity_3);
            break;
            case MEDIUM_SIZE:
                setImageResource(R.drawable.bad_entity_2);
            break;
            case SMALL_SIZE:
                setImageResource(R.drawable.bad_entity_1);
            break;
            default:
                System.out.println("********* An error occurred setting the" +
                        " sprites for bad entities, in setSprites() method *********");
        }
    }

    private void setPosition(int position,int screenWidth){
        float cityIndividualWidth = screenWidth/Building.CITY_MAX_NUMBER_OF_BUILDINGS;

        setMeteorXPos((cityIndividualWidth*(position-1)));
        setMeteorYPos(0f);
    }

    private void calculateWidthAndHeight(int screenWidth,int screenHeight){
        int calculatedWidth = (screenWidth*ORIGINAL_WIDTH)/AppConstants.ORIGINAL_SCREEN_WIDTH;
        int calculatedHeight = calculatedWidth;
        calculatedIncrement = (calculatedWidth*ORIGINAL_INCREMENT)/ORIGINAL_WIDTH;

        meteorWidth = calculatedWidth;
        meteorHeight = calculatedHeight;

        if(size == MAX_SIZE){
            meteorWidth += (calculatedIncrement*2);
            meteorHeight += (calculatedIncrement*2);
        }else if(size == MEDIUM_SIZE){
            meteorWidth += calculatedIncrement;
            meteorHeight += calculatedIncrement;
        }

        this.requestLayout();
        this.setLayoutParams(new RelativeLayout.LayoutParams(meteorWidth,meteorHeight));
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private void setWidthAndHeight(int meteorWidth,int meteorHeight){
        this.meteorWidth = meteorWidth;
        this.meteorHeight = meteorHeight;

        this.requestLayout();
        this.setLayoutParams(new RelativeLayout.LayoutParams(this.meteorWidth,this.meteorHeight));
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public static final int choosePosition(){
        Random random = new Random();
        int position = random.nextInt(MAX_POSSIBLE_POSITIONS)+1;
        random = null;
        return position;
    }

    public static final int chooseVelocity(){
        Random random = new Random();
        int velocity;
        velocity = (random.nextInt(FAST_VELOCITY)+1);
        random = null;
        return velocity;
    }

    public static final int chooseSize(){
        Random random = new Random();
        int chosenSize;
        chosenSize = (random.nextInt(MAX_SIZE)+1);
        random = null;
        return chosenSize;
    }

    public static final String chooseMovement(){
        Random random = new Random();
        int chosenMov;
        chosenMov = random.nextInt(MOVEMENTS_ID.length);
        random = null;
        return MOVEMENTS_ID[chosenMov];
    }
}
