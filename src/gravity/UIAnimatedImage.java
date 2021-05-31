package gravity;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class UIAnimatedImage extends UIObject
{
    private BufferedImage[] frames;
    public BufferedImage image;
    private int nOFrames;
    
    private long lastTime, timer;
    private int index, frameChange;
    public float rotation = 0;
    private double rotationR8 = 0;
    
    private boolean spinning = false;
    
    public UIAnimatedImage(Handler handler, float x, float y, int width, int height, BufferedImage[] frames, int frameChange) {
        super(handler, x, y, width, height, false);
        this.frames = frames;
        this.frameChange = frameChange;
        this.nOFrames = frames.length;
    }
    
    public UIAnimatedImage(Handler handler, float x, float y, int width, int height, BufferedImage image) {
        super(handler, x, y, width, height, false);
        this.image = image;
        this.nOFrames = 0;
    }
    
    public UIAnimatedImage(Handler handler, float x, float y, int width, int height, BufferedImage image, boolean spinning, double rotationR8) {
        super(handler, x, y, width, height, false);
        this.image = image;
        this.spinning = spinning;
        this.rotationR8 = rotationR8;
        this.nOFrames = 0;
    }

    @Override
    public void tick() 
    {
        if (nOFrames > 1)
        {
            timer += System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();

            if(timer > frameChange)
            {
                index++;
                timer = 0;
                if (index >= nOFrames)
                {
                    index = 0;
                }
            }
        }
        rotation += rotationR8; //.05
    }
    
    @Override
    public void render(Graphics g) 
    {
        if (spinning)
        {
            /*
            AffineTransform at = new AffineTransform();
            
            at.setToScale(width/64, height/64);
            at.translate(x, y);
            at.rotate(Math.toRadians(rotation), 32, 32);
            
   
            
            Graphics2D g2d = (Graphics2D)g;
            g2d.drawImage(image, at, null);
                    */
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(Math.toRadians(rotation), (x + width/2), (y + height/2));
            g2d.drawImage(image, (int)x, (int)y, width, height, null);
            g2d.dispose();
        }
        else if (nOFrames > 1)
        {
            g.drawImage(frames[index],(int) x ,(int) y, width, height, null);
        }
        else
        {
            g.drawImage(image,(int) x ,(int) y, width, height, null);
        }

    }

    @Override
    public void onClick() 
    {
        
    }
    
}
