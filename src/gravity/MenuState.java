package gravity;

import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class MenuState extends State
{   
    private UIManager uiManager;
    private int screenWidth = handler.getScreenWidth();
    private int screenHeight = handler.getScreenHeight();
    private final double scaleWidth = handler.getScaleWidth();
    private final double scaleHeight = handler.getScaleHeight();
    
    private int dankWidth = screenWidth+100;
    private int dankHeight = screenHeight+100;
    
    private double buttonWidth = 192*scaleWidth;
    private double buttonHeight = 64*scaleHeight;
    
    //private long lastSongStart;
    
    
    public MenuState(final Handler handler)
    {
        super(handler);
        //Sound
        //lastSongStart = System.currentTimeMillis();
        
        //UI
        uiManager = new UIManager(handler);
        handler.getMouseManager().setUIManager(new UIManager[]{uiManager});
        
        //AddObjects to title screen
        uiManager.addObject(new UIAnimatedImage(handler ,-100, -100, dankWidth, dankHeight, Assets.mainBackground, 700)); //BACKGROUND
        uiManager.addObject(new UIAnimatedImage(handler, 0, (int)(screenHeight /2 ), (int)(screenWidth), (int)(screenWidth), Assets.planet1, true, .05f)); //SPINNING EARTH
        uiManager.addObject(new UIAnimatedImage(handler, (screenWidth/2)-(int)(144*4*scaleWidth), (screenHeight/4)-(int)(29*2*scaleHeight)-50, (int)((144)*8*scaleWidth), (int)((29)*8*scaleHeight), Assets.GRAVITY)); //GRAVITY LOGO
        
        uiManager.addObject(new UIImageButton(handler, (int)((screenWidth/2)-(buttonWidth)), (int)((screenHeight/2)-(buttonHeight)+175*scaleHeight), 
                                               (int)buttonWidth*2, (int)buttonHeight*2, Assets.play_button, new ClickListener()
        {
            @Override
            public void onClick() 
            {
                handler.getMouseManager().setUIManager(null);
                try {
                    State.setState(new GameState(handler));
                } catch (IOException ex) {
                    Logger.getLogger(MenuState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, true));
        
        uiManager.addObject(new UIImageButton(handler, (int)((screenWidth/2)-(buttonWidth)), (int)((screenHeight/2)-(buttonHeight)+325*scaleHeight), 
                                               (int)buttonWidth*2, (int)buttonHeight*2, Assets.howToPlay_button, new ClickListener()//22
        {
            @Override
            public void onClick() 
            {
                handler.getMouseManager().setUIManager(null);
                State.setState(new InfoState(handler));
            }
        }, true));
        
        
    }
    
    @Override
    public void tick() 
    {
        uiManager.tick();
        /*
        try {
            playMainSong();
        } catch (IOException ex) {
            Logger.getLogger(MenuState.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

    @Override
    public void render(Graphics g) 
    {
        uiManager.render(g);
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
}
