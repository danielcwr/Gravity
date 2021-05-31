package gravity;

import java.awt.Color;

public class NebulaSniperShot extends Attack
{
    private Color[] shotParticles;
    public NebulaSniperShot(Handler handler, float x, float y, double directionInDegrees, boolean gravity, Entity attacker, int attackDMG)
    {
        super(handler, x, y, directionInDegrees, gravity, attacker, attackDMG);
        attackSpeed = 5f;
        shotParticles = Assets.nebSniperShotColors;
    }
    
    @Override
    protected void move()
    {
        x += (float) (Math.cos(Math.toRadians(directionInDegrees)) * attackSpeed);
        y += (float) (Math.sin(Math.toRadians(directionInDegrees)) * attackSpeed);
    }
    
    @Override
    protected void createParticles()
    {
        float particleXVel = (float) (Math.cos(Math.toRadians(directionInDegrees + getRandDirection(0,40))) * (attackSpeed/3));
        float particleYVel = (float) (Math.sin(Math.toRadians(directionInDegrees + getRandDirection(0,40))) * (attackSpeed/3));
        handler.getWorld().getParticleManager().addParticle(new Particle(handler, (int)x, (int)y, particleXVel, particleYVel, shotParticles[handler.random(0, 2)], 100));
        //g2d.drawImage(getLaserFrame(), (shotsHitBox1.x-32) - (int)(handler.getGameCamera().getxOffset()), (shotsHitBox1.y-32) - (int)(handler.getGameCamera().getyOffset()), null);
    }
    
    @Override
    protected void createOnHitParticles()
    {
        if (isActive)
        {
            for (int c  = 0; c < 30; c++)
            {
                float particleXVel = (float) (Math.cos(Math.toRadians(c*12)) * .5);
                float particleYVel = (float) (Math.sin(Math.toRadians(c*12)) * .5);
                handler.getWorld().getParticleManager().addParticle(new Particle(handler, (int)x, (int)y, particleXVel, particleYVel, shotParticles[handler.random(0, 2)], 500));
            }
            isActive = false;
        }
    }
    
    @Override
    protected void other()
    {
        
    }
}
