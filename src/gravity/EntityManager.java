package gravity;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class EntityManager extends Manager
{
    protected Handler handler;
    protected Player player;
    protected ArrayList<Entity> entities;

    //AsteroidManager
    protected int numberOfAsteroids = 0;
    protected int desiredNumberOfAsteroids = 25;//30
    
    //Enemy Manager
    protected int numberOfEnemies = 0;
    protected int maxNumberOfEnemies = 6;
    
        //Nebula class enemies
    protected long lastNebulaSwarmerSpawn = 0;
    protected int numberOfNebulaSwarmers = 0;
    protected int maxNumberOfNebulaSwarmers = 12;
    
    protected long lastNebulaSniperSpawn = 0;
    protected int numberOfNebulaSnipers = 0;
    protected int maxNumberOfNebulaSnipers = 4;
    
    private int currentAsset = 0;
    
    public EntityManager(Handler handler, Player player)
    {
        super(handler, player);
        this.handler = handler;
        this.player = player;
        
        entities = new ArrayList<Entity>();
        addEntity(player);
    }
    
    public void tick()
    {            
        numberOfAsteroids = 0;
        numberOfEnemies = 0;
        numberOfNebulaSwarmers = 0;
        numberOfNebulaSnipers = 0;
        
        Iterator<Entity> it = entities.iterator();
        while(it.hasNext())
        {
            Entity e = it.next();
            if (!e.isActive() && !e.isPlayer)
            {
                it.remove();
                continue;
            }
            boolean[] array = updateScreenBoundaries(e.getCenterX(), e.getCenterY(), e.radius);
            e.setOnTick(array[0]);
            e.setOnRender(array[1]);
            
            if (e.onTick)// || e.isBlackHole || e.isPlanet)
            {
                e.tick();
            }
            else if (e.isAsteroid || e.isEnemy)
            {
                e.setActive(false);
                it.remove();
                continue;
            }
            
            if (e.isAsteroid)
            {
                numberOfAsteroids++;
            }
            
            if (e.isEnemy)
            {
                numberOfEnemies++;
                Enemy enemy = (Enemy)(e);
                if (enemy.isNebulaSwarmer)
                    numberOfNebulaSwarmers++;
                else if (enemy.isNebulaSniper)
                    numberOfNebulaSnipers++;
            }
        }
        
        tickAsteroidManager();
        tickEnemyManager();
        
    }
    
    private void tickAsteroidManager()
    {
        if ((boolean)handler.getPlayer().isNearAsteroidField()[0])
        {
            desiredNumberOfAsteroids = 50;
        }
        else
        {
            desiredNumberOfAsteroids = 25;
        }
        
        if (numberOfAsteroids < desiredNumberOfAsteroids)
        {
            int randomSize = handler.random(1, 5);
            int sizeOffset = randomSize*32;
            int randomX = getRandomX(sizeOffset);
            int randomY = getRandomY(randomX, sizeOffset);
            double directionInRadians = getRandomDirectionInRadians(randomX, randomY);
            
            addEntity(new Asteroid(handler, randomX, randomY, randomSize, directionInRadians));
        }
    }
    
    private void tickEnemyManager()
    {
        long currentTime = System.currentTimeMillis();
        if (numberOfEnemies < maxNumberOfEnemies)
        {
            if ((boolean)handler.getPlayer().isNearNebula()[0]) //Nebula themed enemies
            {
                if (numberOfNebulaSwarmers < maxNumberOfNebulaSwarmers)
                {
                    if (currentTime - lastNebulaSwarmerSpawn > 4000)
                    {
                        int sizeOffset = 64;
                        int randomX = getRandomX(sizeOffset);
                        int randomY = getRandomY(randomX, sizeOffset);

                        addEntity(new NebulaSwarmer(handler, randomX, randomY, true));
                        lastNebulaSwarmerSpawn = currentTime;
                    }
                }
                
                if (handler.getPlayer().getShipLevel() >= 3 && numberOfNebulaSnipers < maxNumberOfNebulaSnipers)
                {
                    if (currentTime - lastNebulaSniperSpawn > 8000)
                    {
                        int sizeOffset = 64;
                        int randomX = getRandomX(sizeOffset);
                        int randomY = getRandomY(randomX, sizeOffset);

                        addEntity(new NebulaSniper(handler, randomX, randomY, false));
                        lastNebulaSniperSpawn = currentTime;
                    }
                }
                
            }
        }
    }
    
    public void render(Graphics g)
    {
    
        for (Entity e : entities) //Renders all entities
        {
            if (e.isPlayer || e.isNebula || e.isPlanet || e.isAsteroidField)
            {
                continue;
            }
            else if (e.getOnRender()) //Makes it not render entities off screen
            {
                e.render(g);
            }
        }
    }
    
    public void load(Graphics g)
    {
        if (currentAsset < entities.size())
        {
            entities.get(currentAsset).render(g);
            currentAsset++;
        }
        else
        {
            handler.getGameState().setLoaded(true);
        }
    }
    
    public void renderNebulas(Graphics g)
    {
        for (Entity e : entities) //Renders all entities
        {
            if (e.isNebula)
            {
                e.render(g);
            }
        }
    }
    
    public void renderAsteroidFields(Graphics g)
    {
        for (Entity e : entities)
        {
            if (e.isAsteroidField)
            {
                e.render(g);
            }
        }
    }
    
    public void renderPlanets(Graphics g)
    {
        for (Entity e : entities) //Renders all entities
        {
            if (e.isPlanet)
            {
                e.render(g);
            }
        }
    }
    
    
    public void renderPlayer(Graphics g)
    {
        entities.get(0).render(g);
    }
   
    
    //AddEntity
    public void addEntity(Entity e)
    {
        entities.add(e);
    }
    
    //Getters and Setters
    public Handler getHandler()
    {
        return handler;
    }
    
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public void setPlayer(Player player)
    {
        this.player = player;
    }
    
    public ArrayList<Entity> getEntities()
    {
        return entities;
    }
    
    public void setEntities(ArrayList<Entity> entities)
    {
        this.entities = entities;
    }
    
    public ArrayList<Entity> getEnemies()
    {
        ArrayList<Entity> memes = new ArrayList<Entity>();
        for (Entity e : entities)
        {
            if (e.isEnemy)
            {
                memes.add(e);
            }
        }
        return memes;
    }
    
}
