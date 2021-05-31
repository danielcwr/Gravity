package gravity;

import java.awt.Font;
import java.awt.Graphics;

public class InfoState extends State
{
    private UIManager mainInfoManager;
    private UIManager controlsInfoManager;
    private UIManager inventoryInfoManager;
    private UIManager objectivesInfoManager;
    private UIManager scoringInfoManager;
    
    private long lastReset;
    
    private int infoState = 0;
    private int screenWidth = handler.getScreenWidth();
    private int screenHeight = handler.getScreenHeight();
    private final double scaleWidth = handler.getScaleWidth();
    private final double scaleHeight = handler.getScaleHeight();
    private final int buttonY = (int)(screenWidth * .04);
    
    private double buttonWidth = 192*scaleWidth*1.75;
    private double buttonHeight = 64*scaleHeight*1.75;
    private Font smallFont = Assets.blockText.deriveFont((float)(40f*scaleWidth));
    
    //text stuff
    private int xMargin = (int)(screenWidth * .078125);
    private int yMargin = (int)(screenHeight* .277777);
    private int lineYOffset = (int) (smallFont.getSize()*1.2);
    
    public InfoState(final Handler handler)
    {
        super(handler);
        mainInfoManager = new UIManager(handler);
        controlsInfoManager = new UIManager(handler);
        inventoryInfoManager = new UIManager(handler);
        objectivesInfoManager = new UIManager(handler);
        scoringInfoManager = new UIManager(handler);
        
        initiateMainInfoManager();
        initiateControlsInfoManager();
        initiateInventoryInfoManager();
        initiateObjectivesInfoManager();
        initiateScoringInfoManager();
        lastReset = System.currentTimeMillis();
        
        handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, controlsInfoManager});
        
    }
    
    public void initiateMainInfoManager()
    {
        mainInfoManager.clearUIManager();
        mainInfoManager.addObject(new UIAnimatedImage(handler, 0, 0, screenWidth, screenHeight, Assets.invBackground)); //****************CHANGE THIS BACKGROUND
        mainInfoManager.addObject(new UIImageButton(handler, (int)(screenWidth*.05), buttonY, 
                                               (int)buttonWidth, (int)buttonHeight, Assets.back_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                handler.getMouseManager().setUIManager(null);
                State.setState(new MenuState(handler));
            }
        }, true));
        
        //controls
        mainInfoManager.addObject(new UIImageButton(handler, (int)(screenWidth*.065+buttonWidth), buttonY, 
                                               (int)buttonWidth, (int)buttonHeight, Assets.controls_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                System.out.println(infoState + "clicked");
                infoState = 0;
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, controlsInfoManager});
                System.out.println(infoState);
                
            }
        }, true, new Renderer()
        {
            @Override
            public void uniqueRender(Graphics g, UIImageButton button)
            {
                if (infoState == 0)
                {
                    g.drawImage(button.images[1], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
                else
                {
                    g.drawImage(button.images[0], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
            }
        }));

        //inventory
        mainInfoManager.addObject(new UIImageButton(handler, (int)(screenWidth*.065+buttonWidth*2), buttonY, 
                                               (int)buttonWidth, (int)buttonHeight, Assets.inventory_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                infoState = 1;
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, inventoryInfoManager});
            }
        }, true, new Renderer()
        {
            @Override
            public void uniqueRender(Graphics g, UIImageButton button)
            {
                if (infoState == 1)
                {
                    g.drawImage(button.images[1], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
                else
                {
                    g.drawImage(button.images[0], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
            }
        }));
        
        //objectives
        mainInfoManager.addObject(new UIImageButton(handler, (int)(screenWidth*.065+buttonWidth *3), buttonY, 
                                               (int)buttonWidth, (int)buttonHeight, Assets.objectives_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                infoState = 2;
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, objectivesInfoManager});
            }
        }, true, new Renderer()
        {
            @Override
            public void uniqueRender(Graphics g, UIImageButton button)
            {
                if (infoState == 2)
                {
                    g.drawImage(button.images[1], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
                else
                {
                    g.drawImage(button.images[0], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
            }
        }));
        
        //scoring points
        mainInfoManager.addObject(new UIImageButton(handler, (int)(screenWidth*.065+buttonWidth *4), buttonY, 
                                               (int)buttonWidth, (int)buttonHeight, Assets.scoringPoints_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                infoState = 3;
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, scoringInfoManager});
            }
        }, true, new Renderer()
        {
            @Override
            public void uniqueRender(Graphics g, UIImageButton button)
            {
                if (infoState == 3)
                {
                    g.drawImage(button.images[1], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
                else
                {
                    g.drawImage(button.images[0], (int)button.x, (int)button.y, (int)button.width, (int)button.height, null);
                }
            }
        }));
    }
    
    public void initiateControlsInfoManager()
    {
        controlsInfoManager.clearUIManager();
        controlsInfoManager.addObject(new UIImageButton(handler, (int)(xMargin - lineYOffset*1.5), (int)(yMargin + lineYOffset*5.2), 
                                               (int)lineYOffset, (int)lineYOffset, Assets.questionMark_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                System.out.println("To-Do: brings to another state describing tractor beam in detail");
            }
        }, true));
        
        controlsInfoManager.addObject(new UIImageButton(handler, (int)(xMargin - lineYOffset*1.5), (int)(yMargin + lineYOffset*12.2), 
                                               (int)lineYOffset, (int)lineYOffset, Assets.questionMark_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                infoState = 1;
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, inventoryInfoManager});
            }
        }, true));
    }
    
    public void initiateInventoryInfoManager()
    {
        inventoryInfoManager.clearUIManager();
    }
    
    public void initiateObjectivesInfoManager()
    {
        objectivesInfoManager.clearUIManager();
        objectivesInfoManager.addObject(new UIImageButton(handler, (int)(xMargin - lineYOffset*1.5), (int)(yMargin + lineYOffset*10.2), 
                                               (int)lineYOffset, (int)lineYOffset, Assets.questionMark_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                infoState = 3;
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, scoringInfoManager});
            }
        }, true));
    }
    
    public void initiateScoringInfoManager()
    {
        scoringInfoManager.clearUIManager();
    }
    
    @Override
    public void tick() 
    {
        if (System.currentTimeMillis() - lastReset > 750)
        {
            initiateMainInfoManager();
            initiateControlsInfoManager();
            initiateInventoryInfoManager();
            initiateObjectivesInfoManager();
            initiateScoringInfoManager();
            if (infoState == 0)
            {
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, controlsInfoManager});
            }
            else if (infoState == 1)
            {
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, inventoryInfoManager});
            }
            else if (infoState == 2)
            {
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, objectivesInfoManager});
            }
            else if (infoState == 4)
            {
                handler.getMouseManager().setUIManager(new UIManager[]{mainInfoManager, scoringInfoManager});
            }
            
            lastReset = System.currentTimeMillis();
        }
        
    }

    @Override
    public void render(Graphics g) 
    {
        mainInfoManager.render(g);
        g.setFont(smallFont);
        if (infoState <= 0) //controls
        {
            controlsInfoManager.render(g);
            g.drawString("W - Forward", xMargin, yMargin);                   
            g.drawString("S - Brake", xMargin, yMargin + lineYOffset);                     
            g.drawString("A - Turn left", xMargin, yMargin + lineYOffset*2);
            g.drawString("D - Turn right", xMargin, yMargin + lineYOffset*3);
            g.drawString("SHIFT - Activate afterburners", xMargin, yMargin + lineYOffset*4);

            g.drawString("R - Toggle tractor beam mode", xMargin, yMargin + lineYOffset*6);//*** 
            g.drawString("L MOUSE - Activate tractor beam [pull mode]", xMargin, yMargin + lineYOffset*7);//*** only available during non auto tractor
            g.drawString("R MOUSE - Activate tractor beam [repel mode]", xMargin, yMargin + lineYOffset*8);//*** only available during non auto tractor
            
            g.drawString("L/R ARROW - Toggle through weapons", xMargin, yMargin + lineYOffset*10);
            g.drawString("SPACE - Use selected weapon", xMargin, yMargin + lineYOffset*11);
            
            g.drawString("I - Open upgrade menu", xMargin, yMargin + lineYOffset*13);//*** brings to Inv
            
            
        }
        else if (infoState == 1) //Inventory
        {
            inventoryInfoManager.render(g);
            g.drawString("Pressing \"I\" toggles the display of upgrade options", xMargin, yMargin);
            
            g.drawString("The \"Inventory\" is used to upgrade your ship using materials:", xMargin, yMargin + lineYOffset*2);
            g.drawImage(Assets.carbon, xMargin, (int)(yMargin + lineYOffset*2.2), lineYOffset, lineYOffset, null);
            g.drawString("     - Carbon: found in all asteroids", xMargin, yMargin + lineYOffset*3);
            g.drawImage(Assets.silicon, xMargin, (int)(yMargin + lineYOffset*3.2), lineYOffset, lineYOffset, null);
            g.drawString("     - Silicon: found in asteroids with a purple hue", xMargin, yMargin + lineYOffset*4);
            g.drawImage(Assets.aluminum, xMargin, (int)(yMargin + lineYOffset*4.2), lineYOffset, lineYOffset, null);
            g.drawString("     - Aluminum: found in asteroids with a golden hue", xMargin, yMargin + lineYOffset*5);
            g.drawImage(Assets.plasma, xMargin, (int)(yMargin + lineYOffset*5.2), lineYOffset, lineYOffset, null);
            g.drawString("     - Plasma: found in Nebulas", xMargin, yMargin + lineYOffset*6);
            
            g.drawString("To upgrade your ship's individual components, you will first need upgrade to the", xMargin, yMargin + lineYOffset*8);
            g.drawString("advanced hull. This is achieved in the modifications tab using the listed materials.", xMargin, yMargin + lineYOffset*9);
            g.drawString("After you have upgraded to the advanced hull, you will be able to add modifactions", xMargin, yMargin + lineYOffset*10);
            g.drawString("to your ship, including: Ion Engines, Weapons, and scanners!", xMargin, yMargin + lineYOffset*11);
            
            g.drawString("Be sure to upgrade whenever possible, as this will make venturing space far easier!", xMargin, yMargin + lineYOffset*13);
        }
        else if (infoState == 2) // objectives
        {
            objectivesInfoManager.render(g);
            g.drawString("The underlying objective of the game is to make your way back to earth.", xMargin, yMargin);
            
            g.drawString("This task may seem daunting in the endless void of space, but with your trusty", xMargin, yMargin + lineYOffset*2);
            g.drawString("computer \"Hal,\" anything is possible!", xMargin, yMargin + lineYOffset*3);
            
            g.drawString("If you feel lost, it may be good idea to follow your scanner to the nearest astral body.", xMargin, yMargin + lineYOffset*5);
            
            g.drawString("Other objectives you should strive to achieve include:", xMargin, yMargin + lineYOffset*8);
            g.drawString("  - Finding all of the astral bodies", xMargin, yMargin + lineYOffset*9);
            g.drawString("  - Upgrading your ship with all of the available modules", xMargin, yMargin + lineYOffset*10); // See inventory
            g.drawString("  - Setting a high score overall, or in individual mini games", xMargin, yMargin + lineYOffset*11);// See scoring
        }
        else if (infoState == 3) //scoring points
        {
            scoringInfoManager.render(g);
            g.drawString("To score points, you may do the following:", xMargin, yMargin);
            g.drawString("  - [x1000] Discover Nebulas ", xMargin, yMargin + lineYOffset*1);
            g.drawString("  - [x500] Land on a newly discovered planet", xMargin, yMargin + lineYOffset*2); //button that leads to alt state
            g.drawString("  - [x50] Destroy enemies", xMargin, yMargin + lineYOffset*3);
            g.drawString("  - [x20] Destroy Asteroids", xMargin, yMargin + lineYOffset*4);
            g.drawString("  - [x5] Collect Materials", xMargin, yMargin + lineYOffset*5);
            
            g.drawString("You may also set high scores for individual Mini Games:", xMargin, yMargin + lineYOffset*7);
            g.drawString("  - Asteroids: When in an asteroid field, see how long you can go without being hit!", xMargin, yMargin + lineYOffset*8);
            g.drawString("  - Orbit: When near a planet, see how long you can orbit without using thrusters!", xMargin, yMargin + lineYOffset*9);
        }
        
    }
}
