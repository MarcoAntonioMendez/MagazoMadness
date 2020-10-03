package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreenManager {
    public static final int TIME_DESIGNATED_FOR_EXPLOSIONS = 4000;
    // Phase 1 lasts for 10 seconds
    public static final int PHASE_1_LENGTH = 10000;
    public static final int PHASE_1_TIME_STAMP = 10000;
    public static final int PHASE_1_INTERVAL = 2000;
    //Phase 2 lasts for 10 seconds
    public static final int PHASE_2_LENGTH = 10000;
    public static final int PHASE_2_TIME_STAMP = 20000;
    public static final int PHASE_2_INTERVAL = 1000;
    //Phase 3 lasts for 13 seconds
    public static final int PHASE_3_LENGTH = 13000;
    public static final int PHASE_3_TIME_STAMP = 33000;
    public static final int PHASE_3_INTERVAL = 900;
    //Phase 4 lasts for 10 seconds
    public static final int PHASE_4_LENGTH = 10000;
    public static final int PHASE_4_TIME_STAMP = 43000;
    public static final int PHASE_4_INTERVAL = 800;
    //Phase 5 lasts for 10 seconds
    public static final int PHASE_5_LENGTH = 10000;
    public static final int PHASE_5_TIME_STAMP = 53000;
    public static final int PHASE_5_INTERVAL = 700;
    //Phase 6 lasts for 30 seconds
    public static final int PHASE_6_LENGTH = 30000;
    public static final int PHASE_6_TIME_STAMP = 63000;
    public static final int PHASE_6_INTERVAL = 600;
    //Phase 7 lasts for 30 seconds
    public static final int PHASE_7_LENGTH = 30000;
    public static final int PHASE_7_TIME_STAMP = 93000;
    public static final int PHASE_7_INTERVAL = 500;
    //Phase 8 lasts for 30 seconds
    public static final int PHASE_8_LENGTH = 30000;
    public static final int PHASE_8_TIME_STAMP = 123000;
    public static final int PHASE_8_INTERVAL = 400;
    // Variables
    private String architectureStyle;
    private String[] allArchitectureStyles = {AppConstants.DARK_WET_BUILDING};
    private Random random;
    private City city;
    private int secondsPassed,screenWidth,screenHeight,timePassedForMeteors,currentWave,explosionTime;
    private RelativeLayout layout;
    private Context context;
    private ArrayList<Integer> timeStampsForWave;
    private ArrayList<Meteor> meteors;
    private boolean anyMeteorHasHitCityBlock;
    private GameObject2D explosionObject;

    public GameScreenManager(int screenWidth,int screenHeight,Context context,RelativeLayout layout){
        architectureStyle = AppConstants.DARK_WET_BUILDING;
        this.layout = layout;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.context = context;
        anyMeteorHasHitCityBlock = false;
        explosionTime = 0;

        city = new City(screenWidth,screenHeight,context);
        secondsPassed = 0;
        timePassedForMeteors = 0;
        currentWave = 0;

        // Setting the meteors
        meteors = new ArrayList<Meteor>();
        setMeteorsWaves();

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
    public String update(){
        secondsPassed += AppConstants.DELTA_TIME;
        timePassedForMeteors += AppConstants.DELTA_TIME;

        if(!anyMeteorHasHitCityBlock) {
            dispatchMeteors();
            addCityBlock();

            // Updating the meteors, making go down down down down
            for(int x = 0; x < meteors.size(); x++){
                meteors.get(x).update(screenWidth);
            }

            checkIfAnyMeteorsHitAnyCityBlock();

            // Checking if user killed the meteor
            for(int x = 0; x < meteors.size(); x++){
                if(meteors.get(x).isDeath()){
                    meteors.get(x).setVisibility(View.INVISIBLE);
                    meteors.remove(x);
                    x--;
                    break;
                }
            }
        }else{
            explosionTime += AppConstants.DELTA_TIME;
            if(explosionTime >= TIME_DESIGNATED_FOR_EXPLOSIONS){
                return AppConstants.GAME_OVER_STATE;
            }
        }

        return AppConstants.GAME_STATE;
    }

    public void render(){}

    /**
     * Checks if any meteor hits the top of cityBlocks
     */
    private void checkIfAnyMeteorsHitAnyCityBlock(){
        for(int x = 0; x < city.getCity().size(); x++){
            for(int y = 0; y < meteors.size(); y++){
                if(meteors.get(y).onCollision(city.getCity().get(x))){
                    anyMeteorHasHitCityBlock = true;
                    meteors.get(y).setVisibility(View.INVISIBLE);

                    int[] explosionImages = {R.drawable.explosion};
                    explosionObject = new GameObject2D(context,meteors.get(y).getMeteorXPos(),
                            meteors.get(y).getMeteorYPos(),meteors.get(y).getMeteorWidth(),
                            meteors.get(y).getMeteorHeight(),explosionImages);
                    layout.addView(explosionObject);
                    meteors.remove(y);
                    y--;
                    break;
                }
            }
        }
    }

    /**
     * Checks if a new city block should be added.
     */
    private void addCityBlock(){
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

    /**
     * Checks if another meteor should be added.
     */
    private void dispatchMeteors(){
        if(currentWave < timeStampsForWave.size()){
            if(timePassedForMeteors >= timeStampsForWave.get(currentWave)){
                dispatchWave(currentWave);
                timePassedForMeteors = 0;
                currentWave++;
            }
        }else{
            if(timePassedForMeteors >= timeStampsForWave.get((currentWave-1))){
                dispatchWave((currentWave-1));
                timePassedForMeteors = 0;
            }
        }

    }

    /**
     * According to the current difficulty of the game (PHASE), it calls the method createMeteor()
     */
    private void dispatchWave(int x){
        if(secondsPassed < PHASE_1_TIME_STAMP){
            createMeteor(Meteor.VERTICAL_MOVEMENT,Meteor.SMALL_SIZE,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_2_TIME_STAMP){
            createMeteor(Meteor.VERTICAL_MOVEMENT,Meteor.SMALL_SIZE,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_3_TIME_STAMP){
            createMeteor(Meteor.VERTICAL_MOVEMENT,Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_4_TIME_STAMP){
            createMeteor(Meteor.VERTICAL_MOVEMENT,Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_5_TIME_STAMP){
            createMeteor(Meteor.chooseMovement(),Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_6_TIME_STAMP){
            createMeteor(Meteor.chooseMovement(),Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_7_TIME_STAMP){
            createMeteor(Meteor.chooseMovement(),Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else if(secondsPassed < PHASE_8_TIME_STAMP){
            createMeteor(Meteor.chooseMovement(),Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }else{
            createMeteor(Meteor.chooseMovement(),Meteor.chooseSize() ,Meteor.chooseVelocity(),
                    Meteor.choosePosition());
        }
    }

    /**
     * Creates a meteor and adds it to the layout
     * @param movement - Movement of the meteor (SIN, VERTICAL etc.):
     * @param size - Size of the meteor (3,2,1).
     * @param velocity - Velocity of the meteor, how fast it will go down.
     * @param position - Position of depending of screen width.
     */
    private void createMeteor(String movement,int size,int velocity,int position){
        final Meteor meteor = new Meteor(context,movement,size,velocity,position,
                screenWidth,screenHeight);
        meteor.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN ){
                    if(!anyMeteorHasHitCityBlock){
                        meteor.receiveAttack();
                    }
                }

                return true;
            }
        });
        meteors.add(meteor);
        layout.addView(meteor);
    }

    // Methods only called in the constructor
    private void setMeteorsWaves(){
        timeStampsForWave = new ArrayList<Integer>();

        //Phase 1
        for(int x = 0; x < PHASE_1_LENGTH/PHASE_1_INTERVAL; x++){
            timeStampsForWave.add(PHASE_1_INTERVAL);
        }
        //Phase 2
        for(int x = 0; x < PHASE_2_LENGTH/PHASE_2_INTERVAL; x++){
            timeStampsForWave.add(PHASE_2_INTERVAL);
        }
        //Phase 3
        for(int x = 0; x < PHASE_3_LENGTH/PHASE_3_INTERVAL; x++){
            timeStampsForWave.add(PHASE_3_INTERVAL);
        }
        //Phase 4
        for(int x = 0; x < PHASE_4_LENGTH/PHASE_4_INTERVAL; x++){
            timeStampsForWave.add(PHASE_4_INTERVAL);
        }
        //Phase 5
        for(int x = 0; x < PHASE_5_LENGTH/PHASE_5_INTERVAL; x++){
            timeStampsForWave.add(PHASE_5_INTERVAL);
        }
        //Phase 6
        for(int x = 0; x < PHASE_6_LENGTH/PHASE_6_INTERVAL; x++){
            timeStampsForWave.add(PHASE_6_INTERVAL);
        }
        //Phase 7
        for(int x = 0; x < PHASE_7_LENGTH/PHASE_7_INTERVAL; x++){
            timeStampsForWave.add(PHASE_7_INTERVAL);
        }
        //Phase 8
        for(int x = 0; x < PHASE_8_LENGTH/PHASE_8_INTERVAL; x++){
            timeStampsForWave.add(PHASE_8_INTERVAL);
        }
    }
}
