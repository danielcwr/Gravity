package gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Particle 
{
    protected boolean active = true;
    protected boolean onTick = false;
    protected boolean onRender = false;
    
    protected Handler handler;
    protected Point coords;
    
    protected float x, y;
    protected float currentXVelocity;
    protected float currentYVelocity;  
    protected Color color;
    protected long spawnedAtTime;
    protected int activeTime;
    
    public Particle(Handler handler, int x, int y, double xVel, double yVel, Color color)
    {
        this.handler = handler;
        
        this.x = x;
        this.y = y;
        this.currentXVelocity = (float)xVel;
        this.currentYVelocity = (float)yVel;
        this.color = color;
        
        spawnedAtTime = System.currentTimeMillis();
        activeTime = handler.random(2000, 4000);
    }
    
    public Particle(Handler handler, int x, int y, double xVel, double yVel, Color color, int activeTime)
    {
        this.handler = handler;
        
        this.x = x;
        this.y = y;
        this.currentXVelocity = (float)xVel;
        this.currentYVelocity = (float)yVel;
        this.color = color;
        
        spawnedAtTime = System.currentTimeMillis();
        this.activeTime = activeTime;
    }
    
    public void tick()
    {
        x = x + currentXVelocity;
        y = y + currentYVelocity;
    }
    
    public void render(Graphics g)
    {
        g.setColor(color);
        g.fillRect((int)(x- handler.getGameCamera().getxOffset()), (int)(y- handler.getGameCamera().getyOffset()), 2, 2);
    }
    
    public boolean getOnTick()
    {
        return onTick;
    }
    
    public void setOnTick(boolean TF)
    {
        onTick = TF;
    }
    
    public boolean getOnRender()
    {
        return onRender;
    }
    
    public void setOnRender(boolean TF)
    {
        onRender = TF;
    }
    
}
