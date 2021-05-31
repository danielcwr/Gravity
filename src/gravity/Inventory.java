package gravity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class Inventory 
{
    private Handler handler;
    private ArrayList<Item> inventoryItems;
    private UIManager specManagerMin;
    private UIManager specManager;
    private UIManager modManagerMin;
    private UIManager modManager;
    private final int screenWidth;
    private final int screenHeight;
    private final int invWidth = Assets.invBackground.getWidth();
    private final int invHeight = Assets.invBackground.getHeight();
    private final int invTopLeftX;
    private final int invTopLeftY;
    
    private final int specButtonWidth = Assets.specsButton[0].getWidth();
    private final int specButtonHeight = Assets.specsButton[0].getHeight();
    private final int upButtonWidth = Assets.upgradeButton[0].getWidth();
    private final int upButtonHeight = Assets.upgradeButton[0].getHeight();
    
    private int[] startUpgrade = new int[]{0, 1, 1, 0};
    
    private int maxHealth, nextMaxHealth; 
    private int[] mHUpgrade = new int[]{4,4,4,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA //4 4 4 0
    
    private int attackCooldown, nextAttackCooldown;
    private int[] aCUpgrade = new int[]{4,1,1,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA //4 1 1 0
    
    private int shotDMG, nextShotDMG;
    private int[] sDUpgrade = new int[]{1,0,0,4}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA /1 0 0 4
    
    private double maxSpeed, nextMaxSpeed;
    private int[] mSUpgrade = new int[]{4,2,2,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA 
    
    private float acceleration, nextAcceleration;
    private int[] aUpgrade = new int[]{2,1,0,2}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA 
    
    private double turningSpeed, nextTurningSpeed;
    private int[] tSUpgrade = new int[]{4,0,0,1}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA
    
    private double currentHealth, nextCurrentHealth;
    private int[] restoreHealth = new int[]{0,1,1,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA;
    
    private final int[] aHUpgrade = new int[]{5,5,5,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA | //5 10 10 0
    private final int[] aEUpgrade = new int[]{4,4,2,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA | //4 4 2 0
    private final int[] pBUpgrade = new int[]{4,2,0,4}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA |
    private final int[] aSUpgrade = new int[]{8,2,0,0}; //IN FORMAT: SILICON | CARBON | ALUMINUM | PLASMA |
    
    private boolean engineEquiped = false;
    private boolean blasterEquiped = false;
    private boolean sensorsEquiped = false;
    
    
    private boolean specHasBeenChanged = true;
    
    private boolean enoughSilicon;
    private boolean enoughCarbon;
    private boolean enoughAluminum;
    private boolean enoughPlasma;
    
    private int amountSilicon;
    private int amountCarbon;
    private int amountAluminum;
    private int amountPlasma;
    
    private final Font specFont = Assets.blockText.deriveFont(30f); //new Font("Dialog", Font.BOLD, 30);
    private final Font modMinFont = Assets.blockText.deriveFont(50f);//new Font("Dialog", Font.BOLD, 50);
    private final Font modFont = Assets.blockText.deriveFont(40f);//new Font("Dialog", Font.BOLD, 40);
    
    private int invState = 0;
    private boolean invStateChanged = true;
    private int shipLevel = 0; //0
    
    private boolean invBeenOpenedBefore = false;
    
    public Inventory(final Handler handler)
    {
        this.handler = handler;
        inventoryItems = new ArrayList<Item>();
        
        screenWidth = handler.getScreenWidth();
        screenHeight = handler.getScreenHeight();
        invTopLeftX = (screenWidth/2)-(invWidth/2);
        invTopLeftY = (screenHeight/2)-(invHeight/2);
        
        specManager();
        specManagerMin();
        modManager();
        modManagerMin();
        
        
    }
    
    public void tick()
    {
        if (handler.getKeyManager().inventory == false)
        {
            handler.getGameState().setUIManager_to_gameUIManager();
            return;
        }
        invBeenOpenedBefore = true;
        updateUIManager();
        
        if (specHasBeenChanged)
        {
            updateSpecs();
        }
        
        if (invState == 0)
        {
            if (shipLevel > 0)
            {
                specManager.tick();
            }
            else
                specManagerMin.tick();
        }
        if (invState == 1)
        {
            if (shipLevel > 0)
            {
                modManager.tick();
            }
            else
                modManagerMin.tick();
        }
        
        //Item Amount updates
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
        
        //CurrentHealth updates
        currentHealth = handler.getPlayer().getHealth();
        if (maxHealth - currentHealth < 20)
            nextCurrentHealth = maxHealth;
        else
            nextCurrentHealth = currentHealth + 20;
    }
    
    public void render(Graphics g)
    {
        if (handler.getKeyManager().inventory == false)
            return;
        
        ///INVENTORY GUI HERE
        g.drawImage(Assets.invBackground, invTopLeftX, invTopLeftY, null);
        
        
        if (invState == 0) //Specifications Tab Selected
        {
            
            if (shipLevel == 0)
            {
                specManagerMin.render(g);
                g.drawImage(Assets.upgradeText, invTopLeftX, invTopLeftY+50, null);
                return;
            }
            specManager.render(g);
            g.drawImage(Assets.specText, invTopLeftX, invTopLeftY, null);
            
            g.setFont(specFont);
            
            
            if (shipLevel >= 1)
            {
                renderHullSpecs(g);
            }
            
            if (blasterEquiped)
            {
                renderBlasterSpecs(g);
            }
            else
            {
                g.drawImage(Assets.equipBlastersText, invTopLeftX, invTopLeftY, null);
            }
                
            if (engineEquiped) 
            {
                renderEngineSpecs(g);
            }
            else
            {
                g.drawImage(Assets.equipEnginesText, invTopLeftX, invTopLeftY, null);
            }  
            
            //Restore HP text
            //CURRENT HP
            g.setColor(Color.DARK_GRAY);
            g.drawString(Integer.toString((int) currentHealth), (int) (invTopLeftX + (invWidth*.435)), (int) (invTopLeftY + (invHeight*.905)));

            //NEXT HP
            g.setColor(Color.orange);
            g.drawString(Integer.toString((int) nextCurrentHealth), (int) (invTopLeftX + (invWidth*.795)), (int) (invTopLeftY + (invHeight*.905)));

            //REQUIRED HP regen materials
            g.setColor(Color.red);
            if (amountCarbon >= restoreHealth[1])
                g.setColor(Color.green);
            g.drawString(Integer.toString(restoreHealth[1]), (int) (invTopLeftX + (invWidth*.575)), (int) (invTopLeftY + (invHeight*.905)));
            g.setColor(Color.red);
            if (amountAluminum >= restoreHealth[2])
                g.setColor(Color.green);
            g.drawString(Integer.toString(restoreHealth[2]), (int) (invTopLeftX + (invWidth*.655)), (int) (invTopLeftY + (invHeight*.905)));
        }
        if (invState == 1) //Mods Tab Selected
        {
            if (shipLevel == 0)
            {
                modManagerMin.render(g);
                g.drawImage(Assets.advancedHullText, invTopLeftX, invTopLeftY, null);
                
                g.setFont(modMinFont);
                g.setColor(Color.red);//REQUIRED HULL UPGRADE MATERIALS
                if (amountSilicon >= aHUpgrade[0])
                    g.setColor(Color.green);
                g.drawString(Integer.toString(aHUpgrade[0]), (int) (invTopLeftX + (invWidth*.336)), (int) (invTopLeftY + (invHeight*.48)));
                g.setColor(Color.red);
                if (amountCarbon >= aHUpgrade[1])
                    g.setColor(Color.green);
                g.drawString(Integer.toString(aHUpgrade[1]), (int) (invTopLeftX + (invWidth*.448)), (int) (invTopLeftY + (invHeight*.48)));
                g.setColor(Color.red);
                if (amountAluminum >= aHUpgrade[2])
                    g.setColor(Color.green);
                g.drawString(Integer.toString(aHUpgrade[2]), (int) (invTopLeftX + (invWidth*.564)), (int) (invTopLeftY + (invHeight*.48)));
            }
            else if (shipLevel > 0)
            {
                modManager.render(g);
                g.drawImage(Assets.modText, invTopLeftX, invTopLeftY, null);
                g.setFont(modFont);
                
                if (!engineEquiped)
                {
                    g.setColor(Color.red);//ENGINES UPGRADE
                    if (amountSilicon >= aEUpgrade[0])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(aEUpgrade[0]), (int) (invTopLeftX + (invWidth*.16)), (int) (invTopLeftY + (invHeight*.39)));
                    g.setColor(Color.red);
                    if (amountCarbon >= aEUpgrade[1])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(aEUpgrade[1]), (int) (invTopLeftX + (invWidth*.28)), (int) (invTopLeftY + (invHeight*.39)));
                    g.setColor(Color.red);
                    if (amountAluminum >= aEUpgrade[2])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(aEUpgrade[2]), (int) (invTopLeftX + (invWidth*.4)), (int) (invTopLeftY + (invHeight*.39)));
                }
                else
                    g.drawImage(Assets.equiped, (int) (invTopLeftX + (invWidth*.06)), (int) (invTopLeftY + (invHeight*.2)), null);
                
                if (!blasterEquiped)
                {
                    g.setColor(Color.red);//BLASTERS UPGRADE
                    if (amountSilicon >= pBUpgrade[0])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(pBUpgrade[0]), (int) (invTopLeftX + (invWidth*.16)), (int) (invTopLeftY + (invHeight*.635)));
                    g.setColor(Color.red);
                    if (amountCarbon >= pBUpgrade[1])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(pBUpgrade[1]), (int) (invTopLeftX + (invWidth*.28)), (int) (invTopLeftY + (invHeight*.635)));
                    g.setColor(Color.red);
                    if (amountPlasma >= pBUpgrade[3])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(pBUpgrade[3]), (int) (invTopLeftX + (invWidth*.4)), (int) (invTopLeftY + (invHeight*.635)));
                }
                else
                    g.drawImage(Assets.equiped, (int) (invTopLeftX + (invWidth*.06)), (int) (invTopLeftY + (invHeight*.45)), null);
                
                if (!sensorsEquiped)
                {
                    g.setColor(Color.red);//SENSORS UPGRADE
                    if (amountSilicon >= aSUpgrade[0])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(aSUpgrade[0]), (int) (invTopLeftX + (invWidth*.16)), (int) (invTopLeftY + (invHeight*.885)));
                    g.setColor(Color.red);
                    if (amountCarbon >= aSUpgrade[1])
                        g.setColor(Color.green);
                    g.drawString(Integer.toString(aSUpgrade[1]), (int) (invTopLeftX + (invWidth*.28)), (int) (invTopLeftY + (invHeight*.885)));
                }
                else
                    g.drawImage(Assets.equiped, (int) (invTopLeftX + (invWidth*.06)), (int) (invTopLeftY + (invHeight*.7)), null);
            }
        }
    }
    //Inventory Methods
    public void addItem(Item item)
    {
        for(Item i : inventoryItems)
        {
            if (i.getID() == item.getID())
            {
                i.setCount(i.getCount() + item.getCount());
                return;
            }
        }
        inventoryItems.add(item);
    }
    
    public void renderHullSpecs(Graphics g)
    {
        //CURRENT SPECS
        g.drawString(Integer.toString(maxHealth), (int) (invTopLeftX + (invWidth*.435)), (int) (invTopLeftY + (invHeight*.327)));

        //NEXT SPECS
        g.drawString(Integer.toString(nextMaxHealth), (int) (invTopLeftX + (invWidth*.795)), (int) (invTopLeftY + (invHeight*.327)));
        
        //REQUIRED MAXHEALTH MATERIALS
        g.setColor(Color.red);
        if (amountSilicon >= mHUpgrade[0])
            g.setColor(Color.green);
        g.drawString(Integer.toString(mHUpgrade[0]), (int) (invTopLeftX + (invWidth*.523)), (int) (invTopLeftY + (invHeight*.327)));
        g.setColor(Color.red);
        if (amountCarbon >= mHUpgrade[1])
            g.setColor(Color.green);
        g.drawString(Integer.toString(mHUpgrade[1]), (int) (invTopLeftX + (invWidth*.604)), (int) (invTopLeftY + (invHeight*.327)));
        g.setColor(Color.red);
        if (amountAluminum >= mHUpgrade[2])
            g.setColor(Color.green);
        g.drawString(Integer.toString(mHUpgrade[2]), (int) (invTopLeftX + (invWidth*.685)), (int) (invTopLeftY + (invHeight*.327)));
    }
    
    public void renderBlasterSpecs(Graphics g)
    {
        //Current
        g.drawString(Integer.toString(attackCooldown), (int) (invTopLeftX + (invWidth*.425)), (int) (invTopLeftY + (invHeight*.455)));
        g.drawString(Integer.toString(shotDMG), (int) (invTopLeftX + (invWidth*.442)), (int) (invTopLeftY + (invHeight*.519)));
        
        //Next
        g.drawString(Integer.toString(nextAttackCooldown), (int) (invTopLeftX + (invWidth*.795)), (int) (invTopLeftY + (invHeight*.455)));
        g.drawString(Integer.toString(nextShotDMG), (int) (invTopLeftX + (invWidth*.802)), (int) (invTopLeftY + (invHeight*.519)));
        
        //REQUIRED ATTACKCOOLDOWN MATERIALS
        g.setColor(Color.red);
        if (amountSilicon >= aCUpgrade[0])
            g.setColor(Color.green);
        g.drawString(Integer.toString(aCUpgrade[0]), (int) (invTopLeftX + (invWidth*.523)), (int) (invTopLeftY + (invHeight*.455)));
        g.setColor(Color.red);
        if (amountCarbon >= aCUpgrade[1])
            g.setColor(Color.green);
        g.drawString(Integer.toString(aCUpgrade[1]), (int) (invTopLeftX + (invWidth*.604)), (int) (invTopLeftY + (invHeight*.455)));
        g.setColor(Color.red);
        if (amountAluminum >= aCUpgrade[2])
            g.setColor(Color.green);
        g.drawString(Integer.toString(aCUpgrade[2]), (int) (invTopLeftX + (invWidth*.685)), (int) (invTopLeftY + (invHeight*.455)));

        g.setColor(Color.red);//REQUIRED SHOTDMG MATERIALS
        if (amountSilicon >= sDUpgrade[0])
            g.setColor(Color.green);
        g.drawString(Integer.toString(sDUpgrade[0]), (int) (invTopLeftX + (invWidth*.563)), (int) (invTopLeftY + (invHeight*.519)));
        g.setColor(Color.red);
        if (amountPlasma >= sDUpgrade[3])
            g.setColor(Color.green);
        g.drawString(Integer.toString(sDUpgrade[3]), (int) (invTopLeftX + (invWidth*.645)), (int) (invTopLeftY + (invHeight*.519)));
            
    }
    
    public void renderEngineSpecs(Graphics g)
    {
        //Current
        g.drawString(Integer.toString((int)(maxSpeed*100)), (int) (invTopLeftX + (invWidth*.435)), (int) (invTopLeftY + (invHeight*.647)));
        g.drawString(Integer.toString((int)(acceleration*1000)), (int) (invTopLeftX + (invWidth*.442)), (int) (invTopLeftY + (invHeight*.71)));
        g.drawString(Integer.toString((int)(turningSpeed*10)), (int) (invTopLeftX + (invWidth*.45)), (int) (invTopLeftY + (invHeight*.773)));
            
        //Next
        g.drawString(Integer.toString((int)(nextMaxSpeed*100)), (int) (invTopLeftX + (invWidth*.795)), (int) (invTopLeftY + (invHeight*.647)));
        g.drawString(Integer.toString((int)(nextAcceleration*1000)), (int) (invTopLeftX + (invWidth*.802)), (int) (invTopLeftY + (invHeight*.71)));
        g.drawString(Integer.toString((int)(nextTurningSpeed*10)), (int) (invTopLeftX + (invWidth*.81)), (int) (invTopLeftY + (invHeight*.773)));
        
        //REQUIRED MAXSPEED MATERIALS
        g.setColor(Color.red);
        if (amountSilicon >= mSUpgrade[0])
            g.setColor(Color.green);
        g.drawString(Integer.toString(mSUpgrade[0]), (int) (invTopLeftX + (invWidth*.523)), (int) (invTopLeftY + (invHeight*.647)));
        g.setColor(Color.red);
        if (amountCarbon >= mSUpgrade[1])
            g.setColor(Color.green);
        g.drawString(Integer.toString(mSUpgrade[1]), (int) (invTopLeftX + (invWidth*.604)), (int) (invTopLeftY + (invHeight*.647)));
        g.setColor(Color.red);
        if (amountAluminum >= mSUpgrade[2])
            g.setColor(Color.green);
        g.drawString(Integer.toString(mSUpgrade[2]), (int) (invTopLeftX + (invWidth*.685)), (int) (invTopLeftY + (invHeight*.647)));

        g.setColor(Color.red);//REQUIRED ACCELERATION MATERIALS
        if (amountSilicon >= aUpgrade[0])
            g.setColor(Color.green);
        g.drawString(Integer.toString(aUpgrade[0]), (int) (invTopLeftX + (invWidth*.523)), (int) (invTopLeftY + (invHeight*.71)));
        g.setColor(Color.red);
        if (amountCarbon >= aUpgrade[1])
            g.setColor(Color.green);
        g.drawString(Integer.toString(aUpgrade[1]), (int) (invTopLeftX + (invWidth*.604)), (int) (invTopLeftY + (invHeight*.71)));
        g.setColor(Color.red);
        if (amountPlasma >= aUpgrade[3])
            g.setColor(Color.green);
        g.drawString(Integer.toString(aUpgrade[3]), (int) (invTopLeftX + (invWidth*.685)), (int) (invTopLeftY + (invHeight*.71)));

        g.setColor(Color.red);//REQUIRED TURNINGSPEED MATERIALS
        if (amountSilicon >= tSUpgrade[0])
            g.setColor(Color.green);
        g.drawString(Integer.toString(tSUpgrade[0]), (int) (invTopLeftX + (invWidth*.563)), (int) (invTopLeftY + (invHeight*.773)));
        g.setColor(Color.red);
        if (amountPlasma >= tSUpgrade[3])
            g.setColor(Color.green);
        g.drawString(Integer.toString(tSUpgrade[3]), (int) (invTopLeftX + (invWidth*.645)), (int) (invTopLeftY + (invHeight*.773)));
    }
    
    public void updateSpecs()
    {
        maxHealth = handler.getPlayer().getMAX_HEALTH(); //100
        attackCooldown = handler.getPlayer().getATTACK_COOLDOWN();//1000
        shotDMG = handler.getPlayer().getSHOT_DMG();//10
        maxSpeed = handler.getPlayer().getMAX_SPEED();//4
        acceleration = handler.getPlayer().getMAX_ACCELERATION();//.02
        turningSpeed = handler.getPlayer().getTURNING_SPEED();//.8
        
        nextMaxHealth = maxHealth + 20;
        nextAttackCooldown = attackCooldown - 100;
        nextShotDMG = shotDMG + 2;
        nextMaxSpeed = maxSpeed + .5;
        nextAcceleration = acceleration + .005f;
        nextTurningSpeed = turningSpeed + .1;
        
        specHasBeenChanged = false;
    }
    
    public void specManagerMin()
    {
        specManagerMin = new UIManager(handler);
        
        //Speccifications Button
        specManagerMin.addObject(new UIImageButton(handler, invTopLeftX, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.specsButton, new ClickListener()
        {
            @Override
            public void onClick() {}
        }, false));
        //Modifications Button
        specManagerMin.addObject(new UIImageButton(handler, invTopLeftX+specButtonWidth, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.modsButton, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                invState = 1;
                invStateChanged = true;
            }
        }, false));
        //HEAL BUTTON
        specManagerMin.addObject(new HealButton(handler, (int) (invTopLeftX + (invWidth*.5)-upButtonWidth), (int) (invTopLeftY + (invHeight*.25)),//.325 SO MINUS .04
                                               upButtonWidth*2, upButtonHeight*2, "startUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(startUpgrade) && handler.getPlayer().health != maxHealth)//&& handler.getInGameIntro())
                {
                    handler.getPlayer().setHealth(maxHealth);
                    removeMaterials(startUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },startUpgrade));
        
    }
    
    public void specManager()
    {
        specManager = new UIManager(handler);
        
        //Speccifications Button
        specManager.addObject(new UIImageButton(handler, invTopLeftX, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.specsButton, new ClickListener()
        {
            @Override
            public void onClick() {}
        }, false));
        //Modifications Button
        specManager.addObject(new UIImageButton(handler, invTopLeftX+specButtonWidth, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.modsButton, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                invState = 1;
                invStateChanged = true;
            }
        }, false));
        
        //Upgrade MAX_HEALTH Button
        specManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.287)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "mHUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(mHUpgrade))
                {
                    handler.getPlayer().setMAX_HEALTH(nextMaxHealth);
                    handler.getPlayer().setHealth((int)handler.getPlayer().getHealth()+20);
                    removeMaterials(mHUpgrade);
                    mHUpgrade = changeMaterials(mHUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },mHUpgrade));
        
        //Upgrade ATTACK_COOLDOWN Button
        specManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.415)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "aCUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(aCUpgrade))
                {
                    handler.getPlayer().setATTACK_COOLDOWN(nextAttackCooldown);
                    removeMaterials(aCUpgrade);
                    aCUpgrade = changeMaterials(aCUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },aCUpgrade));
        
        //Upgrade SHOT_DMG Button
        specManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.479)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "sDUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(sDUpgrade))
                {
                    handler.getPlayer().setSHOT_DMG(nextShotDMG);
                    removeMaterials(sDUpgrade);
                    sDUpgrade = changeMaterials(sDUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },sDUpgrade));
        
        //Upgrade MAX_SPEED Button
        specManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.608)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "mSUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(mSUpgrade))
                {
                    handler.getPlayer().setMAX_SPEED(nextMaxSpeed);
                    removeMaterials(mSUpgrade);
                    mSUpgrade = changeMaterials(mSUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },mSUpgrade));
        
        //Upgrade ACCELERATION Button
        specManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.67)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "aUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(aUpgrade))
                {
                    handler.getPlayer().setMAX_ACCELERATION(nextAcceleration);
                    removeMaterials(aUpgrade);
                    aUpgrade = changeMaterials(aUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },aUpgrade));
        
        //Upgrade TURNING_SPEED Button
        specManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.732)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "tSUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(tSUpgrade))
                {
                    handler.getPlayer().setMAX_ACCELERATION(nextAcceleration);
                    removeMaterials(tSUpgrade);
                    tSUpgrade = changeMaterials(tSUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },tSUpgrade));
        
        //Upgrade TURNING_SPEED Button
        specManager.addObject(new HealButton(handler, (int) (invTopLeftX + (invWidth*.875)), (int) (invTopLeftY + (invHeight*.865)),//.325 SO MINUS .04
                                               upButtonWidth, upButtonHeight, "restoreHealth", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(restoreHealth) && currentHealth < maxHealth)
                {
                    handler.getPlayer().setHealth((int) nextCurrentHealth);
                    removeMaterials(restoreHealth);
                    specHasBeenChanged = true;
                }
            }
        },restoreHealth));
    }
    
    public void modManager()
    {
        modManager = new UIManager(handler);
        
        modManager.addObject(new UIImageButton(handler, invTopLeftX, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.specsButton, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                invState = 0;
                invStateChanged = true;
            }
        }, false));
        
        modManager.addObject(new UIImageButton(handler, invTopLeftX+specButtonWidth, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.modsButton, new ClickListener()
        {
            @Override
            public void onClick() {}
        }, false));
        
        //Upgrade ENGINES Button
        modManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.63)), (int) (invTopLeftY + (invHeight*.25)),
                                               upButtonWidth*3, upButtonHeight*3, "aEUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (engineEquiped)
                    return;
                if (checkMaterials(aEUpgrade))
                {
                    if (blasterEquiped)
                    {
                        handler.getPlayer().shipLevel4();
                        shipLevel = 4;
                    }
                    else
                    {
                        handler.getPlayer().shipLevel2();
                        shipLevel = 2;
                    }
                    engineEquiped = true;
                    removeMaterials(aEUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },aEUpgrade));
        
        //Upgrade BLASTERS Button
        modManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.63)), (int) (invTopLeftY + (invHeight*.5)),
                                               upButtonWidth*3, upButtonHeight*3, "pBUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (blasterEquiped)
                    return;
                if (checkMaterials(pBUpgrade))
                {
                    if (engineEquiped)
                    {
                        handler.getPlayer().shipLevel4();
                        shipLevel = 4;
                    }
                    else
                    {
                        handler.getPlayer().shipLevel3();
                        shipLevel = 3;
                    }
                    blasterEquiped = true;
                    removeMaterials(pBUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },pBUpgrade));
        
        //Upgrade SENSORS Button
        modManager.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.63)), (int) (invTopLeftY + (invHeight*.75)),
                                               upButtonWidth*3, upButtonHeight*3, "aSUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (sensorsEquiped)
                    return;
                if (checkMaterials(aSUpgrade))
                {
                    sensorsEquiped = true;
                    removeMaterials(aSUpgrade);
                    specHasBeenChanged = true;
                }
            }
        },aSUpgrade));
        
    }
    
        public void modManagerMin()
    {
        modManagerMin = new UIManager(handler);
        
        modManagerMin.addObject(new UIImageButton(handler, invTopLeftX, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.specsButton, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                invState = 0;
                invStateChanged = true;
            }
        }, false));
        
        modManagerMin.addObject(new UIImageButton(handler, invTopLeftX+specButtonWidth, invTopLeftY,
                                               specButtonWidth, specButtonHeight, Assets.modsButton, new ClickListener()
        {
            @Override
            public void onClick() {}
        }, false));
        
        //Upgrade TURNING_SPEED Button
        modManagerMin.addObject(new UpgradeButton(handler, (int) (invTopLeftX + (invWidth*.375)), (int) (invTopLeftY + (invHeight*.52)),
                                               upButtonWidth*3, upButtonHeight*3, "aHUpgrade", new ClickListener()
        {
            @Override
            public void onClick() 
            {
                if (checkMaterials(aHUpgrade))
                {
                    handler.getPlayer().shipLevel1();
                    removeMaterials(aHUpgrade);
                    shipLevel = 1;
                    specHasBeenChanged = true;
                    invStateChanged = true;
                }
            }
        },aHUpgrade));
        
        
    }
    
    public void updateUIManager()
    {
        if (invState == 0)
        {
            if (shipLevel > 0)
            {
                handler.getMouseManager().setUIManager(new UIManager[]{specManager});
            }
            else
                handler.getMouseManager().setUIManager(new UIManager[]{specManagerMin});
        }
        if (invState == 1)
        {
            if (shipLevel > 0)
            {
                handler.getMouseManager().setUIManager(new UIManager[]{modManager});
            }
            else
                handler.getMouseManager().setUIManager(new UIManager[]{modManagerMin});

        }
        invStateChanged = false;
    }
    
    public boolean checkMaterials(int[] array)
    {
        resetBooleans();
        if (inventoryItems.isEmpty())
        {
            return false;
        }
        if (amountSilicon >= array[0] || array[0] == 0)//Silicon
        {
            enoughSilicon = true;
        }
        if (amountCarbon >= array[1] || array[1] == 0)//Carbon
        {
            enoughCarbon = true;
        }
        if (amountAluminum >= array[2] || array[2] == 0)//Aluminum
        {
            enoughAluminum = true;
        }
        if (amountPlasma >= array[3] || array[3] == 0)//Plasma
        {
            enoughPlasma = true;
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
    
    public void removeMaterials(int[] array)
    {
        for (Item i : inventoryItems)
        {
            if (i.getID() == 0)//Silicon
            {
                //System.out.println(i.getCount());
                i.setCount(i.getCount()-array[0]);
                //System.out.println(i.getCount());
            }
            if (i.getID() == 1)//Carbon
            {
                i.setCount(i.getCount()-array[1]);
            }
            if (i.getID() == 2)//Aluminum
            {
                i.setCount(i.getCount()-array[2]);
            }
            if (i.getID() == 3)//Plasma
            {
                i.setCount(i.getCount()-array[3]);
            }
        }
    }
    
    public int[] changeMaterials(int[] array)
    {
        for (int x = 0; x < array.length; x++)
        {
            int newValue = array[x]*2;
            array[x] = newValue;
        }
        return array;
    }

    //Getter and setters
    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    
    public int getInvState()
    {
        return invState;
    }

    public int[] getmHUpgrade() {
        return mHUpgrade;
    }

    public int[] getaCUpgrade() {
        return aCUpgrade;
    }

    public int[] getsDUpgrade() {
        return sDUpgrade;
    }

    public int[] getmSUpgrade() {
        return mSUpgrade;
    }

    public int[] getaUpgrade() {
        return aUpgrade;
    }

    public int[] gettSUpgrade() {
        return tSUpgrade;
    }

    public int[] getaHUpgrade() {
        return aHUpgrade;
    }
    
    public ArrayList<Item> getInventoryItems() {
        return inventoryItems;
    }
    
    public boolean getInvBeenOpenedBefore()
    {
        return invBeenOpenedBefore;
    }
    
    public boolean getEnginesEquiped()
    {
        return engineEquiped;
    }
    
    public boolean getBlastersEquiped()
    {
        return blasterEquiped;
    }
    
    public boolean getSensorsEquiped()
    {
        return sensorsEquiped;
    }
    
}
