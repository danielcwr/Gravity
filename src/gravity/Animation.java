package gravity;

import java.awt.image.BufferedImage;

public class Animation //Uses a BufferedImage[] plus input tick time to create frame by frame animation
{
    private int speed, index;
    private long lastTime, timer;
    private BufferedImage[] frames;
    private int loopFor = -1;
    private boolean maintainImage = false;
    
    public Animation(int speed, BufferedImage[] frames)
    {
        this.speed = speed;
        this.frames = frames;
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }
    
    public Animation(int speed, BufferedImage[] frames, int loopFor, boolean maintainImage)
    {
        this.speed = speed;
        this.frames = frames;
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
        this.loopFor = loopFor;
        this.maintainImage = maintainImage;
    }
    
    public void tick()
    {
        timer += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        
        if(timer > speed && (loopFor == -1 || loopFor > 0))
        {
            index++;
            timer = 0;
            if (index >= frames.length)
            {
                index = 0;
                if (loopFor > 0)
                {
                    loopFor--;
                }
            }
        }
    }
    
    public BufferedImage getCurrentFrame()
    {
        if (loopFor == 0)
        {
            if (maintainImage)
                return frames[frames.length-1];
            return null;
        }
            
        return frames[index];
    }
    
    public int getLoopsLeft()
    {
        return loopFor;
    }
}
