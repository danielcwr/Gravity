package gravity;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UIMisc extends UIObject
{
    private Handler handler;
    
    //FOR AUTODRIVE INFICATOR
    private BufferedImage[] autoDriveIcon;
    
    
    // FOR ITEM COUNTER
    private BufferedImage siliconItem;
    private BufferedImage carbonItem;
    private BufferedImage aluminumItem;
    private BufferedImage plasmaItem;
    
    private int amountSilicon;
    private int amountCarbon;
    private int amountAluminum;
    private int amountPlasma;
    private ArrayList<Item> inventoryItems;

    
    //FOR GREEN ARROW INDICATOR
    private final BufferedImage largeIndicator;
    private final BufferedImage smallIndicator;
    private float[] largeIndicatorDistances = new float[2];
    private float[][] smallIndicatorDistances = new float[50][2];
    
    private double opacity = .5;
    private double opacityChange = .01;
    
    //Icon size + Scale stuff
    private final int arrowDistFromPlayer;
    private final int itemIconSize;
    private final int warningIndicatorSize;
    private final int warningIndicatorSpacing;
    private final int largeIndicatorSize;
    private final int smallIndicatorSize;
    private double scaleWidth;
    private double scaleHeight;
    private final float fontSize;
    
    private ArrayList<Entity> objectsOfInterest = null;
    
    
    private Font font;
    private Font leftPanelFont;
    
    public UIMisc(Handler handler) 
    {
        super(handler , 0, 0, 0, 0, false);
        this.handler = handler;
        
        this.scaleWidth = handler.getScaleWidth();
        this.scaleHeight = handler.getScaleHeight();
        
        //RESOURCE COUNT
        this.inventoryItems = handler.getWorld().getInventory().getInventoryItems();
        this.siliconItem = Assets.silicon;
        this.carbonItem = Assets.carbon;
        this.aluminumItem = Assets.aluminum;
        this.plasmaItem = Assets.plasma;
        
        //AUTODRIVE ICON
        this.autoDriveIcon = Assets.autoTractorBeamIcon;
        
        //Arrow indicator
        this.largeIndicator = Assets.arrowIndicator;
        this.smallIndicator = Assets.IACircleDot;
        
        //Scaleing stuff
        this.arrowDistFromPlayer = (int)(200*scaleWidth);
        this.largeIndicatorSize = (int)(24*scaleWidth);
        this.smallIndicatorSize = (int)(12*scaleWidth);
        this.itemIconSize = (int)(64*scaleWidth);
        this.warningIndicatorSize = this.itemIconSize*2;
        this.warningIndicatorSpacing = (int)(10*scaleWidth);
        this.fontSize = 74 * (float)scaleWidth;
        
        this.font = Assets.blockText.deriveFont(fontSize);
        leftPanelFont = Assets.blockText.deriveFont(20f);
    }

    @Override
    public void tick() 
    {
        tickResourceCount();
        tickLargeIndicator();
        if (handler.getWorld().getInventory().getSensorsEquiped())
        {
            tickSmallIndicators();
        }
    }

    @Override
    public void render(Graphics g) 
    {
        g.setFont(font);
        renderResourceCount(g);
        renderLeftPannel(g);
        if (!handler.getKeyManager().inventory)
        {
            renderRadarCircle(g);
            if (handler.getWorld().getInventory().getSensorsEquiped())
            {
                renderSmallIndicators(g);
            }
            renderLargeIndicator(g);
        }
        g.drawString(Integer.toString((int)handler.getPlayer().x) + ", " + Integer.toString((int)handler.getPlayer().y), (int)(screenWidth * .6), (int)(screenHeight * .9));
        
    }
    
    private void renderLeftPannel(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .85f));
        g2d.drawImage(Assets.invBackground, 0, 0, (int)(screenWidth * .225 * scaleWidth), (int)(screenHeight * .14 * scaleHeight), null);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, (int)(screenHeight*.14), (int)(screenWidth*.225*scaleWidth), (int)(20 * scaleHeight));
        g2d.setColor(Assets.afterBurnerColors[0]);
        g2d.fillRect(6, (int)(screenHeight*.14)+3, (int)(screenWidth*.225)-12, (int)(14 * scaleHeight));
        g2d.setColor(Assets.afterBurnerColors[1]);
        g2d.fillRect(6, (int)(screenHeight*.14)+3, (int)(((screenWidth*.225)-12)*((double)handler.getPlayer().getafterBurnerTimer()/5000)), (int)(14 * scaleHeight));
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int)(192*scaleWidth), (int)(55*scaleHeight), (int)(173*scaleWidth), 32);
        if (handler.getKeyManager().lArrow)
        {
            g2d.drawImage(Assets.lArrow[1], (int)(160*scaleWidth), (int)(55*scaleHeight), 32, 32, null);
        }
        else
        {
           g2d.drawImage(Assets.lArrow[0], (int)(160*scaleWidth), (int)(55*scaleHeight), 32, 32, null);
        }
        if (handler.getKeyManager().rArrow)
        {
            g2d.drawImage(Assets.rArrow[1], (int)(365*scaleWidth), (int)(55*scaleHeight), 32, 32, null);
        }
        else
        {
           g2d.drawImage(Assets.rArrow[0], (int)(365*scaleWidth), (int)(55*scaleHeight), 32, 32, null);
        }
        
        g2d.setColor(Color.yellow);
        g2d.setFont(leftPanelFont);
        int selectedWeapon = handler.getPlayer().getSelectedWeapon();
        if (selectedWeapon == 0)
        {
            g2d.drawString("Mining Laser", (int)(200*scaleWidth), (int)(80*scaleHeight));
        }
        else if (selectedWeapon == 1)
        {
            g2d.drawString("Plasma Blaster", (int)(200*scaleWidth), (int)(80*scaleHeight));
        }
        else if (selectedWeapon == 2)
        {
            g2d.drawString("Combat Laser", (int)(200*scaleWidth), (int)(80*scaleHeight));
        }
        else if (selectedWeapon == 3)
        {
            g2d.drawString("Rockets", (int)(200*scaleWidth), (int)(80*scaleHeight));
        }
        
        renderAutoTractorBeam(g2d);
        g2d.dispose();
    }
    
    // ITEM COUNTER
    private void renderResourceCount(Graphics g)
    {
        g.setColor(Color.yellow);
        
        g.drawImage(siliconItem, (int) (screenWidth*.9), (int) (screenHeight*.3), itemIconSize, itemIconSize, null);
        g.drawString(Integer.toString(amountSilicon), (int) (screenWidth*.9)+(int)(90*scaleWidth), (int) (screenHeight*.3)+itemIconSize);

        g.drawImage(carbonItem, (int) (screenWidth*.9), (int) (screenHeight*.4), itemIconSize, itemIconSize, null);
        g.drawString(Integer.toString(amountCarbon), (int) (screenWidth*.9)+(int)(90*scaleWidth), (int) (screenHeight*.4)+itemIconSize);

        g.drawImage(aluminumItem, (int) (screenWidth*.9), (int) (screenHeight*.5), itemIconSize, itemIconSize, null);
        g.drawString(Integer.toString(amountAluminum), (int) (screenWidth*.9)+(int)(90*scaleWidth), (int) (screenHeight*.5)+itemIconSize);

        g.drawImage(plasmaItem, (int) (screenWidth*.9), (int) (screenHeight*.6), itemIconSize, itemIconSize, null);
        g.drawString(Integer.toString(amountPlasma), (int) (screenWidth*.9)+(int)(90*scaleWidth), (int) (screenHeight*.6)+itemIconSize);
    }
    private void tickResourceCount()
    {
        for(Item i : inventoryItems)
        {
            if(i.getID() == 0)
                amountSilicon = i.getCount();
            if(i.getID() == 1)
                amountCarbon = i.getCount();
            if(i.getID() == 2)
                amountAluminum = i.getCount();
            if(i.getID() == 3)
                amountPlasma = i.getCount();
        }
    }
    
    // AUTODRIVE INDICATOR
    private void renderAutoTractorBeam(Graphics g)
    {
        if (handler.getPlayer().getAutoTractorBeam())//Autodrive on
        {
            g.drawImage(autoDriveIcon[1], warningIndicatorSpacing, warningIndicatorSpacing, warningIndicatorSize, warningIndicatorSize, null);
        }
        else
            g.drawImage(autoDriveIcon[0], warningIndicatorSpacing, warningIndicatorSpacing, warningIndicatorSize, warningIndicatorSize, null);//Autodrive Off
    }
    
    private void tickLargeIndicator()
    {
        if (handler.getPlayer().getObjectsOfInterest() != null)
        {
            objectsOfInterest = handler.getPlayer().getObjectsOfInterest();
            
            Player player = handler.getPlayer();
            Entity e = objectsOfInterest.get(0);
            
            ///THIS IS USEFULL AND EASY//////////////////////////////////////////////////////////////////////////////////////
            float distanceToX = (player.getCenterX() - e.getCenterX());
            float distanceToY = (player.getCenterY() - e.getCenterY());
            float total = (float)Math.sqrt(distanceToX*distanceToX + distanceToY*distanceToY);
            
            largeIndicatorDistances[0] = (distanceToX/total)*arrowDistFromPlayer*-1; //<-- remove the -1 and the arrow will point away!
            largeIndicatorDistances[1] = (distanceToY/total)*arrowDistFromPlayer*-1;
            
            // double angle = (180/Math.pi)*Math.tan((distanceToY/total)/(distanceToX/total))
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
           
        }
        if (opacity >= .9)
        {
            opacityChange = opacityChange *-1;
        }
        if (opacity <= .1)
        {
            opacityChange = opacityChange *-1;
        }
        opacity = opacity + opacityChange;
        //System.out.println(opacity);
        
    }
    
    private void tickSmallIndicators()
    {
        if (handler.getPlayer().getObjectsOfInterest() != null)
        {
            Player player = handler.getPlayer();
            for (int x = 0; x < objectsOfInterest.size()-1; x++)
            {
                Entity e = objectsOfInterest.get(x+1);

                float distanceToX = (player.getCenterX() - e.getCenterX());
                float distanceToY = (player.getCenterY() - e.getCenterY());
                float total = (float)Math.sqrt(distanceToX*distanceToX + distanceToY*distanceToY);

                smallIndicatorDistances[x][0] = (distanceToX/total)*arrowDistFromPlayer*-1; //<-- remove the -1 and the arrow will point away!
                smallIndicatorDistances[x][1] = (distanceToY/total)*arrowDistFromPlayer*-1;
            }
        }
    }
    
    private void renderLargeIndicator(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity));
        g2d.drawImage(largeIndicator, (int)(handler.getPlayer().getCenterX()-(largeIndicatorSize/2)+largeIndicatorDistances[0] -handler.getGameCamera().getxOffset()), (int)(handler.getPlayer().getCenterY()-(largeIndicatorSize/2)+largeIndicatorDistances[1] -handler.getGameCamera().getyOffset()), largeIndicatorSize, largeIndicatorSize, null);
        g2d.dispose();
    }
    
    private void renderSmallIndicators(Graphics g)
    {
        for(int c = 1; c <objectsOfInterest.size()-1; c++)
        {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
            g2d.drawImage(smallIndicator, (int) (handler.getPlayer().getCenterX() + smallIndicatorDistances[c][0] -(smallIndicatorSize/2) - handler.getGameCamera().getxOffset()), (int) (handler.getPlayer().getCenterY()+ smallIndicatorDistances[c][1] -(smallIndicatorSize/2) -handler.getGameCamera().getyOffset()), smallIndicatorSize , smallIndicatorSize , null);
            g2d.dispose();
        }
    }
    
    private void renderRadarCircle(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .1f));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawOval((int) (handler.getPlayer().getCenterX() - arrowDistFromPlayer - handler.getGameCamera().getxOffset()), (int) (handler.getPlayer().getCenterY() - arrowDistFromPlayer - handler.getGameCamera().getyOffset()), arrowDistFromPlayer*2, arrowDistFromPlayer*2);
        g2d.dispose();
    }
    
    //LEAVE BLANK!
    @Override
    public void onClick() {}
}

