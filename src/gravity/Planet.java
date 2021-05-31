package gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Planet extends StaticEntity
{
    private final double rotR8;
    private BufferedImage image;
    private double currentAngle;
    
    public Planet(BufferedImage image,Handler handler, float x, float y, int size, boolean gravity, double rotR8, double density ,boolean earth)
    {
        super(handler, x, y, size*2, size*2, gravity);
        super.density = (float) density;
        super.mass = (float) (density*cm3Tokm3 * (4/3*Math.PI*(radius*radius*radius)));
        
        this.image = image;
        this.rotR8 = rotR8;
        
        bounds[0].x = 0;
        bounds[0].y = 0;
        bounds[0].width = width;
        bounds[0].height = height;
        
        isPlanet = true;
        
        isEarth = earth;
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
    
    public double getRotR8()
    {
        return rotR8;
    }


}

