package gravity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class UIImageButton extends UIObject
{
    private Handler handler;
    public BufferedImage[] images;
    private ClickListener clicker;
    private Renderer renderer = null;
    private int invState;
    
    public UIImageButton(Handler handler, float x, float y, int width, int height, BufferedImage[] images, ClickListener clicker, boolean hoverable) {
        super(handler, x, y, width, height, hoverable);
        this.images = images;
        this.clicker = clicker;
        this.handler = handler;
    }
    
    public UIImageButton(Handler handler, float x, float y, int width, int height, BufferedImage[] images, ClickListener clicker, boolean hoverable, Renderer uniqueRender) {
        super(handler, x, y, width, height, hoverable);
        this.images = images;
        this.clicker = clicker;
        this.handler = handler;
        this.renderer = uniqueRender;
    }

    @Override
    public void tick() 
    {
        
    }

    @Override
    public void render(Graphics g) 
    {
        if (renderer != null)
        {
            uniqueRender(g);
        }
        else if (hoverable)
        {
            if(hovering)
            {
                g.drawImage(images[1], (int) x, (int) y, width, height, null);
            }
            else
            {
                g.drawImage(images[0], (int) x, (int) y, width, height, null);
            }
        }
        else
        {
            invState = handler.getWorld().getInventory().getInvState();
            g.drawImage(images[invState], (int) x, (int) y, width, height, null);
        }
    }

    @Override
    public void onClick() 
    {
        clicker.onClick();
    }
    
    public void uniqueRender(Graphics g)
    {
        renderer.uniqueRender(g, this);
    }
    
    
}
