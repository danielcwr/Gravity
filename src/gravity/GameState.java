package gravity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class GameState extends State
{
    private UIManager gameUIManager;
    private UIManager deathUIManager;
    private World world;
    private Storyline storyline;
    
    private final int screenWidth = handler.getScreenWidth();
    private final int screenHeight = handler.getScreenHeight();
    private final double scaleWidth = handler.getScaleWidth();
    private final double scaleHeight = handler.getScaleHeight();
    
    private final int guiSize = 4;
    private final double guageWidth = Assets.HPEmpty.getWidth()*scaleWidth;
    private final double guageHeight = Assets.HPEmpty.getHeight()*scaleHeight;
    
    private final double buttonWidth = 192*scaleWidth;
    private final double buttonHeight = 64*scaleHeight;
    
    private boolean loaded = false;
    private boolean loading = false;
    
    //Respawn screen stuff
    private Font hugeFont;
    private Font veryLargeFont;
    private Font largeFont;
    private Font smallFont;
    
    private int respawnState = 0;
    private int placeOffset = (int)(screenWidth*.37);
    private int nameOffset = (int)(screenWidth*.42);
    private int scoreOffset = (int)(screenWidth*.54);
    private int verticalOffset = (int)(screenHeight*.29);
    private int horizontalOffset = (int)(screenWidth*.265);
    private int infoOffset = (int)(screenWidth * .025);
    private int multiplierOffset = (int)(screenWidth *.35);
    private int pointsOffset = (int)(screenWidth *.425);
    private int amountOffset = (int)(screenWidth *.285);
    
        //score organization
    private boolean sortedScores = false;
    private ArrayList<String[]> scores;
    private ArrayList<String[]> asteroidScores;
    
    private boolean inputScore = false;
    private String inputName = "";
    private boolean inputNameFinal = false;
    private boolean updatedScores = false;
    private int[] nebulasSeen = new int[2];
    private int[] planetsSeen = new int[2];
    private int[] enemiesDestroyed = new int[2];
    private int[] asteroidsDestroyed = new int[2];
    private int[] materialsCollected = new int[2];
    private int totalScore;
    private int asteroidHighScore;
    private long lastRead;
    private long lastType;
    
    //private long lastSongStart;
    
    public GameState(final Handler handler) throws IOException //Loads world from the text file
    {
        super(handler);
        
        //Sound
        //lastSongStart = System.currentTimeMillis();
        
        gameUIManager = new UIManager(handler);
        deathUIManager = new UIManager(handler);
        handler.getMouseManager().setUIManager(new UIManager[]{gameUIManager});
        
        world = new World(handler, "/world/MainWorld.txt");
        handler.setWorld(world);
        
        storyline = new Storyline(handler);
        
        //UI Stuff
        gameUIManager.addObject(new UIHealthBar(handler, (int) (screenWidth - (guageWidth*guiSize)), 0, (int)guageWidth*guiSize, (int)guageHeight*guiSize, Assets.HPEmpty, Assets.HP, Assets.invincible));
        gameUIManager.addObject(new UIBlasterBar(handler, (int) (screenWidth - (guageWidth*guiSize)/1.5), (int)guageHeight*guiSize, (int) ((guageWidth*guiSize)/1.5), (int) ((guageHeight*guiSize)/1.5), Assets.blasterEmpty, Assets.blaster));
        gameUIManager.addObject(new UIMisc(handler));
        
        //DeathUI Stuff
        deathUIManager.addObject(new UIImageButton(handler, (int)((screenWidth/2)-(buttonWidth*2)-60), (int)((screenHeight*.7)), 
                                               (int)buttonWidth*2, (int)buttonHeight*2, Assets.mainMenu_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                handler.getMouseManager().setUIManager(null);
                handler.getPlayer().resetStats();
                updatedScores = false;
                sortedScores = false;
                inputScore = false;
                inputNameFinal = false;
                State.setState(new MenuState(new Handler(handler.getGame())));
                
            }
        }, true));
        
        deathUIManager.addObject(new UIImageButton(handler, (int)((screenWidth/2)+60), (int)(screenHeight*.7), 
                                               (int)buttonWidth*2, (int)buttonHeight*2, Assets.respawn_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                handler.getMouseManager().setUIManager(new UIManager[]{gameUIManager});
                handler.getPlayer().playerRespawn();
                respawnState = 0;
                handler.getPlayer().resetStats();
                updatedScores = false;
                sortedScores = false;
                inputScore = false;
                inputNameFinal = false;
            }
        }, true));
        
        hugeFont = Assets.blockText.deriveFont(120f * (float)scaleWidth);
        veryLargeFont = Assets.blockText.deriveFont(60f * (float)scaleWidth);
        largeFont = Assets.blockText.deriveFont(40f * (float)scaleWidth);
        smallFont = Assets.blockText.deriveFont(20f * (float)scaleWidth);
        
        scores = new ArrayList<String[]>();
        scores = Assets.highScores;
        
        lastRead = System.currentTimeMillis();
        lastType = System.currentTimeMillis();
        
        handler.setGameState(this);
    }
    
    @Override
    public void tick() 
    {
        if (!handler.getInIntro() && !handler.getInOutro())
        {
            if (!loaded)
            {
                loading = true;
            }
            else
            {
                loading = false;
            }
            world.tick();
        }
        gameUIManager.tick();
        storyline.tick();
        if (handler.getPlayer().isPlayerDead())
        {
            handler.getMouseManager().setUIManager(new UIManager[]{deathUIManager});
            deathUIManager.tick();
            if (updatedScores == false)
            {
                nebulasSeen[0] = handler.getPlayer().getNebulasSeen();
                nebulasSeen[1] = nebulasSeen[0] * 1000;

                planetsSeen[0] = handler.getPlayer().getPlanetsSeen();
                planetsSeen[1] = planetsSeen[0] * 500;

                enemiesDestroyed[0] = handler.getPlayer().getEnemiesDestroyed();
                enemiesDestroyed[1] = enemiesDestroyed[0] * 50;

                asteroidsDestroyed[0] = handler.getPlayer().getAsteroidsDestroyed();
                asteroidsDestroyed[1] = asteroidsDestroyed[0] * 20;

                materialsCollected[0] = handler.getPlayer().getMaterialsCollected();
                materialsCollected[1] = materialsCollected[0] * 5;
                
                totalScore = 0;
                asteroidHighScore = 0;
                updatedScores = true;
            }
        }
    }

    @Override
    public void render(Graphics g) 
    {
        if (loaded)
        {
            world.render(g);
        }
        else
        {
            world.getEntityManager().load(g);
        }
        if (!handler.getInIntro() && !handler.getInOutro())
        {
            if (handler.getPlayer().isPlayerDead())
            {
                renderRespawnScreen(g);
            }
            else
            {
                gameUIManager.render(g);
            }
        }
        storyline.render(g);
        
    }
    
    public void renderRespawnScreen(Graphics g)
    {
        g.drawImage(Assets.invBackground, screenWidth/2 - 500, screenHeight/2 - 375, 1000, 750, null);
        if (respawnState == 0 || respawnState == 1)
        {
            g.setFont(hugeFont);
            g.setColor(Color.RED);
            g.drawString("Game Over!", (int)(screenWidth * .32), (int)(screenHeight*.28));

            g.setFont(largeFont);
            g.setColor(Color.YELLOW);
            g.drawString("Nebulas Discovered: ", (int)(infoOffset + horizontalOffset), (int)(screenHeight * .05 + verticalOffset));   
            g.drawString("Planets Discovered: ", (int)(infoOffset + horizontalOffset), (int)(screenHeight * .10 + verticalOffset));   
            g.drawString("Enemies Destroyed: ", (int)(infoOffset + horizontalOffset), (int)(screenHeight * .15 + verticalOffset));    
            g.drawString("Asteroids Destroyed: ", (int)(infoOffset + horizontalOffset), (int)(screenHeight * .20 + verticalOffset));  
            g.drawString("Materials Collected: ", (int)(infoOffset + horizontalOffset), (int)(screenHeight * .25 + verticalOffset)); 
            
            

            g.setColor(Assets.ionEngineColors[0]);
            g.drawString(Integer.toString(nebulasSeen[0]), (int)(amountOffset + horizontalOffset), (int)(screenHeight * .05 + verticalOffset));
            g.drawString(Integer.toString(planetsSeen[0]), (int)(amountOffset + horizontalOffset), (int)(screenHeight * .10 + verticalOffset));
            g.drawString(Integer.toString(enemiesDestroyed[0]), (int)(amountOffset + horizontalOffset), (int)(screenHeight * .15 + verticalOffset));
            g.drawString(Integer.toString(asteroidsDestroyed[0]), (int)(amountOffset + horizontalOffset), (int)(screenHeight * .20 + verticalOffset));
            g.drawString(Integer.toString(materialsCollected[0]), (int)(amountOffset + horizontalOffset), (int)(screenHeight * .25 + verticalOffset));
            
            
            g.setColor(Color.MAGENTA);
            g.drawString(Integer.toString(nebulasSeen[1]), (int)(pointsOffset + horizontalOffset), (int)(screenHeight * .05 + verticalOffset));
            g.drawString(Integer.toString(planetsSeen[1]), (int)(pointsOffset + horizontalOffset), (int)(screenHeight * .10 + verticalOffset));
            g.drawString(Integer.toString(enemiesDestroyed[1]), (int)(pointsOffset + horizontalOffset), (int)(screenHeight * .15 + verticalOffset));
            g.drawString(Integer.toString(asteroidsDestroyed[1]), (int)(pointsOffset + horizontalOffset), (int)(screenHeight * .20 + verticalOffset));
            g.drawString(Integer.toString(materialsCollected[1]), (int)(pointsOffset + horizontalOffset), (int)(screenHeight * .25 + verticalOffset));
            
            g.setFont(veryLargeFont);
            g.drawString(Integer.toString(totalScore), (int)(screenWidth * .55), (int)(screenHeight * .65));
            g.setColor(Color.YELLOW);
            g.drawString("Total: ", (int)(screenWidth * .4), (int)(screenHeight * .65));

            g.setFont(smallFont);
            g.setColor(Color.green);
            g.drawString("x1000", (int)(multiplierOffset + horizontalOffset), (int)(screenHeight * .05 + verticalOffset));// 1000 pts 
            g.drawString("x500", (int)(multiplierOffset + horizontalOffset), (int)(screenHeight * .10 + verticalOffset));// 500 pts
            g.drawString("x50", (int)(multiplierOffset + horizontalOffset), (int)(screenHeight * .15 + verticalOffset));// 50 pts
            g.drawString("x20", (int)(multiplierOffset + horizontalOffset), (int)(screenHeight * .20 + verticalOffset));// 20 pts
            g.drawString("x5", (int)(multiplierOffset + horizontalOffset), (int)(screenHeight * .25 + verticalOffset));// 5 pts
            
            
        }
        
        if (respawnState == 1)
        {
            if (nebulasSeen[1] > 0)
            {
                nebulasSeen[1]-= 20;
                totalScore+= 20;
                lastRead = System.currentTimeMillis();
            }
            else if (planetsSeen[1] > 0)
            {
                planetsSeen[1]-= 10;
                totalScore+= 10;
                lastRead = System.currentTimeMillis();
            }
            else if (enemiesDestroyed[1] > 0)
            {
                enemiesDestroyed[1]-=5;
                totalScore+= 5;
                lastRead = System.currentTimeMillis();
            }
            else if (asteroidsDestroyed[1] > 0)
            {
                asteroidsDestroyed[1]-= 2;
                totalScore+= 2;
                lastRead = System.currentTimeMillis();
            }
            else if (materialsCollected[1] > 0)
            {
                materialsCollected[1]-= 5;
                totalScore+= 5;
                lastRead = System.currentTimeMillis();
            }
        }
        
        if (respawnState >= 2)
        {
            if (!sortedScores)
            {
                for (int c = 0; c < scores.size(); c++)
                {
                    int pos = c;
                    String[] front = scores.get(c);
                    for (int x = c; x < scores.size(); x++)
                    {
                        //System.out.println(scores.get(x)[0] + ":" +scores.get(x)[1]);
                        if (Integer.parseInt(scores.get(x)[1]) > Integer.parseInt(front[1]))
                        {
                            pos = x;
                            front = scores.get(x);
                        }
                    }
                    if (pos != c)
                    {
                        String[] temp = scores.get(c);
                        scores.set(c, front);
                        scores.set(pos, temp);
                    }
                    
                }
                sortedScores = true;
            }
            if (scores.isEmpty() || totalScore > Integer.parseInt(scores.get(0)[1]))
            {
                g.setFont(veryLargeFont);
                g.setColor(Color.RED);
                g.drawString("New High Score!", (int)(screenWidth * .38), (int)(screenHeight*.28));
                getName();
                
                g.setFont(largeFont);
                g.setColor(Color.YELLOW);
                g.drawString(inputName, nameOffset, (int)(screenHeight * (.3) + verticalOffset));
                g.setColor(Color.MAGENTA);
                g.drawString(Integer.toString(totalScore), scoreOffset, (int)(screenHeight * (.3) + verticalOffset));
            }
            else if (scores.size() < 5)
            {
                g.setFont(veryLargeFont);
                g.setColor(Color.RED);
                g.drawString("A Top 5 Performance!", (int)(screenWidth * .34), (int)(screenHeight*.28));
                getName();
                
                g.setFont(largeFont);
                g.setColor(Color.YELLOW);
                g.drawString(inputName, nameOffset, (int)(screenHeight * (.3) + verticalOffset));
                g.setColor(Color.MAGENTA);
                g.drawString(Integer.toString(totalScore), scoreOffset, (int)(screenHeight * (.3) + verticalOffset));
            }
            else if (totalScore > Integer.parseInt(scores.get(4)[1]))
            {
                g.setFont(veryLargeFont);
                g.setColor(Color.RED);
                g.drawString("A Top 5 Performance!", (int)(screenWidth * .34), (int)(screenHeight*.28));
                getName();
                
                g.setFont(largeFont);
                g.setColor(Color.YELLOW);
                g.drawString(inputName, nameOffset, (int)(screenHeight * (.3) + verticalOffset));
                g.setColor(Color.MAGENTA);
                g.drawString(Integer.toString(totalScore), scoreOffset, (int)(screenHeight * (.3) + verticalOffset));
            }
            
            
            g.setFont(largeFont);
            g.setColor(Color.GREEN);
            for (int c = 0; c < 5; c++)
            {
                g.drawString(Integer.toString(c+1), placeOffset, (int)(screenHeight * ((c+1)*.05) + verticalOffset));
            }
            
            g.setColor(Color.YELLOW);
            for (int c = 0; c < scores.size(); c++)
            {
                if (c == 5)
                {
                    break;
                }
                g.drawString(scores.get(c)[0], nameOffset, (int)(screenHeight * ((c+1)*.05) + verticalOffset));
            }
            
            g.setColor(Color.MAGENTA);
            for (int c = 0; c < scores.size(); c++)
            {
                if (c == 5)
                {
                    break;
                }
                g.drawString(scores.get(c)[1], scoreOffset, (int)(screenHeight * ((c+1)*.05) + verticalOffset));
            }
            
            if (inputNameFinal == true && inputScore == false)
            {
                scores.add(new String[]{inputName, Integer.toString(totalScore)});
                saveScores();
                sortedScores = false;
                inputScore = true;
            }
            deathUIManager.render(g);
        }
        
        if (System.currentTimeMillis() - lastRead > 500)
        {
            if (respawnState == 0 || respawnState == 1)
            {
                g.setFont(largeFont);
                g.setColor(Assets.ionEngineColors[0]);
                g.drawString("Press 'X' to continue", (int)(screenWidth * .73), (int) (screenHeight * .9));
            }
            if (handler.getKeyManager().readText)
            {
                lastRead = System.currentTimeMillis();
                respawnState ++;
            }
        }
    }
    
    private void saveScores()
    {
        try
        {
            File file = new File(Assets.dirPath + "/highScores.txt");

            if (!file.exists())
            {
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int counter = 0; counter < scores.size(); counter++)
            {
            bw.write(scores.get(counter)[0]);
            bw.write(":");
            bw.write(scores.get(counter)[1]);
            bw.newLine();
            }
            bw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private void getName()
    {
        if (System.currentTimeMillis() - lastType > 250)
        {
            int keyPressed = handler.getKeyManager().getKeyPressed();
            if (keyPressed == -1)
            {
                
            }
            else
            {
                if (keyPressed != -1 && keyPressed != 8 && keyPressed != 10 && keyPressed != 13 && keyPressed != 16)
                {
                    if (inputName.length() < 6)
                    {
                        char character = (char)keyPressed;
                        inputName = inputName + "" +character;
                        lastType = System.currentTimeMillis();
                    }
                }
                else if (keyPressed == 8 && inputName.length() > 0)
                {
                    inputName = inputName.substring(0, inputName.length()-1);
                    lastType = System.currentTimeMillis();
                }
                else if (keyPressed == 10 || keyPressed == 13)
                {
                    inputNameFinal = true;
                }

                if (inputName.length() > 6)
                {
                    inputName = inputName.substring(0, 5);
                }
                inputName = inputName.toUpperCase();
            }
        }
    }
    
    public void setUIManager_to_gameUIManager()
    {
        handler.getMouseManager().setUIManager(new UIManager[]{gameUIManager});
    }
    
    /*
    public void playMainSong() throws IOException
    {
        if (System.currentTimeMillis() - lastSongStart > 12000)
        {
            //stops old song
            lastSongStart = System.currentTimeMillis();

            //new song
            AudioPlayer.player.start(new AudioStream(new ByteArrayInputStream(Assets.mainSong)));
        }
    }
    */
    
    public boolean getLoading()
    {
        return loading;
    }
    
    public void setLoading(boolean loading)
    {
        this.loading = loading;
    }
    
    public boolean getLoaded()
    {
        return loaded;
    }
    
    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded;
    }
}
