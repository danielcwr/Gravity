package gravity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class UIHealthBar extends UIObject
{
    private Handler handler;
    private boolean invincible = false;
    private double MAX_HEALTH;
    private double health;
    private double currentGuage;
    private double currentInvincibleGuage;
    private final BufferedImage guageEmpty;
    private final BufferedImage guageFiller;
    private final BufferedImage invincibleFiller;
    private int gEW;
    
    private Animation lowHP;
    
    public UIHealthBar(Handler handler, float x, float y, int width, int height, BufferedImage Empty, BufferedImage Filler, BufferedImage Invincible) {
        super(handler , x, y, width, height, true);
        this.handler = handler;
        guageEmpty = Empty;
        guageFiller = Filler;
        invincibleFiller = Invincible;
        MAX_HEALTH = handler.getWorld().getEntityManager().getPlayer().getMAX_HEALTH();
        gEW = width;
        
        lowHP = new Animation(250, Assets.lowHP);
    }

    @Override
    public void tick() 
    {
        MAX_HEALTH = handler.getPlayer().getMAX_HEALTH();
        health = handler.getPlayer().getHealth();
        invincible = handler.getPlayer().getInvincible();
        if (invincible)
        {
            //System.out.println("invincible: " + (double) handler.getPlayer().getInvincibleTimeLeft() + "/" + handler.getPlayer().getInvincibleTime());
            currentInvincibleGuage = (((double)(handler.getPlayer().getInvincibleTimeLeft())/((double)handler.getPlayer().getInvincibleTime()))*gEW);
        }
        currentGuage = (health / MAX_HEALTH)*gEW;
        lowHP.tick();
    }

    @Override
    public void render(Graphics g) 
    {

        g.drawImage(guageEmpty,(int) x ,(int) y, width, height, null);
        int currentGuageTemp = (int)currentGuage/4;
        if (currentGuageTemp > 0)
        {
            if(health < MAX_HEALTH/4 && health > 0)
                g.drawImage(getFrame().getSubimage(0, 0, (int)currentGuage/4, 20), (int) (x+(gEW - currentGuage)), (int) y, (int) currentGuage-(gEW / 100), height, null);
            else if(health > 0)
                g.drawImage(guageFiller.getSubimage(0, 0, (int)currentGuage/4, 20), (int) (x+(gEW - currentGuage)), (int) y, (int) currentGuage-(gEW / 100), height, null);
        }
        if (invincible)
        {
            int currentInvincibleGuageTemp = (int)currentInvincibleGuage/4;
            if (currentInvincibleGuageTemp > 0)
            {
                g.drawImage(invincibleFiller.getSubimage(0, 0, (int)currentInvincibleGuage/4, 20), (int) (x+(gEW - currentInvincibleGuage)), (int) y, (int) currentInvincibleGuage-(gEW / 100), height, null);
            }
        }
            
    }

    @Override
    public void onClick() 
    {
        
    }
    
    private BufferedImage getFrame()
    {
        return lowHP.getCurrentFrame();
    }
    
}
