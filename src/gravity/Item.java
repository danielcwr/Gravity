package gravity;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Item 
{
    //Handler
    public static Item[] items = new Item[4];
    public static Item siliconItem = new Item(Assets.silicon, "Silicon", 0); //ID 0
    public static Item carbonItem = new Item(Assets.carbon, "Carbon", 1); //ID 1
    public static Item aluminumItem = new Item(Assets.aluminum, "Aluminum", 2); //ID 1
    public static Item plasmaItem = new Item(Assets.plasma, "Plasma", 3);
    
    //Class Stuff
    public static final int ITEMWIDTH = 24, ITEMHEIGHT = 24;
    
    protected Handler handler;
    protected BufferedImage texture;
    protected String name;
    protected final int id;
    
    protected Rectangle bounds;
    
    protected float x, y;
    protected float XVelocity = 0, YVelocity = 0;
    protected int count;
    protected boolean pickedUp = false;
    protected boolean isDestroyed = false;
    
    protected double timerActivated = -1;
    
    public Item(BufferedImage texture, String name, int id)
    {
        this.texture = texture;
        this.name = name;
        this.id = id;
        count = 1;
        
        bounds = new Rectangle((int)x, (int)y, ITEMWIDTH, ITEMHEIGHT);
        
        items[id] = this;
        
    }
    
    public void tick()
    {
        bounds.x = (int)x;
        bounds.y = (int)y;
        if(handler.getWorld().getEntityManager().getPlayer().getCollisionBounds(0, 0)[0].intersects(bounds))
        {
            pickedUp = true;
            handler.getWorld().getInventory().addItem(this);
        }
        if (XVelocity*XVelocity > 0 || YVelocity*YVelocity > 0)
        {
            for (Entity e : handler.getWorld().getEntityManager().getEntities())
            {
                if(e.gravity)
                {
                    double xDist = getCenterX() - e.getCenterX();
                    double yDist = getCenterY() - e.getCenterY();
                    double sqrDist = xDist*xDist + yDist*yDist;
                    if (e.radius * e.radius >= sqrDist)
                    {
                        isDestroyed = true;
                    }
                }
            }
        }
        if(id == 3)
        {
            tickPlasma();
        }
        
        if (timerActivated != -1)
        {
            if (System.currentTimeMillis() - timerActivated > 10000)
            {
                isDestroyed = true;
            }
        }
        
        x += XVelocity;
        y += YVelocity;
    }
    
    public void tickPlasma()
    {
        for (Entity e : handler.getWorld().getEntityManager().getEnemies())
        {
            if (e.getCollisionBounds(0, 0)[0].intersects(bounds))
            {
                isDestroyed = true;
            }
        }
    }

    public void render(Graphics g) //render Item on Game World
    {
        if (handler == null)
            return;
        if (timerActivated != -1)
        {
            Graphics2D g2d = (Graphics2D) g.create();
            double timeLeft = 1 - (System.currentTimeMillis() - timerActivated) / 10000;
            if (timeLeft > 0 && timeLeft <= 1)
            {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)timeLeft));
                g2d.drawImage(texture, (int) (x - handler.getGameCamera().getxOffset()),(int) (y - handler.getGameCamera().getyOffset()), ITEMWIDTH, ITEMHEIGHT, null);
            }
            g2d.dispose();
        }
        else
            render(g, (int) (x - handler.getGameCamera().getxOffset()),(int) (y - handler.getGameCamera().getyOffset()));
    }
    
    public void render(Graphics g, int x, int y) //Render item on screen in certain Pos (Inventory)
    {
        g.drawImage(texture, x, y, ITEMWIDTH, ITEMHEIGHT, null);
    }
    
    public Item createNew(int x, int y)
    {
        Item i = new Item(texture, name, id);
        i.setPosition(x, y);
        return i;
    }
    
    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
        bounds.x = x;
        bounds.y = y;
    }
    
    
    //Getters Setters
    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getXVelocity()
    {
        return XVelocity;
    }
    
    public void setXVelocity(float Xvelocity)
    {
        XVelocity = Xvelocity;
    }
    
    public float getYVelocity()
    {
        return YVelocity;
    }
    
    public void setYVelocity(float Yvelocity)
    {
        YVelocity = Yvelocity;
    }
    
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public int getID()
    {
        return id;
    }
    
    public boolean isPickedUp()
    {
        return pickedUp;
    }
    
    public boolean isDestroyed()
    {
        return isDestroyed;
    }
    
    public float getCenterX()
    {
        return x + (bounds.width/2);
    }
    
    public float getCenterY()
    {
        return y + (bounds.height/2);
    }
    
    
}
