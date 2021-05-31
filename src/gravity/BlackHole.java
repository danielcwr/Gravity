package gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class BlackHole extends StaticEntity
{
    private final double rotR8;
    private BufferedImage image = Assets.blackHole; //128 x 128 img
    private double currentAngle;
    
    
    public BlackHole(Handler handler, float x, float y, int size, boolean gravity, double rotR8)
    {
        super(handler, x, y, BLACK_HOLE_DIAMETER*size, BLACK_HOLE_DIAMETER*size, gravity);
        super.density = 5;
        super.mass = (float) (density * cm3Tokm3 * (4/3*Math.PI*(radius*radius*radius)));
        
        bounds[0].x = 44*size; 
        bounds[0].y = 44*size;
        bounds[0].width = 40*size;
        bounds[0].height = 40*size;
        
        this.rotR8 = rotR8;
        
        isBlackHole = true;
    }

    @Override
    public void tick() 
    {
        if (currentAngle >= 360)
        {
            currentAngle = 0;
        }
        currentAngle += rotR8;
    }
    
    @Override
    public void die()
    {
        
    }

    @Override
    public void render(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(currentAngle), (x - handler.getGameCamera().getxOffset())+radius, (y - handler.getGameCamera().getyOffset())+radius);
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
        
        /*/*******GRAVITY TEST BOX**********
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.blue); 
        Shape Meme = new Ellipse2D.Double((int) (x + bounds.x - handler.getGameCamera().getxOffset())-(radius*3), 
                    (int) (y + bounds.y - handler.getGameCamera().getyOffset())-(radius*3),
                    bounds.width*4, bounds.height*4); //if radius = *1, bounds = *2 //if radius = *2, bounds = *3
        g2.draw(Meme);
        //*/
        
    }


}

