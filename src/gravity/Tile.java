package gravity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tile 
{
    //STATIC STUFF HERE
    
    public static Tile[] tiles = new Tile[256];
    public static Tile tileStandard = new TileStandard(0); //New ID for every tile
    //CLASS
    
    public static final int TILE_WIDTH = 1024,
                            TILE_HEIGHT = 1024;
    
    protected BufferedImage texture;
    protected final int id;
    
    public Tile(BufferedImage texture, int id)
    {
        this.texture = texture;
        this.id = id;
        
        tiles[id] = this;
    }
    
    public void tick()
    {
        
    }
    
    public void render(Graphics g, int x, int y)
    {
        g.drawImage(texture, x, y, TILE_WIDTH, TILE_HEIGHT, null);
    }
    
    public boolean isNebula() //default setting fot ALL tiles, to change, add method in tile class with @Override
    {
        return false;
    }
    
    public int getID()
    {
        return id;
    }
}
