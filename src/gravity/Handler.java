package gravity;

import java.util.Random;

public class Handler 
{
    private Game game;
    private GameState gameState;
    private World world;
    private boolean inIntro = true;
    private boolean inGameIntro = false;
    private boolean inOutro = false;
    private int worldSize = 100;
    private int itemSize = 24;
    private boolean playSound = false;
    
    public boolean playSound()
    {
        return playSound;
    }
    
    public void setWorldSize(int worldSize)
    {
        this.worldSize = worldSize * 1024;
    }
    
    public int getWorldSize()
    {
        return worldSize;
    }
    
    public boolean getInGameIntro()
    {
        return inGameIntro;
    }
    
    public void setInGameIntro(boolean inGameIntro)
    {
        this.inGameIntro = inGameIntro;
    }
    
    public boolean getInIntro()
    {
        return inIntro;
    }
    
    public void setInIntro(boolean inIntro)
    {
        this.inIntro = inIntro;
    }
    
    public boolean getInOutro()
    {
        return inOutro;
    }
    
    public void setInOutro(boolean inOutro)
    {
        this.inOutro = inOutro;
    }
    
    public int random(int min, int max)
    {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public double randomDouble()
    {
        Random rand = new Random();
        return rand.nextDouble();
    }
    
    public Handler(Game game)
    {
        this.game = game;
    }
    
    public int getScreenWidth()
    {
        return game.getScreenWidth();
    }
    
    public int getScreenHeight()
    {
        return game.getScreenHeight();
    }
    
    public double getScaleWidth()
    {
        return game.getScaleWidth();
    }
    
    public double getScaleHeight()
    {
        return game.getScaleHeight();
    }
    
    public Game getGame()
    {
        return game;
    }
    
    public void setGame(Game game)
    {
        this.game = game;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    public void setWorld(World world)
    {
        this.world = world;
    }
    
    
    //CLASSES
    public GameCamera getGameCamera()
    {
        return game.getGameCamera();
    }
    
    public KeyManager getKeyManager()
    {
        return game.getKeyManager();
    }
    
    public MouseManager getMouseManager()
    {
        return game.getMouseManager();
    }
    
    public Player getPlayer()
    {
        return world.getEntityManager().getPlayer();
    }
    
    public void setGameState(GameState gameState)
    {
        this.gameState = gameState;
    }
    
    public GameState getGameState()
    {
        return gameState;
    }
    
    public int getItemSize()
    {
        return itemSize;
    }
    
}
