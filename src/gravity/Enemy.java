package gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;


public abstract class Enemy extends DynamicEntity
{
    private BufferedImage shipImage;
    
    protected boolean usingThruster = false;
    protected double desiredDirectionInDegrees = 0;
    
    protected Entity targetEntity;
    protected Item targetItem;
    
    protected int ATTACK_DMG = 10;
    protected int ATTACK_COOLDOWN = 1000;
    protected double MAX_SPEED = 4;
    protected float MAX_ACCELERATION = .02f;
    protected double TURNING_SPEED = 1.2;
    protected int MAX_HEALTH = 50;
    
    protected long lastAttack = 0;
    
    protected boolean isNebulaSwarmer = false;
    protected boolean isNebulaSniper = false;
    
    public Enemy(Handler handler, float x, float y, BufferedImage image, boolean gravity)
    {
        super(handler, x, y, DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, gravity);
        shipImage = image;
        mass = 300;
        isEnemy = true;
        
        //Define bounds
        //Define Health
        
    }
    
    public abstract void attack();
    public abstract void renderThruster();
    public abstract void getInput();
    public abstract void getTarget();
    public abstract void moveStrategically();
    public abstract void moveAimlessly();
    
    @Override
    public void tick() 
    {
        getTarget();
        getInput();
        move();
        intendedDirectionInDegrees = (float)(intendedDirectionInDegrees + Math.ceil( -intendedDirectionInDegrees / 360 ) * 360);
    }

    @Override
    public void render(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(intendedDirectionInDegrees+90), (x - handler.getGameCamera().getxOffset())+width/2, (y - handler.getGameCamera().getyOffset())+height/2);
        g2d.drawImage(shipImage, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), width, height, null);
        g2d.dispose();
        if (usingThruster)
        {
            renderThruster();
        }
    }
    
    private double getRandVel(double currentVel)
    {
        double randVel = currentVel/handler.random(3, 8)* Math.random();
        if (handler.random(0, 1) == 1)
        {
            if (handler.random(0, 1) == 1)
            {
                randVel += Math.random();
            }
            else
            {
                randVel -= Math.random();
            }
        }
        return randVel;
    }
    
    private int getRandCoord(float coord, int maxOffset)
    {
        int randCoord = (int)(coord+(handler.random(0, maxOffset)*Math.cos(handler.random(0, 359)*.0175)));
        return randCoord;
    }
    
    protected void pointAtTarget()
    {
        
        /*//THIS IS USEFULL AND EASY//////////////////////////////////////////////////////////////////////////////////////
        float distanceToX = (player.getCenterX() - e.getCenterX());
        float distanceToY = (player.getCenterY() - e.getCenterY());
        float total = (float)Math.sqrt(distanceToX*distanceToX + distanceToY*distanceToY);

        largeIndicatorDistances[0] = (distanceToX/total)*arrowDistFromPlayer*-1; //<-- remove the -1 and the arrow will point away!
        largeIndicatorDistances[1] = (distanceToY/total)*arrowDistFromPlayer*-1;

        // double angle = (180/Math.pi)*Math.tan((distanceToY/total)/(distanceToX/total))
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
        
        if (targetEntity != null)
        {
            float xDistance = (targetEntity.getCenterX() - getCenterX());
            float yDistance = (targetEntity.getCenterY() - getCenterY());
            //float hyp = (float)Math.sqrt(xDistance*xDistance + yDistance*yDistance);

            double angleInRad = Math.atan(yDistance/xDistance);

            desiredDirectionInDegrees = Math.toDegrees(angleInRad);
            if (getCenterX() > targetEntity.getCenterX())
            {
                desiredDirectionInDegrees += 180;
            }
            intendedDirectionInDegrees = (float)desiredDirectionInDegrees;
        }
        else if (targetItem != null)
        {
            float xDistance = (targetItem.getCenterX() - getCenterX());
            float yDistance = (targetItem.getCenterY() - getCenterY());

            double placeholder = Math.tan(yDistance/xDistance);

            if (getCenterX() > handler.getPlayer().getCenterX())
                placeholder = placeholder + Math.PI;

            desiredDirectionInDegrees = Math.toDegrees(placeholder);
        }
        else
        {
            moveAimlessly();
        }
    }
    
    public float distanceToItem(Item item)
    {
        float distanceToX = Math.abs(getCenterX() - item.getCenterX());
        float distanceToY = Math.abs(getCenterY() - item.getCenterY());
        float HypSquared = (distanceToX*distanceToX) + (distanceToY*distanceToY);
        return HypSquared;
    }
    
    
}