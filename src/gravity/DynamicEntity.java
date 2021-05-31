package gravity;

import java.awt.Point;
import java.util.ArrayList;

public abstract class DynamicEntity extends Entity
{
    
    public static final int DEFAULT_CREATURE_WIDTH = 64,
                            DEFAULT_CREATURE_HEIGHT = 64;
    
    
    protected float xMove, yMove;
    
    
    public DynamicEntity(Handler handler, float x, float y, int width, int height, boolean gravity) 
    {
        super(handler, x, y, width, height, gravity);

        xMove = 0;
        yMove = 0;
        
    }
    
    public void move()
    {
        if (affectedByGravity)
        {
            Object[] array = checkEntityCollisions(currentXVelocity, currentYVelocity);
            boolean collided = (boolean) array[0];
            if (!collided)
            {
                ArrayList<Entity> list = checkGravityRange(currentXVelocity, currentYVelocity);
                if (list != null)
                {
                    gravXAccel = 0;
                    gravYAccel = 0;
                    for (Entity e : list) 
                    {
                        float dToCentSqr = (float) distanceTo(this, e);
                        float gGS = getGravityStrength(e, 0, 0);
                        
                        float eXCenter = e.x + (e.width / 2);
                        float eYCenter = e.y + (e.height / 2);
                        
                        float xCenter = x + (width/2);
                        float yCenter = y + (height/2);
                        
                        float dToX = (float) Math.sqrt(Math.abs((dToCentSqr) - ((yCenter - eYCenter)*(yCenter - eYCenter))));
                        float dToY = (float) Math.sqrt(Math.abs((dToCentSqr) - ((xCenter - eXCenter)*(xCenter - eXCenter))));
                        float xPull = (dToX) / (e.width / 2) * gGS;
                        float yPull = (dToY) / (e.height / 2) * gGS;
                        if (isPlayer)
                        {
                            handler.getWorld().getEntityManager().getPlayer().setGravityStrength(gGS);
                            //System.out.println(gGS);
                        }
                        
                        if(xCenter > eXCenter)
                        {
                            //x = x - (xPull);
                            gravXAccel -= xPull;
                        }
                        else if(xCenter < eXCenter)
                        {
                            //x = x + (xPull);
                            gravXAccel += xPull;
                        }
                        if(yCenter > eYCenter)
                        {
                            //y = y - (yPull);
                            gravYAccel -= yPull;
                        }
                        else if(yCenter < eYCenter)
                        {
                           //y = y + (yPull);
                            gravYAccel += yPull;
                        }
                    }
                }
            }
            
            else //Do some asteroid collision logic here pls
            {
                handleCollisions(array[1]);
            }
            if (isPlayer)
            {
                if (!(boolean)handler.getPlayer().getLanded()[0])
                {
                    currentXVelocity += gravXAccel; 
                    currentYVelocity += gravYAccel;
                }
            }
            else
            {
                currentXVelocity += gravXAccel; 
                currentYVelocity += gravYAccel;
            }
            
            double xMax = 2;
            double yMax = 2;
            if (isPlayer)
            {
                xMax = handler.getPlayer().getMAX_SPEED()*Math.cos(intendedDirectionInDegrees * .0175);
                yMax = handler.getPlayer().getMAX_SPEED()*Math.sin(intendedDirectionInDegrees * .0175);
            }
            if (isEnemy)
            {
                Enemy enemy = (Enemy) this;
                xMax = enemy.MAX_SPEED*Math.cos(intendedDirectionInDegrees * .0175);
                yMax = enemy.MAX_SPEED*Math.sin(intendedDirectionInDegrees * .0175);
            }
            if ((currentXVelocity < 0) && (currentXVelocity < xMax))
            {
                if (!(inputXAccel < 0))
                {
                    currentXVelocity += inputXAccel;
                }
            }
            else if ((currentXVelocity > 0) && (currentXVelocity > xMax))
            {
                if (!(inputXAccel > 0))
                {
                    currentXVelocity += inputXAccel;
                }
            }
            else
            {
                currentXVelocity += inputXAccel;
            }
            
            
            if ((currentYVelocity < 0) && (currentYVelocity < yMax))
            {
                if (!(inputYAccel < 0))
                {
                    currentYVelocity += inputYAccel;
                }
            }
            else if ((currentYVelocity > 0) && (currentYVelocity > yMax))
            {
                if (!(inputYAccel > 0))
                {
                    currentYVelocity += inputYAccel;
                }
            }
            else
            {
                currentYVelocity += inputYAccel;
            }
            
            x += currentXVelocity; //Apply final movement
            y += currentYVelocity; //Ditto
            
            if (isPlayer && (boolean)handler.getPlayer().getLanded()[0])
            {
                if (!speedTakeOff())
                {
                    handler.getPlayer().setLanded(false, null);
                }
            }
        }
    }
    
