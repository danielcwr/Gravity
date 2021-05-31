package gravity;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Manager 
{
    protected Handler handler;
    protected Player player;
    
    protected double pRadius;
    protected int pX; //player x
    protected int pY; //player y
    
    protected int screenLeftX; //render distance extremes--vv--
    protected int screenTopY;
    protected int screenRightX;
    protected int screenBottomY; //^^
    
    protected int tickLeftX; //tick distance extremes--vv--
    protected int tickTopY;
    protected int tickRightX;
    protected int tickBottomY; //^^
    
    protected int renderMarginWidth;
    protected int renderMarginHeight;
    protected int tickMargin;
    
    protected boolean onTick = false;
    protected boolean onRender = false;
    
    public Manager(Handler handler, Player player)
    {
        this.handler = handler;
        this.player = player;
        pRadius = player.radius;
        
        this.tickMargin = (handler.getScreenWidth()*2);
        this.renderMarginWidth = (handler.getScreenWidth()/2)+50;
        this.renderMarginHeight = (handler.getScreenHeight()/2)+50;
    }
    
    protected boolean[] updateScreenBoundaries(float x, float y, int radius)
    {
        pX = (int) handler.getPlayer().getCenterX();
        pY = (int) handler.getPlayer().getCenterY();
        
        screenLeftX = pX - renderMarginWidth;
        screenTopY = pY - renderMarginHeight;
        screenRightX = pX + renderMarginWidth;
        screenBottomY = pY + renderMarginHeight;
        
        tickLeftX = pX - tickMargin;
        tickTopY = pY - tickMargin;
        tickRightX = pX + tickMargin;
        tickBottomY = pY + tickMargin;  
        
        onRender = false;
        onTick = false;
        if (x+radius > screenLeftX && y+radius > screenTopY && x-radius < screenRightX && y-radius < screenBottomY) 
        {
            onRender = true;
            onTick = true;
        }
        else if (x+radius > tickLeftX && y+radius > tickTopY && x-radius < tickRightX && y-radius < tickBottomY)
        {
            onTick = true;
            onRender = false;
        }
        else
        {
            onTick = false;
            onRender = false;
        }
        
        return (new boolean[]{onTick, onRender});
    }
    
    //Random Asteroid Gen
    protected int getRandomX(int sizeOffset)
    {
        int onOrNextTo = handler.random(1, 4);
        if (onOrNextTo == 1)
        {
            return handler.random(screenLeftX-sizeOffset, screenRightX+sizeOffset);
        }
        else
        {
            int leftOrRight = handler.random(0, 1);
            if (leftOrRight == 0)//left
            {
                //left = left + 1;
                return handler.random(tickLeftX-sizeOffset, screenLeftX-sizeOffset);
            }
            else //right
            {
                //right = right + 1;
                return handler.random(screenRightX+sizeOffset, tickRightX+sizeOffset);
            }
        }
    }
    
    protected int getRandomY(int randomX, int sizeOffset)
    {
        if (randomX > screenLeftX && randomX < screenRightX)//on screen, therefore Top or Bot
        {
            int topOrBottom = handler.random(0, 1);
            if (topOrBottom == 0)//top
            {
                //top = top+1;
                return handler.random(tickTopY-sizeOffset, screenTopY-sizeOffset);
            }
            else //bottom
            {
                //bot = bot+1;
                return handler.random(screenBottomY+sizeOffset, tickBottomY+sizeOffset);
            }
        }
        else
        {
            return handler.random(screenTopY-sizeOffset, screenBottomY+sizeOffset);
        }
    }
    
    protected double getRandomDirectionInRadians(int randomX, int randomY)
    {
        float xDistance = (player.getCenterX() - randomX);
        float yDistance = (player.getCenterY() - randomY);
        
        double randomDirection = 0;//.0175*handler.random(-45, 45); //<random degree, to radians
        
        double placeholder = Math.tan(yDistance/xDistance)+randomDirection; 
        
        if (randomX > pX)
            placeholder = placeholder + Math.PI;
        
        return placeholder;
    }
    
    //Getters and Setters
    
    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public int getTopLeftX() {
        return screenLeftX;
    }

    public int getTopLeftY() {
        return screenTopY;
    }

    public int getBotRightX() {
        return screenRightX;
    }

    public int getBotRightY() {
        return screenBottomY;
    }
    
}
