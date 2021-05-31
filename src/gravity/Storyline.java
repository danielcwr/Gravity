package gravity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class Storyline 
{
    private ArrayList<String> storyTextTitle;
    private ArrayList<String> storyTextBody;
    private TypeText assesingDamageText, intro6Text, intro7Text, intro8Text, toBeContText;
    private TypeText respawnText, planetText, siliconText, carbonText, aluminumText, plasmaText, asteroidText, nebulaText, blackholeText; //respawn = .get(4)
    private TypeText respawnTitle, planetTitle, siliconTitle, carbonTitle, aluminumTitle, plasmaTitle, asteroidTitle, nebulaTitle, blackholeTitle;
    
    private Handler handler;
    
    private final double scaleWidth;
    private final double scaleHeight;
    
    private final int screenWidth;
    private final int screenHeight;
    
    private final int speechHeight = 350;
    private final int speechWidth = 700;
    //Storyline booleans
    private Object[] seenAsteroid = {false, null};
    private Object[] seenPlanet = {false, null};
    private Object[] seenNebula = {false, null};
    private Object[] seenBlackHole = {false, null};
    private Object[] seenWormhole = {false, null};
    
    private ArrayList<Item> inventoryItems;
    private boolean seenCarbon = false;
    private boolean seenSilicon = false;
    private boolean seenAluminum = false;
    private boolean seenPlasma = false;
    private int amountSilicon = 0;
    private int amountCarbon = 0;
    private int amountAluminum = 0;
    private int amountPlasma = 0;
    private int cSilicon = 0;
    private int cCarbon = 0;
    private int cAluminum = 0;
    private int cPlasma = 0;
    
    private boolean readAsteroid = false;
    private boolean readPlanet = false;
    private boolean readNebula = false;
    private boolean readBlackHole = false;
    private boolean readWormhole = false;
    private boolean readCarbon = false;
    private boolean readSilicon = false;
    private boolean readAluminum = false;
    private boolean readPlasma = false;
    
    //So it doesnt update so frequently
    private long lastTick;
    private long newPopUpQuery;
    private long lastPopUp;
    private long lastHalRefresh;
    
    private boolean readingRespawnText = false;
    private boolean readingAsteroid = false;
    private boolean readingPlanet = false;
    private boolean readingNebula = false;
    private boolean readingBlackHole = false;
    private boolean readingWormhole = false;
    private boolean readingCarbon = false;
    private boolean readingSilicon = false;
    private boolean readingAluminum = false;
    private boolean readingPlasma = false;
    
    private int currentInvIntro = 0;
    
    private boolean inOutro = false;
    private int currentOutroSlide = 0;
    private boolean inIntro = true;
    private boolean inGameIntro = false;
    private int currentIntro = 0;
    private int currentSlide = 0;
    private Animation outro4;
    
    private Animation hal;
    private Animation halLights;
    public boolean renderHal = false;
    
    private int textX;
    private int textY;
    private int textWidth;
    private int textHeight;
    
    private long gameTimer;
    private long gameTimeLimit = 840000; //840000
    private boolean gameHasTimeLimit = true;
    
    private final Font titleFont;
    private final Font halTitleFont;
    private final Font halFont;
    
    private int infoArrowSize;
    private double infoArrowOffset = 0;
    
    private JTextArea halTextArea, halTitleArea;
    
    //Opactity for infoArrows
    private BufferedImage infoArrow;
    private double opacity = .5;
    private double opacityChange = .01;
    
    public Storyline(Handler handler)
    {
        this.handler = handler;
        
        scaleWidth = handler.getScaleWidth();
        scaleHeight = handler.getScaleHeight();
        
        screenWidth = handler.getScreenWidth();
        screenHeight = handler.getScreenHeight();
        
        textX = 30;
        textY = 600;
        textWidth = speechWidth;
        textHeight = speechHeight - 100;
        
        this.lastTick = System.currentTimeMillis();
        this.lastPopUp = System.currentTimeMillis();
        this.lastHalRefresh = System.currentTimeMillis();
        
        storyTextTitle = Assets.storyTextTitle;
        storyTextBody = Assets.storyTextBody;
        
        assesingDamageText = new TypeText(storyTextTitle.get(0));
        
        intro6Text = new TypeText(storyTextTitle.get(1));
        intro7Text = new TypeText(storyTextTitle.get(2));
        intro8Text = new TypeText(storyTextTitle.get(3));
        toBeContText = new TypeText(storyTextTitle.get(storyTextTitle.size()-1));
        
        respawnTitle = new TypeText(storyTextTitle.get(4));
        respawnText = new TypeText(storyTextBody.get(4));
        planetTitle = new TypeText(storyTextTitle.get(5));
        planetText = new TypeText(storyTextBody.get(5)); 
        /*
        siliconTitle = new TypeText(storyTextTitle.get(6));
        siliconText = new TypeText(storyTextBody.get(6));
        carbonTitle = new TypeText(storyTextTitle.get(7));
        carbonText = new TypeText(storyTextBody.get(7));
        aluminumTitle = new TypeText(storyTextTitle.get(8));
        aluminumText = new TypeText(storyTextBody.get(8));
        plasmaTitle = new TypeText(storyTextTitle.get(9));
        plasmaText = new TypeText(storyTextBody.get(9));
        */
        asteroidTitle = new TypeText(storyTextTitle.get(10));
        asteroidText = new TypeText(storyTextBody.get(10));
        nebulaTitle = new TypeText(storyTextTitle.get(11));
        nebulaText = new TypeText(storyTextBody.get(11));
        blackholeTitle = new TypeText(storyTextTitle.get(12));
        blackholeText = new TypeText(storyTextBody.get(12));
        
        halLights = new Animation(200, Assets.halsLights);
        hal = new Animation(200, Assets.hal, 1, true);
        
        titleFont = Assets.blockText.deriveFont(150f * (float)scaleWidth);
        halTitleFont = Assets.blockText.deriveFont(40f * (float)scaleWidth);
        halFont = Assets.blockText.deriveFont(30f * (float)scaleWidth);
        
        Insets halTextMargin = new Insets(screenHeight-speechHeight+140,textX,textX,textX); //top, left, bottom, right
        Insets halTitleMargin = new Insets(screenHeight-speechHeight+60,textX+100,textX,textX); //top, left, bottom, right
        
        halTextArea = new JTextArea();
        halTextArea.setSize(textWidth, screenHeight);
        halTextArea.setLineWrap(true);
        halTextArea.setWrapStyleWord(true);
        halTextArea.setMargin(halTextMargin);
        halTextArea.setFont(halFont);
        halTextArea.setForeground(Color.DARK_GRAY); //.Yellow looks ok
        halTextArea.setBackground(new Color(0,0,0,0));
        
        halTitleArea = new JTextArea();
        halTitleArea.setSize(textWidth, screenHeight);
        halTitleArea.setLineWrap(true);
        halTitleArea.setWrapStyleWord(true);
        halTitleArea.setMargin(halTitleMargin);
        halTitleArea.setFont(halTitleFont);
        halTitleArea.setForeground(Color.DARK_GRAY); //.Yellow looks ok
        halTitleArea.setBackground(new Color(0,0,0,0));
        //textArea.setLocation(textX, textY);
        
        infoArrowSize = (int)(32*scaleWidth);
        
        gameTimer = System.currentTimeMillis();
        
        infoArrow = Assets.infoArrow;
    }
    
    public void tick()
    {
        inIntro = handler.getInIntro();
        inGameIntro = handler.getInGameIntro();
        inOutro = handler.getInOutro();
        if (inIntro)
        {
            if (currentIntro == 4)
            {
                assesingDamageText.tickLong();
            }
        }
        if (inOutro)
        {
            if (currentOutroSlide == 3)
            {
                toBeContText.tickLong();
            }
        }
        
        if (System.currentTimeMillis() - lastTick > 1000)
        {
            inventoryItems = handler.getWorld().getInventory().getInventoryItems();
            if (inGameIntro)
            {
                for(Item i : inventoryItems)
                {
                    if(i.getID() == 0)
                    {
                        amountSilicon = i.getCount();
                    }
                                
                    if(i.getID() == 1)
                    {
                        amountCarbon = i.getCount();
                    }
                    
                    if(i.getID() == 2)
                    {
                        amountAluminum = i.getCount();
                    }
                    if(i.getID() == 3)
                    {
                        amountPlasma = i.getCount();
                    }
                }
            }
            if (!inIntro && !inGameIntro)
            {
                for(Item i : inventoryItems)
                {
                    if(i.getID() == 0 && !seenSilicon)
                    {
                        amountSilicon = i.getCount();
                        if (amountSilicon > cSilicon && !readingCarbon && !readingAluminum && !readingPlasma)
                            seenSilicon = true;
                        
                            siliconTitle = new TypeText(storyTextTitle.get(6));
                            siliconText = new TypeText(storyTextBody.get(6));
                            
                    }
                                
                    if(i.getID() == 1 && !seenCarbon)
                    {
                        amountCarbon = i.getCount();
                        if (amountCarbon > cCarbon && !readingSilicon && !readingAluminum && !readingPlasma)
                            seenCarbon = true;
                        
                            carbonTitle = new TypeText(storyTextTitle.get(7));
                            carbonText = new TypeText(storyTextBody.get(7));
                        
                    }
                    
                    if(i.getID() == 2 && !seenAluminum)
                    {
                        amountAluminum = i.getCount();
                        if (amountAluminum > cAluminum && !readingCarbon && !readingSilicon && !readingPlasma)
                            seenAluminum = true;

                            aluminumTitle = new TypeText(storyTextTitle.get(8));
                            aluminumText = new TypeText(storyTextBody.get(8));
                            
                    }
                    
                    if(i.getID() == 3 && !seenPlasma)
                    {
                        amountPlasma = i.getCount();
                        if (amountPlasma > cPlasma && !readingCarbon && !readingAluminum && !readingSilicon)
                            seenPlasma = true;
                        
                            plasmaTitle = new TypeText(storyTextTitle.get(9));
                            plasmaText = new TypeText(storyTextBody.get(9));
                    }
                }
                
                if (!(boolean)seenAsteroid[0])
                    seenAsteroid = handler.getPlayer().isNearAsteroid();
                if (!(boolean)seenPlanet[0])
                    seenPlanet = handler.getPlayer().isNearPlanet();
                if (!(boolean)seenNebula[0])
                    seenNebula = handler.getPlayer().isNearNebula();
                if (!(boolean)seenBlackHole[0])
                    seenBlackHole = handler.getPlayer().isNearBlackHole();
                if (!(boolean)seenWormhole[0])
                    seenWormhole = handler.getPlayer().isNearWormhole();
                if (!(boolean)inOutro)
                    handler.setInOutro(handler.getPlayer().isNearEarth());
            }
            lastTick = System.currentTimeMillis();
            
            if (System.currentTimeMillis() - gameTimer > gameTimeLimit && gameHasTimeLimit)
            {
                handler.setInOutro(true);
            }
        }
        tickOpacity();
        refreshHal();
        halLights.tick();
        hal.tick();
    }
    
    public void render(Graphics g)
    {
        if (handler.getPlayer().isPlayerDead())
        {
            return;
        }
        
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .85f));
        
        newPopUpQuery = System.currentTimeMillis();
        if (inIntro || inGameIntro || inOutro)
        {
            if (inIntro || inGameIntro)
            {
                renderIntro(g);
            }
            if (inOutro)
            {
                renderOutro(g);
            }
        }
        else
        {
            renderHal = false;
            if (readingRespawnText || handler.getPlayer().getRespawnText()) //RESPAWN POP UP
            {
                readingRespawnText = true;
                renderHal = true;
                
                g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                respawnTitle.tick();
                halTitleArea.setText(respawnTitle.getCurrentFrame());
                halTitleArea.paint(g2d);
                if (respawnTitle.getCurrentFrame().equals(respawnTitle.getFullString()))
                {
                    respawnText.tick();
                    halTextArea.setText(respawnText.getCurrentFrame());
                    halTextArea.paint(g2d);
                }
            
                if (handler.getKeyManager().readText)
                {
                    lastPopUp = System.currentTimeMillis();
                    handler.getPlayer().setRespawnText(false);
                    readingRespawnText = false;
                    renderHal = false;
                    
                }
            }
            else
            {   //Planet Info popup
                if (readingPlanet || ((boolean)seenPlanet[0] && !readPlanet && (newPopUpQuery - lastPopUp > 2000) && !readingAsteroid && !readingBlackHole && !readingNebula
                        && !readingSilicon && !readingCarbon && !readingAluminum && !readingPlasma))
                {
                    readingPlanet = true;
                    renderHal = true;
                    
                    renderInfoArrowsAroundObject(g, (Entity)seenPlanet[1]); //<-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    planetTitle.tick();
                    halTitleArea.setText(planetTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (planetTitle.getCurrentFrame().equals(planetTitle.getFullString()))
                    {
                        planetText.tick();
                        halTextArea.setText(planetText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }

                    if (handler.getKeyManager().readText)
                    {
                        readPlanet = true;
                        lastPopUp = System.currentTimeMillis();
                        readingPlanet = false;
                        renderHal = false;
                        
                        planetTitle = null;
                        planetText = null;
                    }
                }//Silicon info popup
                else if (readingSilicon || (seenSilicon && !readSilicon && (newPopUpQuery - lastPopUp > 2000) && !readingBlackHole && !readingPlanet && !readingNebula && !readingAsteroid
                        && !readingCarbon && !readingAluminum && !readingPlasma))
                {
                    readingSilicon = true;
                    renderHal = true;
                    
                    renderStaticInfoArrow(g, .9, .3, 180);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    siliconTitle.tick();
                    halTitleArea.setText(siliconTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (siliconTitle.getCurrentFrame().equals(siliconTitle.getFullString()))
                    {
                        siliconText.tick();
                        halTextArea.setText(siliconText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readSilicon = true;
                        lastPopUp = System.currentTimeMillis();
                        readingSilicon = false;
                        renderHal = false;
                        setCompareItems();
                        
                        siliconTitle = null;
                        siliconText = null;
                    }
                }//Carbon info popup
                else if (readingCarbon || (seenCarbon&& !readCarbon && (newPopUpQuery - lastPopUp > 2000) && !readingBlackHole && !readingPlanet && !readingNebula && !readingAsteroid
                        && !readingSilicon && !readingAluminum && !readingPlasma))
                {
                    readingCarbon = true;
                    renderHal = true;
                    
                    renderStaticInfoArrow(g, .9, .4, 180);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    carbonTitle.tick();
                    halTitleArea.setText(carbonTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (carbonTitle.getCurrentFrame().equals(carbonTitle.getFullString()))
                    {
                        carbonText.tick();
                        halTextArea.setText(carbonText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readCarbon = true;
                        lastPopUp = System.currentTimeMillis();
                        readingCarbon = false;
                        renderHal = false;
                        setCompareItems();
                        
                        carbonTitle = null;
                        carbonText = null;
                    }
                }//aluminum info popup
                else if (readingAluminum || (seenAluminum&& !readAluminum && (newPopUpQuery - lastPopUp > 2000) && !readingBlackHole && !readingPlanet && !readingNebula && !readingAsteroid
                        && !readingCarbon && !readingSilicon && !readingPlasma))
                {
                    readingAluminum = true;
                    renderHal = true;
                    
                    renderStaticInfoArrow(g, .9, .5, 180);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    aluminumTitle.tick();
                    halTitleArea.setText(aluminumTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (aluminumTitle.getCurrentFrame().equals(aluminumTitle.getFullString()))
                    {
                        aluminumText.tick();
                        halTextArea.setText(aluminumText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readAluminum = true;
                        lastPopUp = System.currentTimeMillis();
                        readingAluminum = false;
                        renderHal = false;
                        setCompareItems();
                        
                        aluminumTitle = null;
                        aluminumText = null;
                    }
                }//plasma info popup
                else if (readingPlasma || (seenPlasma&& !readPlasma && (newPopUpQuery - lastPopUp > 2000) && !readingBlackHole && !readingPlanet && !readingNebula && !readingAsteroid
                        && !readingCarbon && !readingAluminum && !readingSilicon))
                {
                    readingPlasma = true;
                    renderHal = true;
                    
                    renderStaticInfoArrow(g, .9, .6, 180);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    plasmaTitle.tick();
                    halTitleArea.setText(plasmaTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (plasmaTitle.getCurrentFrame().equals(plasmaTitle.getFullString()))
                    {
                        plasmaText.tick();
                        halTextArea.setText(plasmaText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readPlasma = true;
                        lastPopUp = System.currentTimeMillis();
                        readingPlasma = false;
                        renderHal = false;
                        setCompareItems();
                        
                        plasmaTitle = null;
                        plasmaText = null;
                    }
                }//asteroid info popup
                else if (readingAsteroid || ((boolean)seenAsteroid[0] && !readAsteroid && (newPopUpQuery - lastPopUp > 2000) && !readingBlackHole && !readingPlanet && !readingNebula
                        && !readingSilicon && !readingCarbon && !readingAluminum && !readingPlasma))
                {
                    readingAsteroid = true;
                    renderHal = true;
                    
                    renderInfoArrowsAroundObject(g, (Entity)seenAsteroid[1]);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    asteroidTitle.tick();
                    halTitleArea.setText(asteroidTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (asteroidTitle.getCurrentFrame().equals(asteroidTitle.getFullString()))
                    {
                        asteroidText.tick();
                        halTextArea.setText(asteroidText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readAsteroid = true;
                        lastPopUp = System.currentTimeMillis();
                        readingAsteroid = false;
                        renderHal = false;
                        
                        asteroidTitle = null;
                        asteroidText = null;
                    }
                }//nebula info popup
                else if (readingNebula || ((boolean)seenNebula[0] && !readNebula && (newPopUpQuery - lastPopUp > 2000) && !readingAsteroid && !readingPlanet && !readingBlackHole
                        && !readingSilicon && !readingCarbon && !readingAluminum && !readingPlasma))
                {
                    readingNebula = true;
                    renderHal = true;
                    
                    renderInfoArrowsAroundObject(g, (Entity)seenNebula[1]);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    nebulaTitle.tick();
                    halTitleArea.setText(nebulaTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (nebulaTitle.getCurrentFrame().equals(nebulaTitle.getFullString()))
                    {
                        nebulaText.tick();
                        halTextArea.setText(nebulaText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readNebula = true;
                        lastPopUp = System.currentTimeMillis();
                        readingNebula = false;
                        renderHal = false;
                        
                        nebulaTitle = null;
                        nebulaText = null;
                    }
                }//blackhole info popup
                else if (readingBlackHole || ((boolean)seenBlackHole[0] && !readBlackHole && (newPopUpQuery - lastPopUp > 2000) && !readingAsteroid && !readingPlanet && !readingNebula
                        && !readingSilicon && !readingCarbon && !readingAluminum && !readingPlasma))
                {
                    readingBlackHole = true;
                    renderHal = true;
                    
                    renderInfoArrowsAroundObject(g, (Entity)seenBlackHole[1]);
                    
                    g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
                    blackholeTitle.tick();
                    halTitleArea.setText(blackholeTitle.getCurrentFrame());
                    halTitleArea.paint(g2d);
                    if (blackholeTitle.getCurrentFrame().equals(blackholeTitle.getFullString()))
                    {
                        blackholeText.tick();
                        halTextArea.setText(blackholeText.getCurrentFrame());
                        halTextArea.paint(g2d);
                    }
                
                    if (handler.getKeyManager().readText)
                    {
                        readBlackHole = true;
                        lastPopUp = System.currentTimeMillis();
                        readingBlackHole = false;
                        renderHal = false;
                        
                        blackholeTitle = null;
                        blackholeText = null;
                    }
                }

            }
        }//Hall rendering code
        if (renderHal)
        {
            g2d.drawImage(hal.getCurrentFrame(), 25, screenHeight-speechHeight+25, null);
            g2d.drawImage(halLights.getCurrentFrame(), 26, screenHeight-speechHeight+110, null);
        }
        g2d.dispose();
    }
    
    public void renderIntro(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .85f));
        if (currentIntro == 0)
        {
            g.drawImage(Assets.slide1, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 500))
            {
                currentIntro++;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentIntro == 1)
        {
            g.drawImage(Assets.slide2, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 500))
            {
                currentIntro++;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentIntro == 2)
        {
            g.drawImage(Assets.slide3, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 500))
            {
                currentIntro++;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentIntro == 3)
        {
            g.drawImage(Assets.slide4, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 500))
            {
                currentIntro++;
                lastPopUp = System.currentTimeMillis();
                //ticking souind here
            }
        }
        else if (currentIntro == 4)
        {
            g.setFont(titleFont);
            g.setColor(Color.WHITE);
            g.drawImage(Assets.slide5, 0, 0, screenWidth, screenHeight, null);
            g.drawString(assesingDamageText.getCurrentFrame(), (int)(screenWidth*.15), screenHeight/2);
            //TYPING SOUND HERE
            if (assesingDamageText.getCurrentFrame().equals(storyTextTitle.get(0)))
            {
                currentIntro++;
                lastPopUp = System.currentTimeMillis();
            }
            //ticking souind here
        }
        else if (currentIntro == 5)
        {
            g.setFont(titleFont);
            g.setColor(Color.WHITE);
            g.drawImage(Assets.slide5, 0, 0, screenWidth, screenHeight, null);
            g.drawString(assesingDamageText.getCurrentFrame(), (int)(screenWidth*.15), screenHeight/2);
            if ((newPopUpQuery - lastPopUp > 2000)) //Delay from ASDMG to game GUI
            {
                handler.setInIntro(false);
                handler.setInGameIntro(true);
                currentIntro++;
                lastPopUp = System.currentTimeMillis();
                
                handler.getPlayer().setInvincible(true, 20000);
            }
        }
        else if (currentIntro == 6 && (newPopUpQuery - lastPopUp > 2000)) //Delay from Game GUI to Hal Popup
        {
            renderHal = true;
        
            g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
            intro6Text.tick();
            halTextArea.setText(intro6Text.getCurrentFrame());
            halTextArea.paint(g2d);
            
            if (handler.getKeyManager().readText)
            {
                currentIntro++;
                renderHal = false;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentIntro == 7 && amountCarbon > 0 && amountAluminum > 0 && (newPopUpQuery - lastPopUp > 2000))
        {
            renderHal = true;
            
            g2d.drawImage(Assets.dialogueBackgroundI, 0, screenHeight-speechHeight, null);
            intro7Text.tick();
            halTextArea.setText(intro7Text.getCurrentFrame());
            halTextArea.paint(g2d);
            
            if (handler.getKeyManager().inventory && (newPopUpQuery - lastPopUp > 2000))
            {
                currentIntro++;
                renderHal = false;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentIntro == 8)//Fix this, when you press "x" before clicking heal, it breaks heal button
        {
            renderHal = true;
            
            g2d.drawImage(Assets.dialogueBackgroundX, 0, screenHeight-speechHeight, null);
            intro8Text.tick();
            halTextArea.setText(intro8Text.getCurrentFrame());
            halTextArea.paint(g2d);
            
            if (handler.getKeyManager().readText || handler.getPlayer().getHealth()==handler.getPlayer().getMAX_HEALTH() && (newPopUpQuery - lastPopUp > 2000))
            {
                handler.setInGameIntro(false);
                setCompareItems();
                renderHal = false;
                lastPopUp = System.currentTimeMillis();
            }
        }
    }
    
    public void setCompareItems()
    {
        ArrayList<Item> list = handler.getWorld().getInventory().getInventoryItems();
        for(Item i : list)
        {
            if(i.getID() == 0)
            {
                cSilicon = i.getCount();
            }

            if(i.getID() == 1)
            {
                cCarbon = i.getCount();
            }

            if(i.getID() == 2)
            {
                cAluminum = i.getCount();
            }
            if(i.getID() == 3)
            {
                cPlasma = i.getCount();
            }
        }
    }
    
    public void renderOutro(Graphics g)
    {
        renderHal = false;
        
        
        if (currentOutroSlide == 0)
        {
            g.drawImage(Assets.outro1, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 1000))
            {
                currentOutroSlide++;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentOutroSlide == 1)
        {
            g.drawImage(Assets.outro2, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 1000))
            {
                currentOutroSlide++;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentOutroSlide == 2)
        {
            g.drawImage(Assets.outro3, 0, 0, screenWidth, screenHeight, null);
            if (handler.getKeyManager().readText && (newPopUpQuery - lastPopUp > 1000))
            {
                currentOutroSlide++;
                lastPopUp = System.currentTimeMillis();
            }
        }
        else if (currentOutroSlide == 3)
        {
            g.setFont(titleFont);
            g.setColor(Color.WHITE);
            g.drawImage(Assets.slide5, 0, 0, screenWidth, screenHeight, null);
            g.drawString(toBeContText.getCurrentFrame(), (int)(screenWidth*.15), screenHeight/2);
            {
                //END OF GAME!!!!
            }
        }

    }
    
    public void refreshHal()
    {
        if (System.currentTimeMillis() - lastHalRefresh > 5000)
        {
            hal = new Animation(200, Assets.hal, 2, true);
            lastHalRefresh = System.currentTimeMillis();
        }
    }
    
    public boolean getInIntro()
    {
        return inIntro;
    }
    
    public void renderInfoArrowsAroundObject(Graphics g, Entity e)
    {
        if (!handler.getKeyManager().inventory)
        {
            Graphics2D g2d = (Graphics2D)g.create();
            float x = e.getCenterX();
            float y = e.getCenterY();
            double radius = e.radius*1.2;
            
            infoArrowOffset += .005;
            if (infoArrowOffset >= Math.PI*2)
            {
                infoArrowOffset = 0;
            }
            
            for (int c = 0; c < 16; c++)
            {
                double angleInRad = Math.toRadians(c*22.5)+infoArrowOffset;
                AffineTransform old = g2d.getTransform();
                AffineTransform arrow = AffineTransform.getTranslateInstance((x + (radius*Math.cos(angleInRad)) - handler.getGameCamera().getxOffset()), (y + (radius*Math.sin(angleInRad)) - handler.getGameCamera().getyOffset()));
                arrow.rotate(angleInRad);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity));
                g2d.drawImage(infoArrow, arrow, null);
                g2d.setTransform(old);
            }
            g2d.dispose();
        }
    }
    
    public void renderStaticInfoArrow(Graphics g, double x, double y, int rotation)
    {
        double angleInRad = Math.toRadians(rotation);
        Graphics2D g2d = (Graphics2D)g.create();
        AffineTransform arrow = AffineTransform.getTranslateInstance((screenWidth * x)-20, (screenHeight * y)+64);
        arrow.rotate(angleInRad);
        arrow.scale(2, 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity));
        g2d.drawImage(infoArrow, arrow, null);
        g2d.dispose();
    }
    
    public void tickOpacity()
    {
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
    /*
    public String typeBodyText(TypeText typeThis)
    {
        String text = ""; 
        
        return text;
    }
    
    public String typeTitleText(TypeText typeThis)
    {
        String text = ""; 
        
        return text;
    }
*/
    
}
