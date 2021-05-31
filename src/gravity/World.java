package gravity;

import java.awt.Graphics;
import java.io.IOException;
import java.util.Scanner;

public class World 
{
    private Handler handler;
    private int width, height;
    private float MAPSCALE = .5f;
    private float PLANETSCALE = .25f/2;
    private int spawnX = (int) (7100*MAPSCALE);
    private int spawnY = (int) (7100*MAPSCALE);
    
    //Entities
    private Player player;
    private EntityManager entityManager;
    private ParticleManager particleManager;
    private AttackManager attackManager;
    
    //Item
    private ItemManager itemManager;
    
    //Inventory
    private Inventory inventory;
    
    //Storyline
    //private Storyline storyline;
    
    
    public World(Handler handler, String path) throws IOException
    {
        this.handler = handler;
        
        player = new Player(handler, spawnX, spawnY, false); //<remove this and undo // below if fails
        
        //entityManager = new EntityManager(handler, new Player(handler, 7100*MAPSCALE, 7100*MAPSCALE, false));//7750
        entityManager = new EntityManager(handler, player);
        particleManager = new ParticleManager(handler, player);
        attackManager = new AttackManager(handler, player);
        
        itemManager = new ItemManager(handler);
        inventory = new Inventory(handler);
        //storyline = new Storyline(handler);
        //mouseManager = new MouseManager();
        
        //USER DEFINED ASTEROIDFIELDS
        entityManager.addEntity(new AsteroidField(handler, 0*MAPSCALE, 0*MAPSCALE, 4000, 4000));
        
        //USER DEFINED NEBULAS
        entityManager.addEntity(new Nebula(handler, Assets.nebula3, 12000*MAPSCALE, 28000*MAPSCALE, (int) (400*PLANETSCALE*2)));
        entityManager.addEntity(new Nebula(handler, Assets.nebula1, 60000*MAPSCALE, 88000*MAPSCALE, (int) (300*PLANETSCALE*2)));
        entityManager.addEntity(new Nebula(handler, Assets.nebula2, 56000*MAPSCALE, 12000*MAPSCALE, (int) (350*PLANETSCALE*2)));
        
        
        //PLANETS//////////////////////////PLANET IMAGE///////////////XCord///////////YCord//////////////Size///////////Grav//rotR8/Density/Earth?///
        entityManager.addEntity(new Planet(Assets.planet6 ,handler, 5000*MAPSCALE, 5000*MAPSCALE, (int) (2560*PLANETSCALE), true, .02, 6.66 ,false)); //Starting Planet .02
        entityManager.addEntity(new Planet(Assets.planet2, handler, 12000*MAPSCALE, 20000*MAPSCALE, (int) (3390*PLANETSCALE), true, .03, 3.933, false)); //Mars //smallest = 2440, largest = 69911 irl radius
        entityManager.addEntity(new Planet(Assets.planet3, handler, 36000*MAPSCALE, 20000*MAPSCALE, (int) (12000*PLANETSCALE), true, .04, 1.326, false)); //Jupiter//low = .7g/m largest = 10g/m, earth = 5.51g/m
        entityManager.addEntity(new Planet(Assets.planet4, handler, 72000*MAPSCALE, 40000*MAPSCALE, (int) (9000*PLANETSCALE), true, .03, 1.638, false)); //neptune
        entityManager.addEntity(new Planet(Assets.planet5, handler, 40000*MAPSCALE, 64000*MAPSCALE, (int) (10500*PLANETSCALE), true, .02, 0.687, false)); //Saturn
        entityManager.addEntity(new Planet(Assets.planet7, handler, 80000*MAPSCALE, 68000*MAPSCALE, (int) (3840*PLANETSCALE), true, .03, 3.14, false));
        entityManager.addEntity(new Planet(Assets.planet8, handler, 72000*MAPSCALE, 80000*MAPSCALE, (int) (5000*PLANETSCALE), true, .03, 2.69, false));
        entityManager.addEntity(new Planet(Assets.planet10, handler, 95000*MAPSCALE, 95000*MAPSCALE, (int) (6371*PLANETSCALE), true, .03, 5.51, true)); //dead earth
        
        entityManager.addEntity(new BlackHole(handler, 50000*MAPSCALE, 50000*MAPSCALE, (int) (100*PLANETSCALE), true, .05));
        
        //Starting Items
        int centX = (int) (entityManager.getEntities().get(4).getCenterX());
        int centY = (int) (entityManager.getEntities().get(4).getCenterY());
        int min = (int) (entityManager.getEntities().get(4).radius*1.2);
        int max = (int)(min * 1.8);
        double angle, radius;
        
        for (int c = 0; c < 10; c++)
        {
            angle = handler.random(0, 3590)/10 *.0175;
            radius = handler.random(min, max);
            itemManager.addItem(Item.siliconItem.createNew((int)(centX + Math.cos(angle) * radius), (int)(centY + Math.sin(angle) * radius)));
        }
        for (int c = 0; c < 15; c++)
        {
            angle = handler.random(0, 3590)/10 *.0175;
            radius = handler.random(min, max);
            itemManager.addItem(Item.carbonItem.createNew((int)(centX + Math.cos(angle) * radius), (int)(centY + Math.sin(angle) * radius)));
        }
        for (int c = 0; c < 12; c++)
        {
            angle = handler.random(0, 3590)/10 *.0175;
            radius = handler.random(min, max);
            itemManager.addItem(Item.aluminumItem.createNew((int)(centX + Math.cos(angle) * radius), (int)(centY + Math.sin(angle) * radius)));
        }

    }
    
