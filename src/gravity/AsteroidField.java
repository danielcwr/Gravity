package gravity;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AsteroidField extends StaticEntity
{
    private int minDistance = 32;
    
    private ArrayList<UIAnimatedImage> asteroids = new ArrayList<UIAnimatedImage>();
    
    public AsteroidField(Handler handler, float x, float y, int width, int height)
    {
        super(handler, x, y, width, height, false);
        isAsteroidField = true;
        hasHitBox = false;
        
        generateAsteroids();
    }
    
    @Override
    public void die()
    {
        
    }
    
    @Override
    public void tick()
    {
        for (UIAnimatedImage asteroid : asteroids) 
        {
            asteroid.tick();
        }
    }
    
    @Override
    public void render(Graphics g)
    {
        UIAnimatedImage asteroid;
        float opac;
        Graphics2D g2d = (Graphics2D)g.create();
        double dist = Math.sqrt(distanceToPlayerSqr) - radius;
        if (dist < 0)
            dist = 0;
        
        opac = 1;
        if (dist > 0) //250^2
        {
            opac = (float)(1 - (dist / radius/2));
            if (opac > 0 && opac < 1)
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opac));
        }
        
        for (int c = 0; c < (int)(asteroids.size() * opac); c++)**make asteroids a) darker, b)max opacity of .1. Also: make for loop have new asteroids fade in.
        {
            System.out.println(opac);
            asteroid = asteroids.get(c);
            
            g2d.rotate(Math.toRadians(asteroid.rotation), (asteroid.x + asteroid.width/2), (asteroid.y + asteroid.height/2));
            g2d.drawImage(asteroid.image, (int)asteroid.x, (int)asteroid.y, asteroid.width, asteroid.height, null);
            g2d.rotate(Math.toRadians(-asteroid.rotation), (asteroid.x + asteroid.width/2), (asteroid.y + asteroid.height/2));
        }
        g2d.dispose();
    }
    private void generateAsteroids()
    {
        UIAnimatedImage asteroid;
        int randX = 0, randY = 0, size;
        double randomR8;
        BufferedImage image;
        for (int c = 0; c < 100; c++)//50 and increase minDist to 64
        {
            if (handler.random(0, 3) == 3)
            {
                image = Assets.randomAsteroid[handler.random(3, 6)];
            }
            else
            {
                image = Assets.randomAsteroid[handler.random(0, 2)];
            }
            
            size = handler.random(32, 64);
            randomR8 = Math.random()/10;
            
            if (asteroids.isEmpty())
            {
                asteroids.add(new UIAnimatedImage(handler, randX,randY, size, size, image, true, randomR8));
            }
            else 
            {
                randX = handler.random(0, handler.getScreenWidth());
                randY = handler.random(0, handler.getScreenHeight());
                for (int c2 = 0; c2 <asteroids.size(); c2++)
                {
                    asteroid = asteroids.get(c2);
                    if (asteroid.bounds.contains(new Rectangle((int)asteroid.x - minDistance, (int)asteroid.y - minDistance, asteroid.width + minDistance, asteroid.height + minDistance)))
                    {
                        randX = handler.random(0, handler.getScreenWidth());
                        randY = handler.random(0, handler.getScreenHeight());
                        
                        c2 = 0;
                    }
                }
                asteroids.add(new UIAnimatedImage(handler, randX ,randY, size, size, image, true, randomR8));
                
            }
        }
    }
}