    /* // Making whole tiles solid, (Not neeeded, kept for reference)
    protected boolean collisionWithTile(int x, int y)
    {
        return handler.getWorld().getTile(x, y).isSolid();
    }
    */
    public void handleCollisions(Object e)
    {
        double collisionX, collisionY;
        double XVelocity, YVelocity;
        Entity B = (Entity) e;
        if (B.isBlackHole)
        {
            if (isPlayer)
            {
                damaged(health, B, 0, 0);
            }
                    
            else 
                active = false;
        }
        double xDist = getCenterX() - B.getCenterX();
        double yDist = getCenterY() - B.getCenterY();
        double distSquared = xDist*xDist + yDist*yDist;
        if(distSquared <= (radius + B.radius)*(radius + B.radius))
        {
             XVelocity = B.currentXVelocity - currentXVelocity;
            YVelocity = B.currentYVelocity - currentYVelocity;
            double dotProduct = xDist*XVelocity + yDist*YVelocity;

            if (dotProduct > 0)
            {
                double collisionScale = dotProduct / distSquared;
                double xCollision = xDist * collisionScale;
                double yCollision = yDist * collisionScale;
                //The Collision vector is the speed difference projected on the Dist vector,
                //thus it is the component of the speed difference needed for the collision.
                double combinedMass = mass + B.mass;
                double collisionWeightA = 2 * B.mass / combinedMass;
                double collisionWeightB = 2 * mass / combinedMass;
                currentXVelocity += collisionWeightA * xCollision /2;
                currentYVelocity += collisionWeightA * yCollision /2 ;
                B.currentXVelocity -= collisionWeightB * xCollision /2;
                B.currentYVelocity -= collisionWeightB * yCollision /2;
                
                collisionX = getCenterX();
                collisionY = getCenterY();

               
                
                if (!isPlanet)
                {
                    damaged(B.currentSize*XVelocity*YVelocity, B, (float)collisionX, (float)collisionY);
                }
                if (!B.isPlanet)
                {
                    B.damaged(currentSize*XVelocity*YVelocity, this, (float)collisionX, (float)collisionY);
                }
                else
                { //means you hit a planet
                    if (isPlayer)
                    {
                        if (checkPlayerLanding(B) /*speedLandable()*/)
                        {
                            if ((boolean)handler.getPlayer().getLanded()[0] == false)
                            {
                                float d1 = B.getCenterX() - getCenterX();
                                float d2 = B.getCenterY() - getCenterY();
                                double angleInRad = Math.atan(d2/d1);
                                if (getCenterX() < B.getCenterX())
                                {
                                    angleInRad += Math.PI;
                                }
                                intendedDirectionInDegrees = (float)Math.toDegrees(angleInRad);
                            }
                            handler.getPlayer().setLanded(true, B);
                            currentXVelocity = 0;
                            currentYVelocity = 0;
                        }
                        else
                        {
                            damaged(health, B, (float)collisionX, (float)collisionY);
                        }
                        
                    }
                    else
                    {
                        damaged(health, B, (float)collisionX, (float)collisionY);
                    }
                }
            }
        }
    }
    
    public boolean checkPlayerLanding(Entity planet)
    {
        Point p1 = new Point((int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees - 24)) * - 30)), (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees - 24)) * - 30)));
        Point p2 = new Point((int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees + 24)) * - 30)), (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees + 24)) * - 30)));
        
        float distanceToX1 = planet.getCenterX() - p1.x;
        float distanceToY1 = planet.getCenterY() - p1.y;
        double distSqr = (distanceToX1*distanceToX1) + (distanceToY1*distanceToY1);
        
        
        float distanceToX2 = planet.getCenterX() - p2.x;
        float distanceToY2 = planet.getCenterY() - p2.y;
        double distSqr2 = (distanceToX2*distanceToX2) + (distanceToY2*distanceToY2);
        
        if (distSqr < planet.radius*planet.radius && distSqr2 < planet.radius*planet.radius)
        {
            return true;
        }
        return false;
    }
    
    /*
    public boolean speedLandable()
    {
        double xMax = Math.abs(handler.getPlayer().getMAX_SPEED()*.75*Math.cos(intendedDirectionInDegrees * .0175));
        double yMax = Math.abs(handler.getPlayer().getMAX_SPEED()*.75*Math.sin(intendedDirectionInDegrees * .0175));
        double currentXV = Math.abs(currentXVelocity);
        double currentYV = Math.abs(currentYVelocity);
        
        if (currentXV < xMax && currentYV < yMax)
        {
            return true;
        }
        return false;
    }
*/
    
    public boolean speedTakeOff()
    {
        double xMax = Math.abs(handler.getPlayer().getMAX_SPEED()*.75*Math.cos(intendedDirectionInDegrees * .0175));
        double yMax = Math.abs(handler.getPlayer().getMAX_SPEED()*.75*Math.sin(intendedDirectionInDegrees * .0175));
        double currentXV = Math.abs(currentXVelocity);
        double currentYV = Math.abs(currentYVelocity);
        
        if (currentXV < xMax && currentYV < yMax)
        {
            return true;
        }
        return false;
    }
    
    
    //Getters and Setters
    public float getxMove()
    {
        return xMove;
    }
    
    public void setxMove(float xMove)
    {
        this.xMove = xMove;
    }
    
    public float getyMove()
    {
        return yMove;
    }
    
    public void setyMove(float yMove)
    {
        this.yMove = yMove;
    }
    
}
