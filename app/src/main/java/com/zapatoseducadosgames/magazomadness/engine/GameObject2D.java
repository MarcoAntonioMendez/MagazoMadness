package com.zapatoseducadosgames.magazomadness.engine;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zapatoseducadosgames.magazomadness.R;

public class GameObject2D extends AppCompatImageView {
    private float xPos,yPos,gOWidth,gOHeight;
    private int[] imagesResources;
    private int currentImageResource;

    public GameObject2D(Context context,float xPos,float yPos,float gOWidth,float gOHeight,
                        int[] imagesResources){
        super(context);

        currentImageResource = 0;

        setImagesResources(imagesResources);
        setXPos(xPos);
        setYPos(yPos);
        this.setWidthAndHeight(gOWidth,gOHeight);

        setImageResource(imagesResources[0]);
    }

    public GameObject2D(Context context){
        super(context);
    }

    /**
     * Every time this methods gets called, the next image in the specific sprite gets called.
     * Achieving an animation.
     */
    public void render(){
        if((currentImageResource+1) == imagesResources.length){
            currentImageResource = 0;
        }else{
            currentImageResource++;
        }

        setImageResource(imagesResources[currentImageResource]);
    }

    /**
     * This methods checks if an object provided as a parameter has touched this object
     * @param object2D The object to check collision for
     * @return True if the object provided as a parameter collisions with this object.
     */
    public boolean onCollision(GameObject2D object2D){
        if(xPos < (object2D.getXPos() + object2D.getObjectWidth()) &&
                (xPos + gOWidth) > object2D.getXPos() &&
                yPos < (object2D.getYPos() + object2D.getObjectHeight()) &&
                (yPos + gOHeight) > object2D.getYPos()){
            return true;
        }
        return false;
    }

    /**
     * Sets the x coordinate of the object.
     * @param xPos The x coordinate.
     */
    public void setXPos(float xPos){
        this.xPos = xPos;
        this.setX(this.xPos);
    }

    /**
     * Gets the x coordinate of the object.
     * @return X coordinate.
     */
    public float getXPos(){
        return xPos;
    }

    /**
     * Sets the y coordinate of the object.
     * @param yPos The y coordinate.
     */
    public void setYPos(float yPos){
        this.yPos = yPos;
        this.setY(this.yPos);
    }

    /**
     * Gets the y coordinate of the object.
     * @return Y Coordinate
     */
    public float getYPos(){
        return yPos;
    }

    /**
     * Sets the width and the height of the object.
     * @param gOWidth The width to set.
     * @param gOHeight The height to set.
     */
    public void setWidthAndHeight(float gOWidth,float gOHeight){
        this.gOWidth = gOWidth;
        this.gOHeight = gOHeight;

        requestLayout();
        setLayoutParams(new RelativeLayout.LayoutParams((int)gOWidth,(int)gOHeight));
        setScaleType(ImageView.ScaleType.FIT_XY);
    }

    /**
     * Gets the width of the object.
     * @return gOWidth, width of the object.
     */
    public float getObjectWidth(){
        return gOWidth;
    }

    /**
     * Gets the height of the object.
     * @return gOHeight, height of the object.
     */
    public float getObjectHeight(){
        return gOHeight;
    }

    /**
     * Sets the array of images that are used the object's animations.
     * @param imagesResources An array of integers representing the id's of the images.
     */
    public void setImagesResources(int[] imagesResources){
        this.imagesResources = imagesResources;
        setImageResource(imagesResources[0]);
    }

    /**
     * Gets the whole array of images id
     * @return imagesResources(Array of Integers) - The images Ids
     */
    public int[] getImagesResources(){
        return imagesResources;
    }

    /**
     * Gets the current frame id(a number) that is being displayed
     * @return currentImageResource - The frame that is being displayed
     */
    public int getCurrentFrameId(){
        return currentImageResource;
    }

    public static final GameObject2D getInvisibleBlock(int invisibleBlockWidth,int invisibleBlockHeight,
                                                       Context context){
        int[] invisible = {R.drawable.transparent_image};
        return new GameObject2D(context,0,0,invisibleBlockWidth,invisibleBlockHeight,invisible);
    }
}
