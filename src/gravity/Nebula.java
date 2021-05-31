package gravity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Nebula extends StaticEntity
{
    private BufferedImage image;
    private double currentAngle;
    
    public Nebula(Handler handler, BufferedImage image, float x, float y, int size)
    {
        super(handler, x, y, PLANET_DIAMETER*size, PLANET_DIAMETER*size, false);
        
        this.image = image;
        
        currentAngle = handler.random(0, 359);
        
        isNebula = true;
        hasHitBox = false;
    }

    @Override
    public void tick() 
    {
        //plasma spawning
    }
    
    @Override
    public void die()
    {
        //Can't die
    }


    @Override
    public void render(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(currentAngle), (x - handler.getGameCamera().getxOffset())+radius, (y - handler.getGameCamera().getyOffset())+radius);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .6f));//.4 looks good
        g2d.drawImage(image, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
        g2d.dispose();
        
        /*//*******COLLISION TEST BOX**********
        Graphics2D g3 = (Graphics2D) g;
        g3.setColor(Color.red); 
        Shape Circle = new Ellipse2D.Double((int) (x + bounds.x - handler.getGameCamera().getxOffset()), 
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset()),
                    bounds.width, bounds.height);
        g3.draw(Circle);
        */
        
    }


}

