package gravity;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class ItemManager  //STORES ITEMS IN GAME ON GROUND
{
    private Handler handler;
    private ArrayList<Item> items;
    
    private int pX; //player x
    private int pY; //player y
    private int screenLeftX; //render distance extremes--vv--
    private int screenTopY;
    private int screenRightX;
    private int screenBottomY; //^^
    
    private int renderMarginWidth;
    private int renderMarginHeight;
    
    private long lastPlasmaSpawn = 0;
    private int plasmaAmount = 0;
    
    //despawn
    private int numberOfItems;
    
    public ItemManager(Handler handler)
    {
        this.handler = handler;
        
        this.renderMarginWidth = handler.getScreenWidth();
        this.renderMarginHeight = handler.getScreenHeight();
        
        items = new ArrayList<Item>();
        
    }
    
    public void tick()
    {
        pX = (int) handler.getPlayer().getCenterX();
        pY = (int) handler.getPlayer().getCenterY();
        screenLeftX = pX - renderMarginWidth;
        screenTopY = pY - renderMarginHeight;
        screenRightX = pX + renderMarginWidth;
        screenBottomY = pY + renderMarginHeight;
        
        numberOfItems = 0;
        plasmaAmount = 0;

        Iterator<Item> it = items.iterator();
        while(it.hasNext())
        {
            Item i = it.next();
            
            if (i.getCenterX() > screenLeftX && i.getCenterY() > screenTopY && i.getCenterX() < screenRightX && i.getCenterY() < screenBottomY) 
            {
                i.tick();
            }
            else 
            {
                it.remove();
            }
            
            if(i.isPickedUp())
            {
                handler.getPlayer().addMaterialsCollected();
                it.remove();
            }
            
            if(i.isDestroyed())
            {
                it.remove();
            }
            
            if (i.id == 3) //plasma
            {
                plasmaAmount++;
            }
            
            if (i.timerActivated == -1)
            {
                numberOfItems++;
            }
        }
        
        if ((boolean)handler.getPlayer().isNearNebula()[0] && System.currentTimeMillis() - lastPlasmaSpawn > 5000 && plasmaAmount < 15)
        {
            addItem(Item.plasmaItem.createNew(handler.random((int)(handler.getPlayer().x-handler.getScreenWidth()/2), (int) (handler.getPlayer().x+handler.getScreenWidth()/2)), handler.random((int)(handler.getPlayer().y-handler.getScreenHeight()/2), (int) (handler.getPlayer().y+handler.getScreenHeight()/2))));
            lastPlasmaSpawn = System.currentTimeMillis();
        }
        
        if (numberOfItems > 30)
        {
            for (int c = 0; c < items.size(); c++)
            {
                if (items.get(c).timerActivated == -1)
                {
                    items.get(c).timerActivated = System.currentTimeMillis();
                    break;
                }
            }
        }
    }
    
    public void render(Graphics g)
    {
        for (Item i : items)
        {
            i.render(g);
        }
    }
    
    public void addItem(Item i)
    {
        i.setHandler(handler);
        items.add(i);
    }
    
    public ArrayList<Item> getItems()
    {
        return items;
    }
    
    
    //Getters and Setters
    public Handler getHandler()
    {
        return handler;
    }
    
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }
}
