package gravity;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class UIObject 
{
    protected Handler handler;
    protected float x, y;
    protected int width, height;
    protected Rectangle bounds;
    protected boolean hovering = false;
    protected boolean hoverable = false;
    
    protected int screenHeight;
    protected int screenWidth;
    
    public UIObject(Handler handler, float x, float y, int width, int height, boolean hoverable)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hoverable = hoverable;
        bounds = new Rectangle((int) x, (int) y, width, height);
        
        this.screenWidth = handler.getScreenWidth();
        this.screenHeight = handler.getScreenHeight();
    }
    
    public abstract void tick();
    
    public abstract void render(Graphics g);
    
    public abstract void onClick();
    
    public void onMouseMove(MouseEvent e)
    {
        hovering = false;
        if(bounds.contains(e.getX(), e.getY()))
        {
            hovering = true;
        }
    }
    
    public void onMouseRelease(MouseEvent e)
    {
        if(hovering)
        {
            onClick();
        }
    }
    
    //Getters
    public float getX()
    {
        return x;
    }
    
    public void setX(float x)
    {
        this.x = x;
    }
    
    public float getY()
    {
        return y;
    }
    
    public void setY(float y)
    {
        this.y = y;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public boolean getHovering()
    {
        return hovering;
    }
    
    public void setHovering(boolean hovering)
    {
        this.hovering = hovering;
    }
}
