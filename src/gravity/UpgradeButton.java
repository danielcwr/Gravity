package gravity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UpgradeButton extends UIObject
{
    private Handler handler;
    private BufferedImage[] images = Assets.upgradeButton;
    private ClickListener clicker;
    private int affordable = 0;
    private int[] array;
    private String Upgrade;
    private ArrayList<Item> inventoryItems;
    
    private boolean enoughSilicon;
    private boolean enoughCarbon;
    private boolean enoughAluminum;
    private boolean enoughPlasma;
    
    public UpgradeButton(Handler handler, float x, float y, int width, int height, String Upgrade, ClickListener clicker, int[] array) {
        super(handler, x, y, width, height, false);
        this.clicker = clicker;
        this.handler = handler;
        this.array = array;
        this.Upgrade = Upgrade;
    }

    @Override
    public void tick() 
    {
        ///////
        if (checkMaterials())
        {
            affordable = 1;
        }
        else
        {
            affordable = 0;
        }
    }

    @Override
    public void render(Graphics g) 
    {
        g.drawImage(images[affordable], (int) x, (int) y, width, height, null);
    }

    @Override
    public void onClick() 
    {
        clicker.onClick();
    }
    
    public boolean checkMaterials()
    {
        resetBooleans();
        inventoryItems = handler.getWorld().getInventory().getInventoryItems();
        if (inventoryItems.isEmpty())
        {
            return false;
        }
        for (Item i : inventoryItems)
        {
            if ((i.getID() == 0 && i.getCount() >= array[0]) || array[0] == 0)//Silicon
            {
                enoughSilicon = true;
            }
            if ((i.getID() == 1 && i.getCount() >= array[1]) || array[1] == 0)//Carbon
            {
                enoughCarbon = true;
            }
            if ((i.getID() == 2 && i.getCount() >= array[2]) || array[2] == 0)//Aluminum
            {
                enoughAluminum = true;
            }
            if ((i.getID() == 3 && i.getCount() >= array[3]) || array[3] == 0)//Plasma
            {
                enoughPlasma = true;
            }
        }
        if(enoughSilicon && enoughCarbon && enoughAluminum && enoughPlasma)
        {
            return true;
        }
        return false;
    }
    
    public void resetBooleans()
    {
        enoughSilicon = false;
        enoughCarbon = false;
        enoughAluminum = false;
        enoughPlasma = false;
    }
    
    
}
