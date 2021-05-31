package gravity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class UIBlasterBar extends UIObject
{
    private Handler handler;
    private double MAX_CHARGE;
    private double charge;
    private double currentGuagePercent;
    private double currentGuageWidth;
    private BufferedImage guageEmpty;
    private BufferedImage guageFiller;
    private int gEW;
    
    
    
    
    public UIBlasterBar(Handler handler, float x, float y, int width, int height, BufferedImage Empty, BufferedImage Filler) {
        super(handler, x, y, width, height, false);
        this.handler = handler;
        guageEmpty = Empty;
        guageFiller = Filler;
        MAX_CHARGE = handler.getWorld().getEntityManager().getPlayer().weaponMaxCharge;
        gEW = width;
        
        
    }

    @Override
    public void tick() 
    {
        if (handler.getPlayer().getShipLevel() >= 3)
        {
            charge = handler.getWorld().getEntityManager().getPlayer().getAttackTimer();
            currentGuagePercent = charge / MAX_CHARGE;
            currentGuageWidth = (charge / MAX_CHARGE)*gEW;
        }
    }

    @Override
    public void render(Graphics g) 
    {
        if (handler.getPlayer().getShipLevel() >= 3)
        {
            g.drawImage(guageEmpty,(int) x ,(int) y, width, height, null);
            if (charge > 0 && (int)(guageFiller.getWidth()*currentGuagePercent) > 0)
                g.drawImage(guageFiller.getSubimage(0, 0, (int)(guageFiller.getWidth()*currentGuagePercent), (int)guageFiller.getHeight()), (int) (x+(gEW - currentGuageWidth)), (int) y, (int) currentGuageWidth-(gEW / 100), height, null);
        }
    }

    @Override
    public void onClick() 
    {
        
    }
    
    
    
}