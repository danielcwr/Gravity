package gravity;

import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Game implements Runnable
{
    private Display display;
    private int screenWidth, screenHeight;
    private double scaleWidth, scaleHeight;
    public String title;
    
    private boolean running = false;
    private Thread thread; //I have no idea what this does tbh
    
    private BufferStrategy bs;
    private Graphics g;
    
    //States
    public State menuState;
    public State infoState;
    
    //Input
    private KeyManager keyManager;
    private MouseManager mouseManager;
    
    //Camera
    private GameCamera gameCamera;
    
    //Handler
    private Handler handler;
    
    //FPS
    private int fps = 60;
    
    //Sound throughout game
    private long lastSongStart;
    
    
    public Game(String title, int width, int height, double scaleWidth, double scaleHeight)
    {
        this.screenWidth = width;
        this.screenHeight = height;
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
        
        this.title = title;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
    }
    
    private void init() throws FontFormatException, IOException //START OF THE GAME
    {
        display = new Display(title, screenWidth, screenHeight);
        display.getFrame().addKeyListener(keyManager);
        display.getFrame().addMouseListener(mouseManager);
        display.getFrame().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        Assets.init();
        
        handler = new Handler(this);
        
        lastSongStart = System.currentTimeMillis();
        
        gameCamera = new GameCamera(handler, 0, 0);
        
        menuState = new MenuState(handler);
        State.setState(menuState);
    }
    
    private void tick() throws IOException                                     //Called over and over
    {
        keyManager.tick();
        
        if (State.getState() != null)
        {
            //System.out.println("state is ticking");
            State.getState().tick();
        }
        
        playMainSong();
    }
    
    private void render()                                   //USEFULL - HOW TO DRAW TO CANVAS
    {
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null)
        {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        //Clear Screen
        g.clearRect(0, 0, screenWidth, screenHeight);
        //Draw Here
        
        if (State.getState() != null)
        {
            //System.out.println("State is rendering");
            State.getState().render(g);
        }
        
        //End Drawing
        bs.show();
        g.dispose();
    }
    
    public void run()
    {
        try {
            init();
        } catch (FontFormatException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
                                               //to set FPS to 60
        double timePerTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        
        long timer = 0;                                     //for FPS counter
        int ticks = 0;                                      //for FPS counter
        
        while(running == true)                              //to set FPS to 60
        {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            timer += now - lastTime;                        //for FPS counter
            lastTime = now;
            
            if(delta >= 1)                                  //to set FPS to 60
            {
                try {
                    tick();
                } catch (IOException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
                render();
                ticks++;                                    //for FPS counter
                delta--;
            }
            
            if(timer >= 1000000000)                         //for FPS counter
            {
                //System.out.println(ticks);
                ticks = 0;
                timer = 0;
            }
        }
        
        stop();
    }
    public KeyManager getKeyManager()
    {
        return keyManager;
    }
    
    public MouseManager getMouseManager()
    {
        return mouseManager;
    }
    
    public GameCamera getGameCamera()
    {
        return gameCamera;
    }
    
    public int getScreenWidth()
    {
        return screenWidth;
    }
    
    public int getScreenHeight()
    {
        return screenHeight;
    }
    
    public double getScaleWidth()
    {
        return scaleWidth;
    }
    
    public double getScaleHeight()
    {
        return scaleHeight;
    }
    
    public synchronized void start()
    {
        if (running == true)
        {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start(); //runs "run" method
    }
    
    public synchronized void stop()
    {
        running = false;
        if (running == false)
        {
            return;
        }
        try 
        {
            thread.join();
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
    
}
