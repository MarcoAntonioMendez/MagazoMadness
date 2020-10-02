package com.zapatoseducadosgames.magazomadness.engine;
import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

public class City {
    // Constants
    public static final int CITY_WIDTH = 7;
    public static final int CITY_HEIGHT = 7;
    // Variables
    private boolean[][] cityStructure;
    private ArrayList<Building> city;
    private int screenWidth,screenHeight;
    private Context context;
    private Random random;

    public City(int screenWidth,int screenHeight,Context context){
        cityStructure = new boolean[CITY_WIDTH][CITY_HEIGHT];
        city = new ArrayList<Building>();
        random = new Random();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.context = context;

        //Initializing the city structure
        for(int x = 0; x < CITY_WIDTH; x++){
            for(int y = 0; y < CITY_HEIGHT; y++){
                cityStructure[x][y] = false;
            }
        }
    }

    /**
     * Creates City block
     * @param horizontalPosition - The horizontal position in which the cityBlock will be placed.
     * @param verticalPosition - The height in which the cityBlock will be placed.
     * @param type - Architecture style.
     * @return - The object og cityBlock to be added to layout
     */
    public Building createCityBlock(int horizontalPosition,int verticalPosition,String type){
        // Creating the cityBlock
        Building cityBlock = new Building(context,type,verticalPosition);
        cityBlock.setScreenWidthAndHeight(screenWidth,screenHeight);
        cityBlock.setCoordinates(horizontalPosition,verticalPosition,screenHeight);
        city.add(cityBlock);

        cityStructure[(horizontalPosition-1)][(CITY_HEIGHT-verticalPosition)] = true;

        return cityBlock;
    }

    /**
     * Chooses an horizontal position for the new city block
     * @return A valid horizontal position, integer from 0-CITY_WIDTH.
     */
    public int chooseHorizontalPosition(){
        return random.nextInt(CITY_WIDTH);
    }

    /**
     * Given a horizontal position, it will choose a vertical position according to the next
     * vertical position in turn, without causing any overriding or bad data.
     * @param horizontalPosition - Horizontal position in which the cityBlock will be placed.
     * @return An integer representing the vertical position of the cityBlock,
     */
    public int chooseVerticalPosition(int horizontalPosition){
        for(int x = 0; x < CITY_HEIGHT; x++){
            if(cityStructure[horizontalPosition][x]){
                return (x-1);
            }
            if(x == (CITY_HEIGHT-1)){
                return x;
            }
        }

        return -1;
    }

    /**
     * Gets the city thah contains all the buildings, also called the cityBlocks
     * @return ArrayList of Buildings
     */
    public ArrayList<Building> getCity(){
        return city;
    }
}
