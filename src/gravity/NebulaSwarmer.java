package gravity;

import java.awt.image.BufferedImage;

public class NebulaSwarmer extends Enemy
{
    //Swarm Enemy, appears in nebulas
    public NebulaSwarmer(Handler handler, float x, float y, boolean gravity)
    {
        super(handler, x, y, Assets.enemies[1], gravity);
        
        bounds[0].x = 16;
        bounds[0].y = 16;
        bounds[0].width = 32;
        bounds[0].height = 32;
        radius = 16;
        
        ATTACK_DMG = 25;
        ATTACK_COOLDOWN = 3000;
        MAX_SPEED = 3;
        MAX_ACCELERATION = .06f;
        TURNING_SPEED = .8;
        MAX_HEALTH = 20;
        health = MAX_HEALTH;
        
        mass = 150;
        
        isNebulaSwarmer = true;
    }
    
    @Override
    public void getTarget()
    {
        if (targetEntity != null)
        {
            if (!targetEntity.active)
            {
                targetEntity = null;
            }
        }
        if (targetItem != null)
        {
            if (targetItem.isDestroyed || targetItem.isPickedUp())
            {
                targetItem = null;
            }
        }
        
        
        if (targetItem == null)
        {
            float distanceSqr = 10000000;
            Item closest = null;
            for (Item i : handler.getWorld().getItemManager().getItems())
            {
                if (i.id == 3) //plasma
                {
                    if (closest == null)
                    {
                        distanceSqr = distanceToItem(i);
                        closest = i;
                    }
                    else
                    {
                        float testDistance = distanceToItem(i);
                        if (testDistance*testDistance < distanceSqr*distanceSqr)
                        {
                            distanceSqr = testDistance*testDistance;
                            closest = i;
                        }
                    }
                }
            }
            if (closest != null)
            {
                targetItem = closest;
            }
        }
        if (targetEntity == null)
        {
            if (handler.getPlayer().getShipLevel() >= 3)
            {
                targetEntity = handler.getPlayer();
            }
        }
        
    }
    
    @Override
    public void renderThruster() 
    {
        
    }

    @Override
    public void getInput() 
    {
        pointAtTarget();
        if(intendedDirectionInDegrees > desiredDirectionInDegrees-5 && intendedDirectionInDegrees < desiredDirectionInDegrees+5)//forwards
        {
            acceleration = MAX_ACCELERATION;
        }
        
        double temp = desiredDirectionInDegrees - intendedDirectionInDegrees;
        if (desiredDirectionInDegrees > intendedDirectionInDegrees)
        {
            if (temp < 180)
            {
                intendedDirectionInDegrees += TURNING_SPEED;
            }
            else
            {
                intendedDirectionInDegrees -= TURNING_SPEED;
            }
        }
        else
        {
            if (temp> -180)
            {
                intendedDirectionInDegrees -= TURNING_SPEED;
            }
            else
            {
                intendedDirectionInDegrees += TURNING_SPEED;
            }
        }

        inputXAccel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
        inputYAccel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
    }
    
    @Override
    public void moveStrategically()
    {
        
    }
    
    @Override
    public void moveAimlessly()
    {
        
    }
    
    @Override
    public void attack()
    {
        
    }
    
    @Override
    public void die() 
    {
        deathParticles();
        active = false;
    }
    
    public void deathParticles()
    {
        
    }

    
    
}