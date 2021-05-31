package gravity;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Attack 
{
    protected Handler handler;
    protected float x, y;
    protected Entity attacker;
    protected boolean gravity;
    protected int attackDMG = 0;
    protected float attackSpeed = 0;
    
    protected long spawnedAtTime;
    protected long activeTime = -1;
    
    protected boolean onTick = true;
    protected boolean onRender = true;
    
    protected double directionInDegrees;
    
    protected BufferedImage image = null;
    
    protected boolean isActive = true;
    
    public Attack(Handler handler, float x, float y, double directionInDegrees, boolean gravity, Entity attacker, int attackDMG)
    {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.gravity = gravity;
        this.attacker = attacker;
        this.attackDMG = attackDMG;
        this.directionInDegrees = directionInDegrees;
        
        spawnedAtTime = System.currentTimeMillis();
    }
    public void tick()
    {
        move();
        checkCollision();
        other();
    }
    
    private void checkCollision()
    {
        for(Entity e : handler.getWorld().getEntityManager().getEntities())
        {                
            if (e.equals(attacker) || e.isNebula || (e.isEnemy && attacker.isEnemy))
            {
                continue;
            }

            if (e.onTick = true)
            {
                if (shotHit(x, y, e))
                {
                    e.damaged(attackDMG, attacker, x, y);//value of HP

                    createOnHitParticles();
                    isActive = false;
                }
            }
            
        }
    }
    
    public void render(Graphics g)
    {
        if (image != null)
        {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(Math.toRadians(directionInDegrees+90), (x - handler.getGameCamera().getxOffset())+8, (y - handler.getGameCamera().getyOffset())+8);
            g2d.drawImage(image, (int) (x - handler.getGameCamera().getxOffset()), (int) (y - handler.getGameCamera().getyOffset()), 16, 16, null);
            g2d.dispose();
        }
        else
        {
            createParticles();
        }
    }
    
    protected boolean shotHit(float x, float y, Entity e)
    {
        double xDistSq = Math.pow((e.getCenterX() - x), 2);
        double yDistSq = Math.pow((y - e.getCenterY()), 2);
        double eRadiusSq = Math.pow(e.width/2, 2);
        if ((xDistSq + yDistSq) <= (eRadiusSq))
        {
            return true;
        }
        return false;
    }
    
    
    protected abstract void move();
    protected abstract void other();
    protected abstract void createParticles();
    protected abstract void createOnHitParticles();
    
    protected int getRandDirection(int min, int max)
    {
        
        int randDirection = handler.random(min, max);
        if (handler.random(0, 1) == 1)
        {
            randDirection *= -1;
        }
        return randDirection;
    }
    
    public boolean getOnTick()
    {
        return onTick;
    }
    
    public void setOnTick(boolean TF)
    {
        onTick = TF;
    }
    
    public boolean getOnRender()
    {
        return onRender;
    }
    
    public void setOnRender(boolean TF)
    {
        onRender = TF;
    }
    
    
}
