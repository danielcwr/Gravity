package gravity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class Assets //loads any file used in game
{
    private static final int w = 64, h = 64;
    public static BufferedImage tileStandard, 
                                nebula1,nebula2,nebula3,nebula4,
                                planet1, planet2, planet3, planet4, planet5, planet6, planet7, planet8, planet9, planet10, blackHole, wormhole,
                                asteroid1, asteroid2, asteroid3, asteroidSilicon1, asteroidSilicon2,asteroidAluminum1, asteroidAluminum2,
                                ship1, ship2Stock, ship2Engines, ship2Guns, ship2Full,
                                titleScreenBR, GRAVITY,
                                silicon, carbon, aluminum, plasma,
                                HP, HPEmpty, warningFlash, blaster, blasterEmpty, invincible,
                                invBackground, specText, upgradeText, modText, advancedHullText, equiped, equipBlastersText, equipEnginesText,
                                slide1, slide2, slide3, slide4, slide5, outro1, outro2, outro3,
                                dialogueBackgroundX, dialogueBackgroundI,
                                arrowIndicator, IACircleDot, infoArrow;
    
    public static BufferedImage[] shipLaserAnimated, shipLaserHit;
    
    public static BufferedImage[] play_button, howToPlay_button, back_button, respawn_button, mainMenu_button,
                                  controls_button, inventory_button, objectives_button, scoringPoints_button, questionMark_button;
    public static BufferedImage[] mainBackground;
    public static BufferedImage[] randomAsteroid;
    public static BufferedImage[] randomNebula;
    public static BufferedImage[] lowHP;
    
    public static BufferedImage[] specsButton;
    public static BufferedImage[] modsButton;
    public static BufferedImage[] upgradeButton;
    public static BufferedImage[] healButton;
    public static BufferedImage[] autoTractorBeamIcon, lArrow, rArrow;
    public static BufferedImage[] enemies;
    
    public static BufferedImage[] deathAnimation;
    
    public static ArrayList<String[]> highScores, asteroidsHighScores;
    public static ArrayList<String> storyTextBody;
    public static ArrayList<String> storyTextTitle;
    
    public static BufferedImage[] hal, halsLights;
    
    public static Font blockText;
    
    public static World world;
    
    public static byte[] mainSong;
    public static byte[] laserSound;
    public static byte[] engineSound;
    
    public static Color[] asteroidColors, engineColors, ionEngineColors, plasmaBlasterColors, explosionColors, nebSniperShotColors, tractorPullColors, tractorPushColors, miningLaserColors, afterBurnerColors;
    
    public static String dirPath;
    
    public static void init() throws FontFormatException, IOException
    {
        //Colors
        asteroidColors = new Color[] {new Color(156, 72, 171),      //Dark Purple
                                      new Color(233, 145, 247),     //Light Purple
                                      new Color(102, 102, 102),     //Dark Grey
                                      new Color(120, 120, 120),     //Grey
                                      new Color(166, 166, 166),     //Light Grey
                                      new Color(186, 145, 59),      //Dark Gold
                                      new Color(249, 193, 79)};     //Light Gold
        
        engineColors = new Color[] {new Color(255, 216, 0),         //Yellow
                                    new Color(255, 106, 0),         //Orange
                                    new Color(255, 0, 0),           //Red
                                    new Color(102, 102, 102),       //Dark Grey
                                    new Color(120, 120, 120)};      //Grey
        
        ionEngineColors = new Color[] {new Color(0, 255, 255),      //Light Blue
                                       new Color(0, 157, 255),      //Blue
                                       new Color(0, 39, 255)};      //Dark Blue
        
        explosionColors = new Color[] {new Color(255, 216, 0),      //Yellow
                                    new Color(255, 106, 0),         //Orange
                                    new Color(255, 0, 0),           //Red
                                    new Color(102, 102, 102),       //Dark Grey
                                    new Color(120, 120, 120)};      //Grey
        
        plasmaBlasterColors = new Color[] {new Color(0, 255, 255),  //Light Blue
                                       new Color(0, 157, 255),      //Blue
                                       new Color(233, 145, 247)};   //LightPurple
        
        nebSniperShotColors = new Color[] {new Color(0, 148, 255),  //Light Blue
                                       new Color(58, 196, 0),       //Light Green
                                       new Color(47, 150, 0)};      //Dark Green
        
        tractorPullColors = new Color[] {new Color(0, 148, 255),    //Light Blue
                                       new Color(0, 255, 255),      //Light Blue
                                       new Color(0, 157, 255),      // Blue
                                       new Color(166, 166, 166)};   // Light Grey
        
        tractorPushColors = new Color[] {new Color(255, 207, 0),    //Light Orange
                                       new Color(255, 179, 0),      //Light Blue
                                       new Color(255, 72, 0),      // Blue
                                       new Color(166, 166, 166)};   // Light Grey
        
        miningLaserColors = new Color[] {new Color(216, 255, 223),    //faded green
                                       new Color(173, 255, 188),      //light green
                                       new Color(79, 255, 108)};   //neon green
        
        afterBurnerColors = new Color[] {new Color(127, 51, 0),    //dark orange
                                       new Color(255, 106, 0)};   //orange
        
        //Audio
        mainSong = (FileLoader.loadAudio("/audio/mainSong.wav"));
        laserSound = (FileLoader.loadAudio("/audio/laser.wav"));
        
        //Fonts
        blockText = (FileLoader.loadFont("/fonts/BlockText.ttf"));
        
        //****************************HOME SCREEN*****************************\\
        GRAVITY = (FileLoader.loadImage("/textures/MenuState/GRAVITY.png"));

        
        mainBackground = new BufferedImage[6];
        titleScreenBR = (FileLoader.loadImage("/textures/MenuState/HomeBackground/HB1.png"));
        mainBackground[0] = titleScreenBR;
        mainBackground[1] = (FileLoader.loadImage("/textures/MenuState/HomeBackground/HB2.png"));
        mainBackground[2] = titleScreenBR;
        mainBackground[3] = (FileLoader.loadImage("/textures/MenuState/HomeBackground/HB3.png"));
        mainBackground[4] = titleScreenBR;
        mainBackground[5] = (FileLoader.loadImage("/textures/MenuState/HomeBackground/HB4.png"));
        
        //Buttons
        SpriteSheet butttonSS = new SpriteSheet(FileLoader.loadImage("/textures/MenuState/ButtonSpriteSheet.png"));
        play_button = new BufferedImage[2];
        play_button[0] = butttonSS.crop(w*0, h*0, w*3, h);
        play_button[1] = butttonSS.crop(w*3, h*0, w*3, h);
        
        howToPlay_button = new BufferedImage[2];
        howToPlay_button[0] = butttonSS.crop(w*0, h*1, w*3, h);
        howToPlay_button[1] = butttonSS.crop(w*3, h*1, w*3, h);
        
        back_button = new BufferedImage[2];
        back_button[0] = butttonSS.crop(w*0, h*2, w*3, h);
        back_button[1] = butttonSS.crop(w*3, h*2, w*3, h);
        
        respawn_button = new BufferedImage[2];
        respawn_button[0] = butttonSS.crop(w*0, h*3, w*3, h);
        respawn_button[1] = butttonSS.crop(w*3, h*3, w*3, h);
        
        mainMenu_button = new BufferedImage[2];
        mainMenu_button[0] = butttonSS.crop(w*0, h*4, w*3, h);
        mainMenu_button[1] = butttonSS.crop(w*3, h*4, w*3, h);
        
        controls_button = new BufferedImage[2];
        controls_button[0] = butttonSS.crop(w*0, h*5, w*3, h);
        controls_button[1] = butttonSS.crop(w*3, h*5, w*3, h);
        
        inventory_button = new BufferedImage[2];
        inventory_button[0] = butttonSS.crop(w*0, h*6, w*3, h);
        inventory_button[1] = butttonSS.crop(w*3, h*6, w*3, h);
        
        objectives_button = new BufferedImage[2];
        objectives_button[0] = butttonSS.crop(w*0, h*7, w*3, h);
        objectives_button[1] = butttonSS.crop(w*3, h*7, w*3, h);
        
        scoringPoints_button = new BufferedImage[2];
        scoringPoints_button[0] = butttonSS.crop(w*0, h*8, w*3, h);
        scoringPoints_button[1] = butttonSS.crop(w*3, h*8, w*3, h);
        
        questionMark_button = new BufferedImage[2];
        questionMark_button[0] = butttonSS.crop(w*0, h*9, w*1, h);
        questionMark_button[1] = butttonSS.crop(w*1, h*9, w*1, h);
        //********************************************************************\\
        
        
        //*******************************TILES********************************\\
        tileStandard = (FileLoader.loadImage("/textures/Tiles/TileStandard.png"));
        
        //Nebulas
        randomNebula = new BufferedImage[3];
        nebula1 = (FileLoader.loadImage("/textures/Nebulas/Nebula1.png"));
        nebula2 = (FileLoader.loadImage("/textures/Nebulas/Nebula2.png"));
        nebula3 = (FileLoader.loadImage("/textures/Nebulas/Nebula3.png"));
        
        randomNebula[0] = nebula1;
        randomNebula[1] = nebula2;
        randomNebula[2] = nebula3;
        //nebula4 = (FileLoader.loadImage("/textures/Tiles/CrabNebula/CrabNebula4.png"));
        //********************************************************************\\
        
        
        //***************************SPACE OBJECTS****************************\\
        SpriteSheet spaceObjSS = new SpriteSheet(FileLoader.loadImage("/textures/SpriteSheets/SpaceObjectSpriteSheet.png"));
        
        planet1 = spaceObjSS.crop(w*0, h*0, w, h);              //Earth
        planet2 = spaceObjSS.crop(w*1, h*0, w, h);              //Mars
        planet3 = spaceObjSS.crop(w*2, h*0, w, h);              //Jupiter
        planet4 = spaceObjSS.crop(w*3, h*0, w, h);              //Neptune
        planet5 = spaceObjSS.crop(w*4, h*0, w, h);              //Saturn
        planet6 = spaceObjSS.crop(w*0, h*1, w, h);              
        planet7 = spaceObjSS.crop(w*1, h*1, w, h);
        planet8 = spaceObjSS.crop(w*2, h*1, w, h);
        planet9 = spaceObjSS.crop(w*3, h*1, w, h);
        planet10 = spaceObjSS.crop(w*4, h*1, w, h);
        
        asteroid1 = spaceObjSS.crop(w*0, h*3, w, h);            //S Asteroid
        asteroid2 = spaceObjSS.crop(w*1, h*3, w, h);            //M Asteroid
        asteroid3 = spaceObjSS.crop(w*2, h*3, w, h);            //L Asteroid
        asteroidSilicon1 = spaceObjSS.crop(w*3, h*3, w, h);        //M-Rare Ateroid
        asteroidSilicon2 = spaceObjSS.crop(w*4, h*3, w, h);        //L-Rare Ateroid
        asteroidAluminum1 = spaceObjSS.crop(w*3, h*4, w, h); 
        asteroidAluminum2 = spaceObjSS.crop(w*4, h*4, w, h); 
        
        randomAsteroid = new BufferedImage[7];
        randomAsteroid[0] = asteroid1; 
        randomAsteroid[1] = asteroid2; 
        randomAsteroid[2] = asteroid3;
        randomAsteroid[3] = asteroidSilicon1;
        randomAsteroid[4] = asteroidSilicon2;
        randomAsteroid[5] = asteroidAluminum1;
        randomAsteroid[6] = asteroidAluminum2;
        
        wormhole = spaceObjSS.crop(w*2, h*4, w, h);
        
        blackHole = FileLoader.loadImage("/textures/BlackHole.png");
        
        //********************************************************************\\
        
        
        //****************************PLAYER SHIP*****************************\\
        SpriteSheet shipSS = new SpriteSheet(FileLoader.loadImage("/textures/SpriteSheets/ShipSpriteSheet.png"));

        ship1 = shipSS.crop(w*0, h*0, w, h);                //ship1
        
        ship2Stock = shipSS.crop(w*0, h*1, w, h);           //ship2 Stock
        
        ship2Engines = shipSS.crop(w*0, h*2, w, h);         //ship2 w/ Engine
        
        ship2Guns = shipSS.crop(w*0, h*3, w, h);            //ship2 w/ Guns
        
        ship2Full = shipSS.crop(w*0, h*4, w, h);            //ship2 Full
        
        SpriteSheet itemSS = new SpriteSheet(FileLoader.loadImage("/textures/SpriteSheets/ItemSpriteSheet.png"));
        shipLaserAnimated = new BufferedImage[2];
        shipLaserAnimated[0] = itemSS.crop(w*0, h*4, w, h);
        shipLaserAnimated[1] = itemSS.crop(w*1, h*4, w, h);
        
        shipLaserHit = new BufferedImage[3];
        shipLaserHit[0] = itemSS.crop(w*2, h*4, w, h);
        shipLaserHit[1] = itemSS.crop(w*3, h*4, w, h);
        shipLaserHit[2] = itemSS.crop(w*4, h*4, w, h);
        
        deathAnimation = new BufferedImage[5];
        deathAnimation[0] = itemSS.crop(w*0, h*3, w, h);
        deathAnimation[1] = itemSS.crop(w*1, h*3, w, h);
        deathAnimation[2] = itemSS.crop(w*2, h*3, w, h);
        deathAnimation[3] = itemSS.crop(w*3, h*3, w, h);
        deathAnimation[4] = itemSS.crop(w*4, h*3, w, h);
        //********************************************************************\\
        
        
        //*******************************Items********************************\\;
        silicon = itemSS.crop(w*0, h*0, w, h);
        carbon = itemSS.crop(w*1, h*0, w, h);
        aluminum = itemSS.crop(w*2, h*0, w, h);
        plasma = itemSS.crop(w*3, h*0, w, h);
        //********************************************************************\\
        
        
        //********************************GUI*********************************\\
        HP = (FileLoader.loadImage("/textures/GUI/Health/HP.png"));
        HPEmpty = (FileLoader.loadImage("/textures/GUI/Health/0.png"));
        invincible = (FileLoader.loadImage("/textures/GUI/Health/Invincible.png"));
        warningFlash = (FileLoader.loadImage("/textures/GUI/Health/Warning.png"));
        lowHP  = new BufferedImage[2];
        lowHP[0] = HP;
        lowHP[1] = warningFlash;
        
        
        blaster = (FileLoader.loadImage("/textures/GUI/Blaster/Full.png"));
        blasterEmpty = (FileLoader.loadImage("/textures/GUI/Blaster/0.png"));
        
        SpriteSheet autoTractorSS = new SpriteSheet(FileLoader.loadImage("/textures/GUI/AutoTractorBeamIcon.png"));
        autoTractorBeamIcon = new BufferedImage[2];
        autoTractorBeamIcon[1] = autoTractorSS.crop(w*0, h*0, w, h);
        autoTractorBeamIcon[0] = autoTractorSS.crop(w*1, h*0, w, h);
        
        SpriteSheet arrowSS = new SpriteSheet(FileLoader.loadImage("/textures/GUI/WeaponSelectArrows.png"));
        lArrow = new BufferedImage[2];
        lArrow[0] = arrowSS.crop(0, 0, 16, 16);
        lArrow[1] = arrowSS.crop(16, 0, 16, 16);
        
        rArrow = new BufferedImage[2];
        rArrow[0] = arrowSS.crop(32, 0, 16, 16);
        rArrow[1] = arrowSS.crop(48, 0, 16, 16);
        
        arrowIndicator = (FileLoader.loadImage("/textures/GUI/IndicatorArrow.png"));
        IACircleDot = (FileLoader.loadImage("/textures/GUI/IACircleDot.png"));
        infoArrow = (FileLoader.loadImage("/textures/GUI/InfoArrow.png"));
        //********************************************************************\\
        
        
        //*****************************Inventory******************************\\
        invBackground = (FileLoader.loadImage("/textures/Inventory/InvBackground.png"));
        
        specsButton = new BufferedImage[2];
        specsButton[1] = (FileLoader.loadImage("/textures/Inventory/Specifications/SpecDeselected.png"));
        specsButton[0] = (FileLoader.loadImage("/textures/Inventory/Specifications/SpecSelected.png"));
        specText = (FileLoader.loadImage("/textures/Inventory/Specifications/SpecText.png"));
        upgradeText = (FileLoader.loadImage("/textures/Inventory/Specifications/UpgradeText.png"));
        equipBlastersText = (FileLoader.loadImage("/textures/Inventory/Specifications/EquipBlasters.png"));
        equipEnginesText = (FileLoader.loadImage("/textures/Inventory/Specifications/EquipEngines.png"));
        
        modsButton = new BufferedImage[2];
        modsButton[0] = (FileLoader.loadImage("/textures/Inventory/Modifications/ModDeselected.png"));
        modsButton[1] = (FileLoader.loadImage("/textures/Inventory/Modifications/ModSelected.png"));
        modText = (FileLoader.loadImage("/textures/Inventory/Modifications/ModText.png"));
        advancedHullText = (FileLoader.loadImage("/textures/Inventory/Modifications/AdvancedHull.png"));
        equiped = (FileLoader.loadImage("/textures/Inventory/Modifications/Equiped.png"));
        
        upgradeButton = new BufferedImage[2];
        upgradeButton[0] = (FileLoader.loadImage("/textures/Inventory/UpgradeFalse.png"));
        upgradeButton[1] = (FileLoader.loadImage("/textures/Inventory/UpgradeTrue.png"));
        
        healButton = new BufferedImage[2];
        healButton[0] = (FileLoader.loadImage("/textures/Inventory/HealFalse.png"));
        healButton[1] = (FileLoader.loadImage("/textures/Inventory/HealTrue.png"));
        //********************************************************************\\
        
        
        //**************************Storyline intro***************************\\
        slide1 = (FileLoader.loadImage("/textures/Intro/slide1.png"));
        slide2 = (FileLoader.loadImage("/textures/Intro/slide2.png"));
        slide3 = (FileLoader.loadImage("/textures/Intro/slide3.png"));
        slide4 = (FileLoader.loadImage("/textures/Intro/slide4.png"));
        slide5 = (FileLoader.loadImage("/textures/Intro/ASDMGBackground.png"));
        
        hal = new BufferedImage[3];
        hal[0] = (FileLoader.loadImage("/textures/Intro/dialogueBox/comp1.png"));
        hal[1] = (FileLoader.loadImage("/textures/Intro/dialogueBox/comp2.png"));
        hal[2] = (FileLoader.loadImage("/textures/Intro/dialogueBox/hal.png"));
        
        halsLights = new BufferedImage[3];
        halsLights[0] = (FileLoader.loadImage("/textures/Intro/dialogueBox/flashything1.png"));
        halsLights[1] = (FileLoader.loadImage("/textures/Intro/dialogueBox/flashything2.png"));
        halsLights[2] = (FileLoader.loadImage("/textures/Intro/dialogueBox/flashything3.png"));
        
        dialogueBackgroundX = (FileLoader.loadImage("/textures/Intro/dialogueBox/DialougeBackgroundX.png"));
        dialogueBackgroundI = (FileLoader.loadImage("/textures/Intro/dialogueBox/DialougeBackgroundI.png"));
        
        outro1 = (FileLoader.loadImage("/textures/Outro/outro1.png"));
        outro2 = (FileLoader.loadImage("/textures/Outro/outro2.png"));
        outro3 = (FileLoader.loadImage("/textures/Outro/outro3.png"));
        
        storyTextTitle = new ArrayList();
        storyTextBody = new ArrayList();
        storyText("/halsSpeech.txt");
        //********************************************************************\\
        
        
        //****************************HIGH SCORES*****************************\\
        dirPath = System.getenv("APPDATA");
        dirPath = dirPath + "/Gravity";
        File dir = new File(dirPath);
        dir.mkdir();
        File file = new File(dirPath + "/highScores.txt");

        if (!file.exists())
        {
            file.createNewFile();
        }
        
        highScores = new ArrayList<String[]>();
        highScores(dirPath + "/highScores.txt");
        
        
        dirPath = System.getenv("APPDATA");
        dirPath = dirPath + "/Gravity";
        dir = new File(dirPath);
        dir.mkdir();
        file = new File(dirPath + "/asteroidsHighScores.txt");

        if (!file.exists())
        {
            file.createNewFile();
        }
        asteroidsHighScores = new ArrayList<String[]>();
        highScores(dirPath + "/asteroidsHighScores.txt");
        //********************************************************************\\
        
        
        //******************************ENEMIES*******************************\\
        SpriteSheet EnemiesSS = new SpriteSheet(FileLoader.loadImage("/textures/SpriteSheets/EnemySpriteSheet.png"));
        enemies = new BufferedImage[5];
        enemies[0] = EnemiesSS.crop(w*0, h*0, w, h);
        enemies[1] = EnemiesSS.crop(w*0, h*1, w, h);
        enemies[2] = EnemiesSS.crop(w*0, h*2, w, h);
        enemies[3] = EnemiesSS.crop(w*0, h*3, w, h);
        enemies[4] = EnemiesSS.crop(w*0, h*4, w, h);
    }
    
    public static void highScores(String path) throws IOException
    {
        String OneLineData;
        String tempToken;
        String[] data = new String[2];
        
        Scanner ReaderTimes = FileLoader.loadText(path);
        while (ReaderTimes.hasNextLine())
        {
            OneLineData = ReaderTimes.nextLine();

            if (OneLineData.startsWith("/"))
            {
                //Just skips it
            }
            else
            {
                StringTokenizer token = new StringTokenizer(OneLineData, ":");
                //System.out.println(OneLineData);
                tempToken = token.nextToken();
                tempToken = tempToken.trim();
                data[0] = tempToken;
                
                if (token.hasMoreTokens())
                {
                    tempToken = token.nextToken();
                    tempToken = tempToken.trim();
                    data[1] = tempToken;
                }
                else
                {
                    data[1] = "0";
                }
                highScores.add(data);
            }
            data = new String[2];
            
        }
    }
    
    public static void storyText(String path) throws FileNotFoundException, IOException
    {
        String OneLineData;
        String tempToken;
        
        Scanner ReaderTimes = FileLoader.loadText(path);
        while (ReaderTimes.hasNextLine())
        {
            OneLineData = ReaderTimes.nextLine();

            if (OneLineData.startsWith("/"))
            {
                //Just skips it
            }
            else
            {
                StringTokenizer token = new StringTokenizer(OneLineData, ":");
                token.nextToken();
                
                tempToken = token.nextToken();
                tempToken = tempToken.trim();
                storyTextTitle.add(tempToken);
                
                if (token.hasMoreTokens())
                {
                    tempToken = token.nextToken();
                    tempToken = tempToken.trim();
                    storyTextBody.add(tempToken);
                }
                else
                {
                    storyTextBody.add("N/A");
                }
            }
        }
    }
    
    public static void loadSound(String path) throws FileNotFoundException, IOException
    {
        AudioPlayer MGP = AudioPlayer.player;
        AudioStream BGM;
        AudioData MD;
        ContinuousAudioDataStream loop = null;
        
        try{
        BGM = new AudioStream(new FileInputStream(path));
        MD = BGM.getData();
        loop = new ContinuousAudioDataStream(MD);
        } catch(IOException error){}
        MGP.start(loop);
    }
}
