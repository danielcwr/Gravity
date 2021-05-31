package gravity;

public class GameCamera 
{
    private Handler handler;
    private float xOffset, yOffset;
    
    public GameCamera(Handler handler, float xOffset, float yOffset)
    {
        this.handler = handler;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    public void checkBlankSpace() //gets rid of white border when the "world" has no more tiles
    {
        if(xOffset < 0)
        {
            xOffset = 0;
        }
        else if (xOffset > handler.getWorld().getWidth() * Tile.TILE_WIDTH - handler.getScreenWidth())
        {
            xOffset = handler.getWorld().getWidth() * Tile.TILE_WIDTH - handler.getScreenWidth();
        }
        
        if (yOffset < 0)
        {
            yOffset = 0;
        }
        else if (yOffset > handler.getWorld().getHeight() * Tile.TILE_HEIGHT - handler.getScreenHeight())
        {
            yOffset = handler.getWorld().getHeight() * Tile.TILE_HEIGHT - handler.getScreenHeight();
        }
    }
                    
    
    public void centerOnEntity(Entity e) //makes player always centred
    {
        xOffset = e.getX() - (handler.getScreenWidth() /2) + (e.getWidth() /2);
        yOffset = e.getY() - (handler.getScreenHeight() / 2) + (e.getHeight() /2);
        ///checkBlankSpace();
    }
    
    /*
    public void move(float xAmt, float yAmt)
    {
        xOffset += xAmt;
        yOffset += yAmt;
        //checkBlankSpace();
    }
*/
    
    public float getxOffset()
    {
        return xOffset;
    }
    
    public void setxOffset(float xOffset)
    {
        this.xOffset = xOffset;
    }
    
    public float getyOffset()
    {
        return yOffset;
    }
    
    public void setyOffset(float yOffset)
    {
        this.yOffset = yOffset;
    }
    
    
}