    public void tick()
    {
        if (handler.getPlayer().isPlayerDead())
        {
            handler.getKeyManager().inventory = false;
        }
        if (handler.getKeyManager().inventory == false)
        {
            itemManager.tick();
            entityManager.tick();
            attackManager.tick();
            particleManager.tick();
        }
        inventory.tick();
        //storyline.tick();
    }
    
    public void render(Graphics g)
    {
        int xStart = (int) (handler.getGameCamera().getxOffset() / Tile.TILE_WIDTH )-1;
        int xEnd = (int) (handler.getGameCamera().getxOffset() + handler.getScreenWidth()) / Tile.TILE_WIDTH + 1;
        int yStart = (int) (handler.getGameCamera().getyOffset() / Tile.TILE_HEIGHT )-1;
        int yEnd = (int) (handler.getGameCamera().getyOffset() + handler.getScreenHeight()) / Tile.TILE_HEIGHT + 1;
        
        for (int y = yStart; y < yEnd; y++)
        {
            for (int x = xStart; x < xEnd; x++)
            {
                //System.out.println(xStart + " - " + xEnd + ", " + yStart + " - " + yEnd );
                Tile.tileStandard.render(g, (int) (x * Tile.TILE_WIDTH - handler.getGameCamera().getxOffset())
                                      , (int) (y * Tile.TILE_WIDTH - handler.getGameCamera().getyOffset()));
            }
        }
        
        if (!handler.getInIntro() && !handler.getInOutro())
        {
            entityManager.renderNebulas(g);
            
            entityManager.renderAsteroidFields(g);

            itemManager.render(g);
            
            entityManager.render(g);
            
            particleManager.render(g);
            
            entityManager.renderPlanets(g);
            
            attackManager.render(g);
            
            entityManager.renderPlayer(g);

            inventory.render(g);
        }
        //storyline.render(g);
        
    }

    
    /*
    public Tile getTile(int x, int y)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
        {
            return Tile.tileStandard;
        }
        
        Tile t = Tile.tiles[tiles[x][y]];
        if(t == null) //if the id doesnt exist, use default
        {
            return Tile.tileStandard;
        }
        return t;
    }\
    */
    
    /*
    private void loadWorld(String path) throws IOException
    {
        String file = Utils.loadFileAsString(path);
        String[] tokens = file.split("\\s+");
        width = Utils.parseInt(tokens[0]); //very first number in text file
        height = Utils.parseInt(tokens[1]); //second number
        
        handler.setWorldSize(width);
        
        spawnX = Utils.parseInt(tokens[2]);
        spawnY = Utils.parseInt(tokens[3]);
        
        tiles = new int[width][height];
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                tiles[x][y] = Utils.parseInt(tokens[(x + y * width) + 4]); //add 4 because first 4 tokens are variables
            }
        }
    }
    */
    
    
    //Getters and Setters
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public int getSpawnX()
    {
        return spawnX;
    }
    
    public int getSpawnY()
    {
        return spawnY;
    }
    
    public ParticleManager getParticleManager()
    {
        return particleManager;
    }
    
    public EntityManager getEntityManager()
    {
        return entityManager;
    }
    
    public AttackManager getAttackManager()
    {
        return attackManager;
    }
    
    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public void setItemManager(ItemManager itemManager) {
        this.itemManager = itemManager;
    }
    
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public float getMapScale()
    {
        return MAPSCALE;
    }
}
