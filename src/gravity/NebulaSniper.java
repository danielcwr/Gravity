package gravity;

public class NebulaSniper extends Enemy
{
    //Swarm Enemy, appears in nebulas
    private int LorR;
    public NebulaSniper(Handler handler, float x, float y, boolean gravity)
    {
        super(handler, x, y, Assets.enemies[0], gravity);
        
        bounds[0].x = 16;
        bounds[0].y = 16;
        bounds[0].width = 32;
        bounds[0].height = 32;
        radius = 16;
        
        ATTACK_DMG = 15;
        ATTACK_COOLDOWN = 5000;
        MAX_SPEED = 2;
        MAX_ACCELERATION = .04f;
        TURNING_SPEED = .8;
        MAX_HEALTH = 11;
        health = MAX_HEALTH;
        
        mass = 150;
        
        LorR = handler.random(0, 1);
        
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
    public void attack()
    {
        if (System.currentTimeMillis() - lastAttack > ATTACK_COOLDOWN)
        {
            int x = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees)) * 26));
            int y = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees)) * 26));
            handler.getWorld().getAttackManager().addAttack(new NebulaSniperShot(handler, x, y, intendedDirectionInDegrees, false, this, ATTACK_DMG));
            lastAttack = System.currentTimeMillis();
        }
    }
    
    @Override
    public void getInput() 
    {
        pointAtTarget();
        
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
        
        if(intendedDirectionInDegrees > desiredDirectionInDegrees-2 && intendedDirectionInDegrees < desiredDirectionInDegrees+2)//forwards
        {
            float dist = distanceTo(targetEntity, this);
            if (dist*dist < 84100)//290^2
            {
                acceleration = MAX_ACCELERATION * -1;
                inputXAccel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
                inputYAccel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
            }
            else if (dist*dist > 96100)//310^2
            {
                acceleration = MAX_ACCELERATION;
                inputXAccel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
                inputYAccel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
            }
            else
            {
                moveStrategically();
            }
            attack();
        }
       
            
    }
    
    @Override
    public void moveStrategically()
    {
        acceleration = MAX_ACCELERATION/2;
        if (LorR == 0) //left
        {
            inputXAccel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees+90)) * acceleration);
            inputYAccel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees+90)) * acceleration);
        }
        else //right
        {
            inputXAccel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees-90)) * acceleration);
            inputYAccel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees-90)) * acceleration);
        }
    }
    
    @Override
    public void moveAimlessly()
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