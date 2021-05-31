package gravity;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public abstract class Entity 
{
    protected boolean active = true;
    protected boolean onRender = false;
    protected boolean onTick = false;
    
    protected Handler handler;
    protected float x, y;
    protected int width, height;
    protected int radius;
    protected boolean gravity;
    protected boolean affectedByGravity = true;
    protected Rectangle[] bounds;
    protected double currentSize = 1;
    
    protected float distanceToPlayerSqr;
    protected float currentXVelocity;
    protected float currentYVelocity;
    protected float density;
    protected float mass = 1;
    
    protected float gravXAccel;
    protected float gravYAccel;
    protected float inputXAccel;
    protected float inputYAccel;
    protected float intendedDirectionInDegrees = 135;
    protected float acceleration;
    
    protected int gRangeWidth, gRangeHeight, gRadius, gModifier, gRadMod;
    protected float G = (float) 0.00000000006674;
    
    protected double health;
    public static final int UNDEFINED_HEALTH = 10000;
    public static final int PLAYER_DEFAULT_HEALTH = 100;
    
    boolean isPlayer = false;
    boolean isEnemy = false;
    boolean isPlanet = false;
    boolean isBlackHole = false;
    boolean isWormHole = false; // need to add
    boolean isAsteroid = false;
    boolean isNebula = false;
    boolean isEarth = false;
    boolean isAsteroidField = false;
    boolean beenNear = false;
    
    boolean hasHitBox = true;
    
    Entity diedTo = null;
    
    boolean invincible = false;
    
    
    public Entity(Handler handler, float x, float y, int width, int height, boolean gravity)
    {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.radius = width/2;
        this.gravity = gravity;
        
        this.gModifier = 4;
        this.gRangeWidth = (width*gModifier);
        this.gRangeHeight = (height*gModifier);
        this.gRadMod = gModifier - 1;
        this.gRadius = ((width/2)*gRadMod);
        
        health = UNDEFINED_HEALTH;
        
        bounds = new Rectangle[2];
        bounds[0] = new Rectangle(0, 0, width, height);
    }
    
    public abstract void tick();
    
    public abstract void render(Graphics g);
    
    public abstract void die();
    
    public void damaged(double amt, Entity e, float x, float y)
    {
        if (invincible)
        {
        }
        
        else
        {
            health -=Math.abs(amt);
            if (health <= 0)
            {
                if (!isPlayer)
                    active = false;
                diedTo = e;
                die();
            }
        }
    }
    
    //CIRCLE TO CIRCLE COLLISION
    //(COPIED SOME GUYS FORMULA http://stackoverflow.com/questions/1736734/circle-circle-collision)
    public Object[] checkEntityCollisions(float xVel, float yVel)
    {
        Object[] array = new Object[2];
        for(Entity e : handler.getWorld().getEntityManager().getEntities())
        {
            if (e.equals(this) || e.hasHitBox == false)
            {
                continue;
            }
            Shape[] curE = getCollisionBounds(xVel, yVel);
            for (int c = 0; c < curE.length; c++)
            {
                if (curE[c] != null)
                {
                    double dank = Math.pow((e.x+e.bounds[0].x+(e.bounds[0].width/2)) - (curE[0].getBounds().x+(curE[0].getBounds().width/2)), 2);
                    double meme = Math.pow((curE[0].getBounds().y+(curE[0].getBounds().height/2) - (e.y+e.bounds[0].y+(e.bounds[0].height/2))), 2);
                    double dankest = Math.pow((curE[0].getBounds().height/2) +(e.bounds[0].width/2), 2);
                    if ((dank + meme) <= (dankest))
                    {
                        array[0] = true;
                        array[1] = e;
                        return array;
                    } 
                }
            }
        }
        array[0] = false;
        array[1] = null;
        return array;
    }
    
    
    public Rectangle[] getCollisionBounds(float xOffset, float yOffset)
    {
        Rectangle[] shapes = new Rectangle[2];
        for (int c = 0; c < bounds.length; c++)
        {
            if (bounds[c] != null)
            {
                shapes[c] = new Rectangle((int)(x + bounds[c].x - xOffset), (int) (y+ bounds[c].y - yOffset), bounds[c].width, bounds[c].height);
            }
        }
        return shapes;
    }
    
    /*
    public Shape getPlanetGravityBounds(float xOffset, float yOffset)
    {
        Ellipse2D Circle = new Ellipse2D.Double((int) (x + bounds.x - xOffset)-(gRadius), 
                    (int) (y + bounds.y - yOffset)-(gRadius),
                    gRangeWidth, gRangeHeight);
        return Circle;
    }
    */
    
    public ArrayList<Entity> checkGravityRange(float xOffset, float yOffset)
    {
        ArrayList<Entity> list = new ArrayList<Entity>();
        for(Entity e : handler.getWorld().getEntityManager().getEntities())
        {
            if (e.gravity == true)
            {
                if (e.equals(this))
                {
                    continue;
                }
            
                /*
                if (e.getPlanetGravityBounds(0f, 0f).intersects(getCollisionBounds(xOffset, yOffset)))
                {
                    list.add(e);
                } 
                        */
                list.add(e);
            }
        }
        if (list.isEmpty())
            return null;
        else
            return list;
    }
    
    public void checkNearPlayer()
    {
        Entity player = handler.getPlayer();
        findNewClosestObject();
        double nearByAcceptanceSqr = handler.getScreenHeight()*.75*handler.getScreenHeight()*.75;
        
        for (Entity e : handler.getWorld().getEntityManager().getEntities())
        {
            e.distanceToPlayerSqr = distanceTo(player, e);
            double radiusSqr = e.radius * e.radius;
            
            if (e.isAsteroid) //Asteroid Check
            {
                if (e.distanceToPlayerSqr < nearByAcceptanceSqr)
                {
                    handler.getPlayer().setNearAsteroid(true, e);
                }
            }
            
            else if(e.isPlanet) //Planet Check
            {
                if (e.isEarth && e.distanceToPlayerSqr < nearByAcceptanceSqr+radiusSqr)
                {
                    handler.getPlayer().setNearEarth(true);
                }
                
                if (e.distanceToPlayerSqr < nearByAcceptanceSqr+radiusSqr)
                {
                    handler.getPlayer().setNearPlanet(true, e);
                    if (!e.beenNear && ((Planet)(handler.getPlayer().getLanded()[1]) == e))
                    {
                        handler.getPlayer().addPlanetSeen();
                        e.beenNear = true;
                    }
                }
            }
            
            else if(e.isBlackHole) //Black Hole Check
            {
                if (e.distanceToPlayerSqr < nearByAcceptanceSqr + radiusSqr)
                {
                    handler.getPlayer().setNearBlackHole(true, e);
                    e. beenNear = true;
                }
            }
            
            else if(e.isNebula) //Nebula Check
            {
                if (e.distanceToPlayerSqr < nearByAcceptanceSqr+radiusSqr)
                {
                    handler.getPlayer().setNearNebula(true, e);
                    if (!e.beenNear)
                    {
                        handler.getPlayer().addNebulaSeen();
                        e.beenNear = true;
                    }
                }
            }
            
            else if (e.isAsteroidField)
            {
                if (e.distanceToPlayerSqr < radiusSqr)
                {
                    handler.getPlayer().setNearAsteroidField(true, e);
                    if (!e.beenNear)
                    {
                        e.beenNear = true;
                    }
                }
            }
        }
    }
    public void findNewClosestObject()
    {
        ArrayList<Entity> objectsOfInterest = new ArrayList<Entity>();
        for (Entity e : handler.getWorld().getEntityManager().getEntities())
        {
            if ((e.isBlackHole || e.isPlanet || e.isNebula) && !e.beenNear)
            {
                if(objectsOfInterest.isEmpty())
                {
                    objectsOfInterest.add(e);
                }
                else if(e.distanceToPlayerSqr < objectsOfInterest.get(0).distanceToPlayerSqr)
                {
                    objectsOfInterest.add(0, e);
                }
                else
                {
                    objectsOfInterest.add(e);
                }
            }
        }
        handler.getPlayer().setObjectsOfInterest(objectsOfInterest);
    }
    //|
    //v
    
    public float getGravityStrength(Entity e, float xOffset, float yOffset)
    {
        float massMultiple = mass * e.mass;
        float distSq = (float) distanceTo(this, e);
        return G*(massMultiple/distSq);
    }
    
    public float distanceTo(Entity a, Entity b)
    {
        float distanceToX = Math.abs(a.getCenterX() - b.getCenterX());
        float distanceToY = Math.abs(a.getCenterY() - b.getCenterY());
        float HypSquared = (distanceToX*distanceToX) + (distanceToY*distanceToY);
        return HypSquared;
    }
    
    public float getCenterX()
    {
        return (x + (width/2));
    }
    
    public float getCenterY()
    {
        return (y + (height/2));
    }
    //Getters and Setters
    public float getX()
    {
        return x;
    }
    
    public void setX(float x)
    {
        this.x = x;
    }
    
    public float getY()
    {
        return y;
    }
    
    public void setY(float y)
    {
        this.y = y;
    }
    
    public float getWidth()
    {
        return width;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public float getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public double getHealth()
    {
        return health;
    }
    
    public void setHealth(int health)
    {
        this.health = health;
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    public boolean getOnTick()
    {
        return onTick;
    }
    
    public void setOnTick(boolean TF)
    {
        onTick = TF;
    }
    
    public boolean getOnRender()
    {
        return onRender;
    }
    
    public void setOnRender(boolean TF)
    {
        onRender = TF;
    }
}
