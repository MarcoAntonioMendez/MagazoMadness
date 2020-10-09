package com.zapatoseducadosgames.magazomadness.engine;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.zapatoseducadosgames.magazomadness.R;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;

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
    private int score;
    private String highestScore;
    private RelativeLayout layout;
    private Context context;
    private ArrayList<Integer> timeStampsForWave;
    private ArrayList<Meteor> meteors;
    private boolean anyMeteorHasHitCityBlock,userAlreadyUsedContinue,watchAdQuestionSet;
    private boolean watchAdQuestionsElementsRemoved,isGamePaused,pauseGameButtonIsSet;
    private GameObject2D explosionObject,leftQuestionMark,rightQuestionMark,watchAdIcon,dontWatchAdIcon;
    private GameObject2D pauseButton,playAgainButton;
    private RewardedAd rewardedAd;
    private Activity activity;

    public GameScreenManager(int screenWidth,int screenHeight,Context context,RelativeLayout layout,
                             Activity activity){
        architectureStyle = AppConstants.DARK_WET_BUILDING;
        this.layout = layout;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.context = context;
        anyMeteorHasHitCityBlock = false;
        explosionTime = 0;
        score = 0;
        userAlreadyUsedContinue = false;
        watchAdQuestionSet = false;
        this.activity = activity;
        watchAdQuestionsElementsRemoved = false;
        isGamePaused = false;
        pauseGameButtonIsSet = false;

        city = new City(screenWidth,screenHeight,context);
        secondsPassed = 0;
        timePassedForMeteors = 0;
        currentWave = 0;

        //TEST AD ID
        rewardedAd = new RewardedAd(context,"ca-app-pub-3940256099942544/5224354917");
        rewardedAd.loadAd(new AdRequest.Builder().build(),new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded(){
                super.onRewardedAdLoaded();
            }
        });

        // Setting the meteors
        meteors = new ArrayList<Meteor>();
        setMeteorsWaves();

        // Setting the pause button

        random = new Random();
    }

    /**
     * Displays a rewarded ad so the user can keep playing, it also sets the values of the variables
     * so the game continues to work perfectly after the ad finishes.
     */
    public void displayAd(){
        rewardedAd.show(activity,new RewardedAdCallback(){
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem){
                explosionTime = 0;
                anyMeteorHasHitCityBlock = false;
                userAlreadyUsedContinue = true;

            }
        });
    }

    /**
     * Chooses one style randomly for the buildings style.
     */
    public void setArchitectureStyle(){
        architectureStyle = allArchitectureStyles[random.nextInt(allArchitectureStyles.length)];
    }

    /**
     * Removes the elements related to asking user if he/she wants to watch an ad, when the
     * ad finishes, 10 meteors will be removed so the user can keep playing.
     * If the number of meteors is less than 10, then all the meteors will be removed.
     */
    private void removeQuestionsElementsAndDelete10Meteors(){
        int numberOfMeteorsToDelete = 10;
        int meteorsDeleted = 0;
        if(userAlreadyUsedContinue && !watchAdQuestionsElementsRemoved){
            layout.removeView(leftQuestionMark);
            layout.removeView(rightQuestionMark);
            layout.removeView(watchAdIcon);
            layout.removeView(dontWatchAdIcon);
            layout.removeView(explosionObject);
            explosionObject = null;
            if(meteors.size() < numberOfMeteorsToDelete){
                numberOfMeteorsToDelete = meteors.size();
            }
            while(meteorsDeleted < numberOfMeteorsToDelete){
                layout.removeView(meteors.get(0));
                meteors.remove(0);
                meteorsDeleted++;
            }
            watchAdQuestionsElementsRemoved = true;
        }
    }

    /**
     * Checks if a meteor was killed by the user so it can be removed.
     */
    private void checkIfUserKilledAMeteor(){
        // Checking if user killed the meteor
        for(int x = 0; x < meteors.size(); x++){
            if(meteors.get(x).isDeath()){
                meteors.get(x).setVisibility(View.INVISIBLE);
                meteors.remove(x);
                x--;
                break;
            }
        }
    }

    /**
     * Updates all objects that are active during the game state
     */
    public String update(){

        if(!isGamePaused){
            secondsPassed += AppConstants.DELTA_TIME;
            timePassedForMeteors += AppConstants.DELTA_TIME;
            if(!anyMeteorHasHitCityBlock) {
                removeQuestionsElementsAndDelete10Meteors();
                dispatchMeteors();
                addCityBlock();
                setPauseGameButton();
                // Updating the meteors, making go down down down down
                for(int x = 0; x < meteors.size(); x++){
                    meteors.get(x).update(screenWidth);
                }
                checkIfAnyMeteorsHitAnyCityBlock();
                checkIfUserKilledAMeteor();

            }else{
                explosionTime += AppConstants.DELTA_TIME;
                if(explosionTime >= TIME_DESIGNATED_FOR_EXPLOSIONS){
                    if(userAlreadyUsedContinue){
                        return updateScoreAndResetAssets();
                    }else{
                        if(!watchAdQuestionSet){
                            setWatchAdQuestion();
                            watchAdQuestionSet = true;
                        }
                    }
                }
            }
        }

        return AppConstants.GAME_STATE;
    }

    public void render(){}

    /**
     * Initializes the elements to pose a question to the user:
     * ¿Watch and AD or not?
     */
    private void setWatchAdQuestion(){
        int elementsWidth = screenWidth/4;
        int elementsHeight = elementsWidth;
        float elementsYPos = (screenHeight/2)-elementsHeight;

        int[] leftQuestionMarkImages = {R.drawable.left_question_mark};
        leftQuestionMark = new GameObject2D(context,0,elementsYPos,elementsWidth,
                elementsHeight,leftQuestionMarkImages);

        int[] dontWatchAdIconImages = {R.drawable.dont_watch_ad_icon};
        dontWatchAdIcon = new GameObject2D(context,elementsWidth,elementsYPos,elementsWidth,
                elementsHeight,dontWatchAdIconImages);
        dontWatchAdIcon.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN ){
                    userAlreadyUsedContinue = true;
                }

                return true;
            }
        });

        int[] watchAdIconImages = {R.drawable.watch_ad_icon};
        watchAdIcon = new GameObject2D(context,elementsWidth*2,elementsYPos,elementsWidth,
                elementsHeight, watchAdIconImages);
        watchAdIcon.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN ){
                    displayAd();
                }

                return true;
            }
        });

        int[] rightQuestionMarkImages = {R.drawable.right_question_mark};
        rightQuestionMark = new GameObject2D(context,elementsWidth*3,elementsYPos,elementsWidth,
                elementsHeight, rightQuestionMarkImages);

        layout.addView(leftQuestionMark);
        layout.addView(dontWatchAdIcon);
        layout.addView(watchAdIcon);
        layout.addView(rightQuestionMark);
    }

    /**
     * Updates the user's highest score and resets all the game assets freeing memory.
     * @return - A String containing the next state GAME_OVER_STATE
     * */
    private String updateScoreAndResetAssets(){
        if(score > Integer.valueOf(highestScore)){
            FilesHandler.writeFile(AppConstants.HIGHEST_SCORE_FILE_NAME,
                    String.valueOf(score),context);
        }
        reset();
        return AppConstants.GAME_OVER_STATE;
    }

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
                        score += 1;
                        meteor.receiveAttack();
                    }
                }

                return true;
            }
        });
        meteors.add(meteor);
        layout.addView(meteor);
    }

    /**
     * Deletes all the objects active in the game state so memory can be freed.
     */
    private void reset(){
        anyMeteorHasHitCityBlock = false;
        explosionTime = 0;
        secondsPassed = 0;
        timePassedForMeteors = 0;
        currentWave = 0;
        score = 0;
        userAlreadyUsedContinue = false;
        watchAdQuestionSet = false;
        watchAdQuestionsElementsRemoved = false;
        isGamePaused = false;
        pauseGameButtonIsSet = false;

        //TEST AD ID
        rewardedAd = new RewardedAd(context,"ca-app-pub-3940256099942544/5224354917");
        rewardedAd.loadAd(new AdRequest.Builder().build(),new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded(){
                super.onRewardedAdLoaded();
            }
        });

        // Removing all meteors
        for(int x = 0; x < meteors.size(); x++){
            layout.removeView(meteors.get(x));
        }
        meteors.removeAll(meteors);

        // Removing all the cityBlocks
        for(int x = 0; x < city.getCity().size(); x++){
            layout.removeView(city.getCity().get(x));
        }

        // Removing the explosionObject and the pauseButton and playAgainButton
        layout.removeView(explosionObject);
        explosionObject = null;
        layout.removeView(pauseButton);
        pauseButton = null;
        layout.removeView(playAgainButton);
        playAgainButton = null;

        city.reset();

        if(leftQuestionMark != null){
            layout.removeView(leftQuestionMark);
            layout.removeView(rightQuestionMark);
            layout.removeView(watchAdIcon);
            layout.removeView(dontWatchAdIcon);
            leftQuestionMark = null;
            rightQuestionMark = null;
            watchAdIcon = null;
            dontWatchAdIcon = null;
        }
    }

    /**
     * Creates and adds the pauseButton
     */
    private void setPauseGameButton(){
        if(!pauseGameButtonIsSet){
            pauseGameButtonIsSet = true;
            // Setting the pauseButton
            int pauseButtonWidth = screenWidth/5;
            int pauseButtonHeight = pauseButtonWidth;
            int pauseButtonXPos = screenWidth-pauseButtonWidth-AppConstants.PAUSE_BUTTON_OFFSET;
            int[] pauseButtonImages = {R.drawable.pause_button};
            pauseButton = new GameObject2D(context,pauseButtonXPos,5,
                    pauseButtonWidth,pauseButtonHeight,pauseButtonImages);

            pauseButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(!anyMeteorHasHitCityBlock){
                            if(!isGamePaused){
                                isGamePaused = true;
                                layout.removeView(pauseButton);
                                layout.addView(playAgainButton);
                            }
                        }
                    }
                    return true;
                }
            });

            // Setting the plaiAgainButton
            int[] playButtonImages = {R.drawable.play_button};
            playAgainButton = new GameObject2D(context,pauseButtonXPos,5,pauseButtonWidth,
                    pauseButtonHeight,playButtonImages);
            playAgainButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(!anyMeteorHasHitCityBlock){
                            if(isGamePaused){
                                isGamePaused = false;
                                layout.removeView(playAgainButton);
                                layout.addView(pauseButton);
                            }
                        }
                    }
                    return true;
                }
            });

            layout.addView(pauseButton);
        }
    }

    public void setHighestScore(String highestScore){
        this.highestScore = highestScore;
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
