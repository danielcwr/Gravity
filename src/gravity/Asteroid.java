package gravity;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
//import java.awt.Shape;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Asteroid extends DynamicEntity //Any asteroid uses this class
 //Any asteroid uses this class
{
    private double currentAngle = 0;
    protected final double randomSpeed = Math.random();
    private float randomDirection = handler.random(1, 360);
    private double rotR8 = handler.randomDouble()/10;
    private int asteroidType;
    private int offSet;
    
    private BufferedImage image;
    
    public Asteroid(Handler handler, float x, float y, int size, double directionInRadians) //User Defined Asteroid, used in random generation -_-
    {
        super(handler, x, y, DEFAULT_CREATURE_WIDTH*size/2, DEFAULT_CREATURE_HEIGHT*size/2, false);
        double rMulti = Math.sqrt(radius);
        super.mass = (float) (53/*<---- leave it!*/*rMulti);
        
        health = size*10;
        currentSize = size;
        
        offSet = handler.getItemSize()/2;
        
        asteroidType();
        resize();
        
        this.currentXVelocity = (float) (Math.cos(directionInRadians) * randomSpeed);
        this.currentYVelocity = (float) (Math.sin(directionInRadians) * randomSpeed);
        
        isAsteroid = true;
    }
    
    @Override
    public void tick() 
    {
        inputXAccel = 0;
        inputYAccel = 0;
        move();
        
        if (currentAngle >= 360)
        {
            currentAngle = 0;
        }
        currentAngle += rotR8;
    }

    @Override
    public void render(Graphics g) 
    {
        /*
        Graphics2D g2d = (Graphics2D) g.create();
        AffineTransform arrow = AffineTransform.getTranslateInstance((int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()));
        arrow.rotate(Math.toRadians(currentAngle), 64/2, 64/2);
        arrow.scale(scaleFactor, scaleFactor);
        g2d.drawImage(image, arrow, null);
        g2d.dispose();
        */
        
        ///*
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(currentAngle), (x - handler.getGameCamera().getxOffset())+radius, (y - handler.getGameCamera().getyOffset())+radius);
        g2d.drawImage(image, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
        g2d.dispose();
        //*/
        
        
        /*******COLLISION TEST BOX**********
        Graphics2D g3 = (Graphics2D) g.create();
        g3.setColor(Color.red); 
        Shape Circle = new Ellipse2D.Double((int) (x + bounds[0].x - handler.getGameCamera().getxOffset()), 
                    (int) (y + bounds[0].y - handler.getGameCamera().getyOffset()),
                    bounds[0].width, bounds[0].height);
        g3.draw(Circle);
        g3.dispose();
        //*/
    }
    
    @Override
    public void damaged(double amt, Entity e, float x, float y)
    {
        health -=Math.abs(amt);
        if (health <= 0)
        {
            diedTo = e;
            
            die();
        }
        else
        {
            resize();
            createParticles(amt, x, y, e);
        }
        if (amt > 3)
        {
            dropMaterials();
        }
        else if (amt == .1)
        {
            if (handler.random(0, 20) == 20)
            {
                dropMaterials();
            }
        }
    }

    @Override
    public void die()
    {
        if (!diedTo.isBlackHole)
        {
            createParticles(width * height, 0, 0, diedTo);


            int randLootNum = handler.random(1, 3);
            int randX = handler.random(offSet, width - offSet)-12;
            int randY = handler.random(offSet, height - offSet)-12;

            if (asteroidType == 1) //Carbon ASTEROID
            {  
                if (randLootNum == 1 || randLootNum == 3)
                {
                    handler.getWorld().getItemManager().addItem(Item.carbonItem.createNew((int) x + randX, (int) y + randY));
                }
            }

            else if (asteroidType == 2) //Aluminum ASTEROID
            {  
                if (randLootNum == 1 || randLootNum == 3)
                {
                    handler.getWorld().getItemManager().addItem(Item.aluminumItem.createNew((int) x + randX, (int) y + randY));
                }
            }

            else if (asteroidType == 3) //SILCION ASTEROID
            {  
                if (randLootNum == 1 || randLootNum == 3)
                {
                    handler.getWorld().getItemManager().addItem(Item.siliconItem.createNew((int) x + randX, (int) y + randY));
                }
            }

            if (randLootNum == 2 || randLootNum == 3)
            {
                handler.getWorld().getItemManager().addItem(Item.carbonItem.createNew((int) x + randX, (int) y + randY));
            }
        }
        active = false;
    }
    
    public void dropMaterials()
    {
        int randLootNum = handler.random(1, 4);
        int randX = handler.random(offSet, width - offSet)-12;
        int randY = handler.random(offSet, height - offSet)-12;
        
        if (asteroidType == 1) //Stansard ASTEROID
        {  
            randLootNum = handler.random(1, 3);
            
            if (randLootNum == 3)
            {
                handler.getWorld().getItemManager().addItem(Item.carbonItem.createNew((int) x + randX, (int) y + randY));
            }
        }
        
        else if (asteroidType == 2) //Aluminum ASTEROID
        {  
            if (randLootNum == 3)
            {
                handler.getWorld().getItemManager().addItem(Item.carbonItem.createNew((int) x + randX, (int) y + randY));
            }
            else if (randLootNum == 4)
            {
                handler.getWorld().getItemManager().addItem(Item.aluminumItem.createNew((int) x + randX, (int) y + randY));
            }
        }
        
        else if (asteroidType == 3) //Silicon
        {  
            if (randLootNum == 3)
            {
                handler.getWorld().getItemManager().addItem(Item.carbonItem.createNew((int) x + randX, (int) y + randY));
            }
            else if (randLootNum == 4)
            {
                handler.getWorld().getItemManager().addItem(Item.siliconItem.createNew((int) x + randX, (int) y + randY));
            }
        }
        
    }
    
    private void asteroidType()
    {
        int rand;
        int asteroidRandNum = handler.random(0, 100);
        
        if (asteroidRandNum >= 0 && asteroidRandNum < 12) //Silicon Asteroid
        {
            rand = handler.random(3,4);
            asteroidType = 3;
            image = Assets.randomAsteroid[rand];
        }
            
        else if (asteroidRandNum >= 12 && asteroidRandNum < 35) //Aluminum Asteroid
        {
            rand = handler.random(5,6);
            asteroidType = 2;
            image = Assets.randomAsteroid[rand];
        }
        else //Standard
        {
            rand = handler.random(0,2);
            asteroidType = 1;
            image = Assets.randomAsteroid[rand];
        }
        
    }
    
    private void resize()
    {
        int oldWidth = width;
        
        currentSize = health/10;
        width = (int)(DEFAULT_CREATURE_WIDTH*currentSize/2);
        if (width < 26)
        {
            width = 26;
        }
        height = width;
        radius = width/2;
        
        float offset = (oldWidth - width) /2;
        
        x = x + offset;
        y = y + offset;
        
        bounds[0] = new Rectangle(0,0,width,height);
        
        double rMulti = Math.sqrt(radius);
        mass = (float) (53/*<---- leave it!*/*rMulti);
    }
    
    private void createParticles(double amt, float x, float y, Entity e)
    {
        int numberOfParticles;
        double angleInRad;
        double totalSpeed = Math.hypot(currentXVelocity, currentYVelocity);
        int[] randPos;
        double[] randVel;
        /*
        int particleX = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirection)) * - 15));
        int particleY = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirection)) * - 15));
        float particleXVel = (float) (Math.cos(Math.toRadians(intendedDirection+getRandDirection(0,45))) * -acceleration * handler.random(100, 150));
        float particleYVel = (float) (Math.sin(Math.toRadians(intendedDirection+getRandDirection(0,45))) * -acceleration * handler.random(100, 150));
        handler.getWorld().getParticleManager().addParticle(new Particle(handler, particleX, particleY, particleXVel, particleYVel, Assets.engineColors[handler.random(3, 4)], handler.random(100, 400)));
        */
        
        
        if (diedTo != null)
        {
            double xDist = getCenterX() - diedTo.getCenterX();
            double yDist = getCenterY() - diedTo.getCenterY();
            angleInRad = Math.atan(yDist/xDist);
            if (getCenterX() < diedTo.getCenterX())
            {
                angleInRad = angleInRad+Math.PI;
            }
            
            if (!diedTo.isPlayer)
            {
                x = (float)(diedTo.getCenterX() + Math.cos(angleInRad)*diedTo.radius);
                y = (float)(diedTo.getCenterY() + Math.sin(angleInRad)*diedTo.radius);
            }
            else
            {
                x = (float)(getCenterX() + Math.cos(angleInRad)*radius);
                y = (float)(getCenterY() + Math.sin(angleInRad)*radius);
            }
            
            numberOfParticles = (int)(amt/20);
            
            if (asteroidType == 1) //carbon
            {
                if (diedTo.isPlanet || diedTo.isAsteroid)
                {
                    for (int c = 0; c< numberOfParticles; c++)
                    {
                        randVel = getRandCrashVelocity(angleInRad, totalSpeed);
                        randPos = getRandCoords(x, y, 5); 
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(2, 4)]));
                    }
                }
                else if (diedTo.isPlayer)
                {
                    numberOfParticles = 250;
                    for (int c = 0; c< numberOfParticles; c++)
                    {
                        randVel = getRandContinueVelocity(currentXVelocity, currentYVelocity, totalSpeed);
                        randPos = getRandCoords(x, y, radius); 
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(2, 4)]));
                    }
                    handler.getPlayer().addAsteroidDestroyed();
                }
            }

            else if (asteroidType == 2) //aluminum
            {
                if (diedTo.isPlanet || diedTo.isAsteroid)
                {
                    for (int c = 0; c< numberOfParticles; c++)
                    {
                        randVel = getRandCrashVelocity(angleInRad, totalSpeed);
                        randPos = getRandCoords(x, y, 5); 
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(2, 6)]));
                    }
                }
                else if (diedTo.isPlayer)
                {
                    numberOfParticles = 250;
                    for (int c = 0; c< numberOfParticles; c++)
                    {
                        randVel = getRandContinueVelocity(currentXVelocity, currentYVelocity, totalSpeed);
                        randPos = getRandCoords(x, y, radius); 
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(2, 6)]));
                    }
                }
            }

            else  if (asteroidType == 3) //silicon
            {
                if (diedTo.isPlanet || diedTo.isAsteroid)
                {
                    for (int c = 0; c< numberOfParticles; c++)
                    {
                        randVel = getRandCrashVelocity(angleInRad, totalSpeed);
                        randPos = getRandCoords(x, y, 5); 
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(0, 4)]));
                    }
                }
                else if (diedTo.isPlayer)
                {
                    numberOfParticles = 250;
                    for (int c = 0; c< numberOfParticles; c++)
                    {
                        randVel = getRandContinueVelocity(currentXVelocity, currentYVelocity, totalSpeed);
                        randPos = getRandCoords(x, y, radius); 
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(0, 4)]));
                    }
                }
            }
        }
        else
        {
            double xDist = getCenterX() - e.getCenterX();
            double yDist = getCenterY() - e.getCenterY();
            angleInRad = Math.atan(yDist/xDist);
            if (getCenterX() < e.getCenterX())
            {
                angleInRad = angleInRad+Math.PI;
            }
            x = (float)(getCenterX() + Math.cos(angleInRad)*radius*-1);
            y = (float)(getCenterY() + Math.sin(angleInRad)*radius*-1);
            
            if (amt > .5)
            {
                numberOfParticles = (int)(amt * (handler.random(3, 7)));
            }
            else
            {
                numberOfParticles = handler.random(0, 1);
                totalSpeed = totalSpeed / 10;
            }
            
            if (asteroidType == 1) //carbon
            {
                for (int c = 0; c< numberOfParticles; c++)
                {
                    randVel = getRandContinueVelocity(currentXVelocity, currentYVelocity, totalSpeed);
                    randPos = getRandCoords(x, y, 5); 
                    handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(2, 4)]));
                }
            }

            else if (asteroidType == 2) //aluminum
            {
                for (int c = 0; c< numberOfParticles; c++)
                {
                    randVel = getRandContinueVelocity(currentXVelocity, currentYVelocity, totalSpeed);
                    randPos = getRandCoords(x, y, 5); 
                    handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(2, 6)]));
                }
            }

            else  if (asteroidType == 3) //silicon
            {
                for (int c = 0; c< numberOfParticles; c++)
                {
                    randVel = getRandContinueVelocity(currentXVelocity, currentYVelocity, totalSpeed);
                    randPos = getRandCoords(x, y, 5); 
                    handler.getWorld().getParticleManager().addParticle(new Particle(handler, randPos[0], randPos[1], randVel[0], randVel[1], Assets.asteroidColors[handler.random(0, 4)]));
                }
            }
        }
    }
    
    private double[] getRandCrashVelocity(double angleInRad, double totalSpeed)
    {
        //+- 30~~ from opposite of initial direction = .6 rad
        double rand[] = new double[2];
        double randAngle = (double)(handler.random(0, 179));//60
        double speed = totalSpeed * Math.random();
        if (randAngle < 5)
        {
            //speed = speed;
        }
        else if (randAngle < 10)
        {
            speed *= .95;
        }
        else if (randAngle < 20)
        {
            speed *= .8;
        }
        else if (randAngle < 30)
        {
            speed *= .7;
        }
        else if (randAngle < 45)
        {
            speed *= .5;
        }
        else if (randAngle < 90)
        {
            speed *= .4;
        }
        else
        {
            speed *= .2;
        }
        
        if (handler.random(0, 1) == 1)
            randAngle = randAngle * -1;
        randAngle *= .0175;
        angleInRad += randAngle;
        
        
        rand[0] = Math.cos(angleInRad)*speed*.4;
        rand[1] = Math.sin(angleInRad)*speed*.4;
        
        return rand;
    }
    
    private double[] getRandContinueVelocity(double xVel, double yVel, double totalSpeed)
    {
        double[] rand = new double[2]; 
        int angle = handler.random(0, 359);
        double speed = totalSpeed * Math.random()/4;
        rand[0] = (Math.cos(angle * .0175)*speed)+(xVel/3);
        rand[1] = (Math.sin(angle * .0175)*speed)+(yVel/3);
        return rand;
    }
    
    private int[] getRandCoords(float x, float y, int maxOffset)
    {
        int[] rand = new int[2];
        int angle = handler.random(0, 359);
        int rad = handler.random(0, maxOffset);
        rand[0] = (int)(x+(rad*Math.cos(angle*.0175)));
        rand[1] = (int)(y+(rad*Math.sin(angle*.0175)));
        return rand;
    }
}
