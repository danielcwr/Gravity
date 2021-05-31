package gravity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Player extends DynamicEntity
{
    //Animations
    private Animation animationDead;
    
    //Mouse
    private float mouseX = 0;
    private float mouseY = 0;
    
    //Traktor Beam
    private boolean autoTractorBeam = true;
    private float tractorBeamAccel = .06f;
    private float autoTractorMaxDist = 75;
    private float tractorMaxDist = 200;
    
    //Laser
    private long lastWeaponChange = 0;
    private int selectedWeapon = 0; //0 = mining laser, 1 = plasma blaster, 2 = combat laser, 3 = rockets
    private boolean hasPlasmaBlaster = false;
    private boolean hasCombatLaser = false;
    private boolean hasRockets = false;
    
    private boolean LorR = false;
    public final long weaponMaxCharge = 5000;
    private long lastShotFired = 500; //any number above 250
    
    //Attack timer
    private long lastAttackTimer, attackTimer;//1000 = 1 second
    private long afterBurnerTimer = 0, lastAfterBurnerAdd = 0;
    
    //UPGRADABLE STATS!!!! (FUTURE IMPLEMENTATION)
    private int SHOT_DMG = 10;
    private int ATTACK_COOLDOWN = 1000;
    private double MAX_SPEED = 4;
    private float MAX_ACCELERATION = .02f;
    private double TURNING_SPEED = 1.2;
    private int MAX_HEALTH = PLAYER_DEFAULT_HEALTH;
    
    private BufferedImage shipImage;
    private int shipLevel = 0;//0 = stock, 1 = advanced hull, 2 = engines, 3 = guns, 4 = full
    
    private boolean dead = false;
    private float gravityStrength;
    
    //STORYLINE STUFF
    private Object[] nearAsteroid = {false, null};
    private Object[] nearPlanet = {false, null};
    private Object[] nearNebula = {false, null};
    private Object[] nearBlackHole= {false, null};
    private Object[] nearWormhole = {false, null};
    private Object[] nearAsteroidField = {false, null};
    private boolean nearEarth = false;
    private ArrayList<Entity> objectsOfInterest = null;
    
    //Respawn Stuff
    private boolean respawnText = false;
    private long invincibleTime;
    private long invincibleTimeLeft = 0;
    private long invincibleTimeStart = 0;
    private double respawnClockStart = 0;
    private BufferedImage wormHole = Assets.wormhole;
    private double wormHoleRotation = 0;
        //Respawn Statistic stuff
    private int numOfNebulasSeen = 0;
    private int numOfPlanetsSeen = 0;
    private int numOfEnemiesDestroyed = 0;
    private int numOfAsteroidsDestroyed = 0;
    private int numOfMaterialsCollected = 0;
    
    private long asteroidsMiniGameScore = 0;
    
    //Landing on planet stuff
    private Object[] landed;
            
    public Player(Handler handler, float x, float y, boolean gravity) {
        super(handler, x, y, DEFAULT_CREATURE_WIDTH, DEFAULT_CREATURE_HEIGHT, gravity);
        super.mass = 300;
        
        bounds[0].x = 18;//25
        bounds[0].y = 18;//10
        bounds[0].width = 28;//14
        bounds[0].height = 28;//43
        radius = 14;
        health = MAX_HEALTH/2; //USE damaged() method to change health
        
        shipLevel0();//0
        
        animationDead = new Animation(125, Assets.deathAnimation, 3, false);
        
        isPlayer = true;
        
        landed = new Object[]{false, null};
        
        setInvincible(true, 20000);
        
        //inventory = new Inventory(handler);
    }
    
    public void playerRespawn()
    {
        
        float distanceToX = (getCenterX() - diedTo.getCenterX());
        float distanceToY = (getCenterY() - diedTo.getCenterY());
        double angle = Math.atan(distanceToY / distanceToX);

        if (getCenterX() < diedTo.getCenterX())
        {
            angle += Math.PI;
        }
        
        intendedDirectionInDegrees = (float)Math.toDegrees(angle);
        
        x = diedTo.getCenterX() + (float)(Math.cos(angle)*diedTo.radius*1.75);
        y = diedTo.getCenterY() + (float)(Math.sin(angle)*diedTo.radius*1.75);
        
        currentXVelocity = 0;
        currentYVelocity = 0;
        
        health = MAX_HEALTH / 2;
        dead = false;
        setInvincible(true, 5000);
        wormHoleRotation = 0;
        
        landed = new Object[]{false, null};
        
        respawnClockStart = System.currentTimeMillis();
        respawnText = true;
    }
    
    @Override
    public void tick() //update variables for an object
    {
        //setInvincible(true, 5000);
        if (dead)
        {
            animationDead.tick();
        }
        else
        {
            if (invincible)
            {
                //System.out.println(invincibleTimeLeft);
                invincibleTimeLeft = invincibleTime - (System.currentTimeMillis() - invincibleTimeStart);
                if (invincibleTimeLeft < 0)
                {
                    invincible = false;
                }
            }
            //Animations
            
            if (!handler.getKeyManager().shift)
            {
                afterBurnerTimer += (System.currentTimeMillis() - lastAfterBurnerAdd)/4;
                lastAfterBurnerAdd = System.currentTimeMillis();
                if (afterBurnerTimer > 5000)
                {
                    afterBurnerTimer = 5000;
                }
            }

            //Movement
            getInput();
            if (handler.getInOutro() == false)
            {
                move();
            }
                
            handler.getGameCamera().centerOnEntity(this);
            tickTractorBeam();

            //Attack
            try 
            {
                checkAttacks();
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
            if ((boolean)landed[0] && acceleration == 0)
            {
                followPlanet();
            }
        }
        resetNearby();
        checkNearPlayer();
    }
    
    private void tickTractorBeam()
    {
        if (autoTractorBeam)
        {
            for (Item i : handler.getWorld().getItemManager().getItems())
            {
                float xDist = i.getCenterX() - getCenterX();
                float yDist = i.getCenterY() - getCenterY();
                double dist = Math.sqrt(xDist * xDist + yDist * yDist);
                if (dist < autoTractorMaxDist)
                {
                    double angleItemInRad = Math.atan(yDist/xDist);
                    double directionItem = Math.toDegrees(angleItemInRad);
                    if (getCenterX() > i.getCenterX())
                    {
                        directionItem += 180;
                    }
                    i.setXVelocity(i.getXVelocity() + (float)Math.cos(directionItem * .0175)*tractorBeamAccel*-1);
                    i.setYVelocity(i.getYVelocity() + (float)Math.sin(directionItem * .0175)*tractorBeamAccel*-1);
                    
                    int randDist = handler.random((int)(dist*.75), (int)dist);
                    if (randDist < 40)
                        randDist = 40;
                    int randDir = getRandDirection(-5, 5);
                    int rand = handler.random(3, 5);
                    double xMod = (Math.cos(Math.toRadians(directionItem + randDir)));
                    double yMod = (Math.sin(Math.toRadians(directionItem + randDir)));
                    Color color = Assets.tractorPullColors[handler.random(0, 3)];
                    for (int c = 1; c < rand; c++)
                    {
                        handler.getWorld().getParticleManager().addParticle(new Particle(handler,(int)(getCenterX()+ xMod*(randDist-c)), (int)(getCenterY() + yMod*(randDist-c)), (double)(xMod*-4), (double)(yMod*-4), color, 150));
                    }
                }
            }
        }
        else if (handler.getMouseManager().isLeftPressed()) //pull
        {
            mouseX = handler.getMouseManager().getMouseX() + handler.getGameCamera().getxOffset();
            mouseY = handler.getMouseManager().getMouseY() + handler.getGameCamera().getyOffset();
            
            float xDistance = (mouseX - getCenterX());
            float yDistance = (mouseY - getCenterY());
            double distance = Math.sqrt(xDistance* xDistance + yDistance*yDistance);
            if (distance > tractorMaxDist)
            {
                distance = tractorMaxDist;
            }
            double angleInRad = Math.atan(yDistance/xDistance);
            double direction = Math.toDegrees(angleInRad);
            if (getCenterX() > mouseX)
            {
                direction += 180;
            }
            
            int randDist = handler.random((int)distance/3, (int)distance);
            int randDir = getRandDirection(-10, 10);
            int rand = handler.random(3, 5);
            double xMod = (Math.cos(Math.toRadians(direction + randDir)));
            double yMod = (Math.sin(Math.toRadians(direction + randDir)));
            Color color = Assets.tractorPullColors[handler.random(0, 3)];
            for (int c = 1; c < rand; c++)
            {
                handler.getWorld().getParticleManager().addParticle(new Particle(handler,(int)(getCenterX()+ xMod*(randDist-c)), (int)(getCenterY() + yMod*(randDist-c)), (double)(xMod*-4), (double)(yMod*-4), color, 150));
            }
            
            for (Item i : handler.getWorld().getItemManager().getItems())
            {
                float xDist = i.getCenterX() - getCenterX();
                float yDist = i.getCenterY() - getCenterY();
                double distSqr = xDist * xDist + yDist * yDist;
                if (distSqr < distance*distance)
                {
                    double angleItemInRad = Math.atan(yDist/xDist);
                    double directionItem = Math.toDegrees(angleItemInRad);
                    if (getCenterX() > i.getCenterX())
                    {
                        directionItem += 180;
                    }
                    if(directionItem  > direction - 10 && directionItem < direction + 10)
                    {
                        i.setXVelocity(i.getXVelocity() + (float)Math.cos(directionItem * .0175)*tractorBeamAccel*-1);
                        i.setYVelocity(i.getYVelocity() + (float)Math.sin(directionItem * .0175)*tractorBeamAccel*-1);
                    }
                }
                
            }
            for (Entity e : handler.getWorld().getEntityManager().getEntities())
            {
                if (!e.gravity && !e.isPlayer && !e.isNebula && !e.isWormHole)
                {
                    float xDist = e.getCenterX() - getCenterX();
                    float yDist = e.getCenterY() - getCenterY();
                    double distSqr = xDist * xDist + yDist * yDist;
                    if (distSqr < distance*distance)
                    {
                        double angleItemInRad = Math.atan(yDist/xDist);
                        double directionItem = Math.toDegrees(angleItemInRad);
                        if (getCenterX() > e.getCenterX())
                        {
                            directionItem += 180;
                        }
                        if(directionItem  > direction - 10 && directionItem < direction + 10)
                        {
                            e.currentXVelocity = (e.currentXVelocity + (float)Math.cos(directionItem * .0175)*tractorBeamAccel*-1);
                            e.currentYVelocity = (e.currentYVelocity + (float)Math.sin(directionItem * .0175)*tractorBeamAccel*-1);
                        }
                    }
                }
                
            }
        }
        else if (handler.getMouseManager().isRightPressed())
        {
            mouseX = handler.getMouseManager().getMouseX() + handler.getGameCamera().getxOffset();
            mouseY = handler.getMouseManager().getMouseY() + handler.getGameCamera().getyOffset();
            
            float xDistance = (mouseX - getCenterX());
            float yDistance = (mouseY - getCenterY());
            double distance = Math.sqrt(xDistance* xDistance + yDistance*yDistance);
            if (distance > tractorMaxDist)
            {
                distance = tractorMaxDist;
            }
            double angleInRad = Math.atan(yDistance/xDistance);
            double direction = Math.toDegrees(angleInRad);
            if (getCenterX() > mouseX)
            {
                direction += 180;
            }
            
            int randDir = getRandDirection(-10, 10);
            int randDist = handler.random((int)0, (int)(distance*.75));
            int rand = handler.random(3, 5);
            double xMod = (Math.cos(Math.toRadians(direction + randDir)));
            double yMod = (Math.sin(Math.toRadians(direction + randDir)));
            Color color = Assets.tractorPushColors[handler.random(0, 3)];
            for (int c = 1; c < rand; c++)
            {
                handler.getWorld().getParticleManager().addParticle(new Particle(handler,(int)(getCenterX()+ xMod*(randDist-c)), (int)(getCenterY() + yMod*(randDist-c)), (double)(xMod*4), (double)(yMod*4), color, 150));
            }
            
            for (Entity e : handler.getWorld().getEntityManager().getEntities())
            {
                if (!e.gravity && !e.isPlayer && !e.isNebula && !e.isWormHole)
                {
                    float xDist = e.getCenterX() - getCenterX();
                    float yDist = e.getCenterY() - getCenterY();
                    double distSqr = xDist * xDist + yDist * yDist;
                    if (distSqr < distance*distance)
                    {
                        double angleItemInRad = Math.atan(yDist/xDist);
                        double directionItem = Math.toDegrees(angleItemInRad);
                        if (getCenterX() > e.getCenterX())
                        {
                            directionItem += 180;
                        }
                        if(directionItem  > direction - 5 && directionItem < direction + 5)
                        {
                            e.currentXVelocity = (e.currentXVelocity + (float)Math.cos(directionItem * .0175)*tractorBeamAccel);
                            e.currentYVelocity = (e.currentYVelocity + (float)Math.sin(directionItem * .0175)*tractorBeamAccel);
                        }
                    }
                }
                
            }
        }
    }
    
    private void followPlanet()
    {
        //TO DO: change ships position and rotation to match planet.
        Planet landedOn = (Planet) landed[1];
        intendedDirectionInDegrees += landedOn.getRotR8();
        
        double radius = landedOn.radius;
        x = (float)(landedOn.getCenterX()-this.radius*2.2 + Math.cos(Math.toRadians(intendedDirectionInDegrees))*(radius+10));
        y = (float)(landedOn.getCenterY()-this.radius*2.2 + Math.sin(Math.toRadians(intendedDirectionInDegrees))*(radius+10));
    }
    
    private void checkAttacks() throws IOException
    {
        if (dead)
            return;
        if (attackTimer <= weaponMaxCharge)
        {
            attackTimer = (long)(attackTimer + (System.currentTimeMillis() - lastAttackTimer)*((double)ATTACK_COOLDOWN/1000));
            if (attackTimer > weaponMaxCharge)
            {
                attackTimer = weaponMaxCharge;
            }
        }
        lastAttackTimer = System.currentTimeMillis();

        //Fires a shot if cooldown is done && enough charge
        if(handler.getKeyManager().attack)
        {
            if (selectedWeapon == 0)//miningLaser
            {
                for (Entity e : handler.getWorld().getEntityManager().getEntities())
                {
                    if (e.isAsteroid) //*Fix particles, probably implement movement till hitting asteroid, increase laser damage
                    {
                        float xDist = e.getCenterX() - getCenterX();
                        float yDist = e.getCenterY() - getCenterY();
                        double dist = Math.sqrt(xDist * xDist + yDist * yDist - e.radius*e.radius);
                        if (dist < autoTractorMaxDist*2)//150
                        {
                            double angleAsteroidInRad = Math.atan(yDist/xDist);
                            double directionAsteroid = Math.toDegrees(angleAsteroidInRad);
                            if (getCenterX() > e.getCenterX())
                            {
                                directionAsteroid += 180;
                            }
                            if ((attackTimer >= 2) && (System.currentTimeMillis() - lastShotFired > 1))
                            {
                                e.damaged(.1, this, x, y);
                                attackTimer -= 2;
                                lastShotFired = System.currentTimeMillis();
                            }
                            
                            //Particles
                            double xMod = (Math.cos(Math.toRadians(directionAsteroid)));
                            double yMod = (Math.sin(Math.toRadians(directionAsteroid)));
                            Color color = Assets.miningLaserColors[handler.random(0, 2)];
                            for (int c = 1; c < 8; c++)
                            {
                                handler.getWorld().getParticleManager().addParticle(new Particle(handler,(int)(getCenterX()+xMod*c), (int)(getCenterY()+yMod*c), (double)(xMod*10+e.currentXVelocity), (double)(yMod*10+e.currentYVelocity), color, (int)dist));
                            }
                        }
                    }
                }
            }
            
            else if (selectedWeapon == 1 && (attackTimer >= 1000) && (System.currentTimeMillis() - lastShotFired > 250))//plasmaBlaster
            {
                if (LorR == false)
                {
                    int shot1X = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees - 113)) * - 12));
                    int shot1Y = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees - 113)) * - 12));
                    handler.getWorld().getAttackManager().addAttack(new PlasmaShot(handler, shot1X, shot1Y, intendedDirectionInDegrees, false, this, SHOT_DMG));
                    LorR = true;
                }
                else
                {
                    int shot2X = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees + 113)) * - 12));
                    int shot2Y = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees + 113)) * - 12));
                    handler.getWorld().getAttackManager().addAttack(new PlasmaShot(handler, shot2X, shot2Y, intendedDirectionInDegrees, false, this, SHOT_DMG));
                    LorR = false;
                }

                attackTimer -= 1000;
                lastShotFired = System.currentTimeMillis();
                playLaser();
            }
        }

    }
    
    @Override
    public void die()
    {
        animationDead = new Animation(125, Assets.deathAnimation, 3, false);
        acceleration = 0;
        dead = true;
    }
    
    private void getInput()
    {   
        if (dead)
        {
            
        }
        else
        {
            
            if (handler.getKeyManager().lArrow && System.currentTimeMillis() - lastWeaponChange > 500)
            {
                selectedWeapon --;
                if (selectedWeapon == -1)
                {
                    if (hasRockets)
                    {
                        selectedWeapon = 3;
                    }
                    else if (hasCombatLaser)
                    {
                        selectedWeapon = 2;
                    }
                    else if (hasPlasmaBlaster)
                    {
                        selectedWeapon = 1;
                    }
                    else
                    {
                        selectedWeapon = 0;
                    }
                }
                else if (selectedWeapon == 2 && !hasCombatLaser)
                {
                    if (hasPlasmaBlaster)
                    {
                        selectedWeapon = 1;
                    }
                    else
                    {
                        selectedWeapon = 0;
                    }
                }
                else if (selectedWeapon == 1 && !hasPlasmaBlaster)
                {
                    selectedWeapon = 0;
                }
                
                lastWeaponChange = System.currentTimeMillis();
            }
            else if (handler.getKeyManager().rArrow && System.currentTimeMillis() - lastWeaponChange > 500)
            {
                selectedWeapon ++;
                if (selectedWeapon == 4)
                {
                    selectedWeapon = 0;
                }
                else if (selectedWeapon == 3 && !hasRockets)
                {
                    selectedWeapon = 0;
                }
                else if (selectedWeapon == 2 && !hasCombatLaser)
                {
                    if (hasRockets)
                    {
                        selectedWeapon = 3;
                    }
                    else
                    {
                        selectedWeapon = 0;
                    }
                }
                else if (selectedWeapon == 1 && !hasPlasmaBlaster)
                {
                    if (hasCombatLaser)
                    {
                        selectedWeapon = 2;
                    }
                    else if (hasRockets)
                    {
                        selectedWeapon = 3;
                    }
                    else
                    {
                        selectedWeapon = 0;
                    }
                }
                
                lastWeaponChange = System.currentTimeMillis();
            }
            
            
            if(handler.getKeyManager().up)
            {
                acceleration = MAX_ACCELERATION;
            }
            else if(handler.getKeyManager().down)
            {
                if (!(boolean)landed[0])
                {
                    acceleration = -MAX_ACCELERATION/4;
                }
            }
            else
            {
                if (!(boolean)landed[0])
                {
                    acceleration = 0;
                }
            }

            if(handler.getKeyManager().left)
            {
                if (!(boolean)landed[0])
                {
                    intendedDirectionInDegrees -= TURNING_SPEED;
                }
                else if(acceleration != 0)
                {
                    intendedDirectionInDegrees -= TURNING_SPEED/4;
                }
            }

            if(handler.getKeyManager().right)
            {
                if (!(boolean)landed[0])
                {
                    intendedDirectionInDegrees += TURNING_SPEED;
                }
                else if(acceleration != 0)
                {
                    intendedDirectionInDegrees += TURNING_SPEED/4;
                }
            }

            if (handler.getKeyManager().r)
            {
                autoTractorBeam = false;
            }
            else
            {
                autoTractorBeam = true;
            }
            
            if (handler.getKeyManager().shift && afterBurnerTimer > 40)
            {
                acceleration = MAX_ACCELERATION*2;
                afterBurnerTimer -= 40;
            }

            inputXAccel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
            inputYAccel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees)) * acceleration);
        }
    }
    
    public void wormHoleRotationTick()
    {
        wormHoleRotation += .1;
    }

    @Override
    public void render(Graphics g) 
    { 
        if (respawnClockStart > 0 && (System.currentTimeMillis() - respawnClockStart) < 3000)
        {
            wormHoleRotationTick();
            Graphics2D g4d = (Graphics2D) g.create();
            AffineTransform at = AffineTransform.getTranslateInstance((int)(x - handler.getGameCamera().getxOffset()), (int)(y - handler.getGameCamera().getyOffset()));
            at.rotate(Math.toRadians(wormHoleRotation), width/2, height/2);
            
            double timeLeft = 1 - (System.currentTimeMillis() - respawnClockStart) / 3000;
            if (timeLeft > 0 && timeLeft <= 1)
            {
                g4d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)timeLeft));
            }
            g4d.drawImage(wormHole, at , null);
            g4d.dispose();
        }
        
        //g.fillRect((int)(x-1-handler.getGameCamera().getxOffset()), (int)(y-1-handler.getGameCamera().getyOffset()), 2, 2);
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        /*COLLISION TEST BOX
        Shape temp = new Ellipse2D.Double((int)(x + bounds.x - handler.getGameCamera().getxOffset()), (int)(y + bounds.y - handler.getGameCamera().getyOffset()), bounds.width, bounds.height);
        g2d.draw(temp);
        //*/
        
        AffineTransform at = AffineTransform.getTranslateInstance((int)(x - handler.getGameCamera().getxOffset()), (int)(y - handler.getGameCamera().getyOffset()));
        if (dead)
        {
            g2d.drawImage(animationDead.getCurrentFrame(), at, null);
        }
        else
        {
            at.rotate(Math.toRadians(intendedDirectionInDegrees+90), width/2, height/2);
            g2d.drawImage(getCurrentShipAFrame(), at, null);
            renderParticles();
        }
        g2d.dispose();
        
        //inventory.render(g);
    }
    
    private BufferedImage getCurrentShipAFrame()
    {
        return shipImage;
        /*
        if(handler.getKeyManager().up || autoDrive == true && acceleration > .2)
        {
            return animationForward.getCurrentFrame();
        }
        else //while ship is not moving
        {
            return shipStill;
        }
                */
    }
    
    private void renderParticles()
    {
        if(acceleration > 0)
        {
            if (shipLevel == 0) //stock brown ship
            {
                int particleX = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees)) * - 15));
                int particleY = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees)) * - 15));
                float particleXVel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,45))) * -acceleration * handler.random(100, 150));
                float particleYVel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,45))) * -acceleration * handler.random(100, 150));
                handler.getWorld().getParticleManager().addParticle(new Particle(handler, particleX, particleY, particleXVel, particleYVel, Assets.engineColors[handler.random(3, 4)], handler.random(100, 400)));
                if (handler.random(0, 3) == 3)
                {
                    handler.getWorld().getParticleManager().addParticle(new Particle(handler, particleX, particleY, particleXVel/2, particleYVel/2, Assets.engineColors[handler.random(0, 2)], handler.random(200, 800)));
                }
            }
            else if (shipLevel == 3 || shipLevel == 1)
            {
                int particleX = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees)) * - 18));
                int particleY = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees)) * - 18));
                float particleXVel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,45))) * -acceleration * handler.random(100, 200));
                float particleYVel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,45))) * -acceleration * handler.random(100, 200));
                handler.getWorld().getParticleManager().addParticle(new Particle(handler, particleX, particleY, particleXVel, particleYVel, Assets.engineColors[handler.random(3, 4)], handler.random(200, 500)));
                if (handler.random(0, 3) == 3)
                {
                    handler.getWorld().getParticleManager().addParticle(new Particle(handler, particleX, particleY, particleXVel/2, particleYVel/2, Assets.engineColors[handler.random(0, 2)], handler.random(300, 900)));
                }
            }
            else if (shipLevel == 2 || shipLevel == 4)
            {
                int particleX = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees+22.75)) * - 18));
                int particleY = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees+22.75)) * - 18));
                int particle2X = (int)(getCenterX()+(Math.cos(Math.toRadians(intendedDirectionInDegrees-22.75)) * - 18));
                int particle2Y = (int)(getCenterY()+(Math.sin(Math.toRadians(intendedDirectionInDegrees-22.75)) * - 18));
                float particleXVel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,10))) * -acceleration * handler.random(200, 400));
                float particleYVel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,10))) * -acceleration * handler.random(200, 400));
                float particle2XVel = (float) (Math.cos(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,10))) * -acceleration * handler.random(200, 400));
                float particle2YVel = (float) (Math.sin(Math.toRadians(intendedDirectionInDegrees+getRandDirection(0,10))) * -acceleration * handler.random(200, 400));
                handler.getWorld().getParticleManager().addParticle(new Particle(handler, particleX, particleY, particleXVel, particleYVel, Assets.ionEngineColors[handler.random(0, 2)], handler.random(100, 150)));
                handler.getWorld().getParticleManager().addParticle(new Particle(handler, particle2X, particle2Y, particle2XVel, particle2YVel, Assets.ionEngineColors[handler.random(0, 2)], handler.random(100, 150)));
            }
        }
    }
    
    private int getRandDirection(int min, int max)
    {
        
        int randDirection = handler.random(min, max);
        if (handler.random(0, 1) == 1)
        {
            randDirection *= -1;
        }
        return randDirection;
    }
    
    //Sound Methods ********************************************
    private void playLaser() throws IOException
    {
        if (handler.playSound())
        {
            AudioPlayer.player.start(new AudioStream(new ByteArrayInputStream(Assets.laserSound)));
        }
    }
    
    private void playEngine()
    {
        
    }
    
    public Rectangle[] getBounds()
    {
        return bounds;
    }
    
    public boolean getAutoTractorBeam()
    {
        return autoTractorBeam;
    }

    public void shipLevel0()
    {
        shipLevel = 0;
        shipImage = Assets.ship1;
    }
    public void shipLevel1()
    {
        shipLevel = 1;
        shipImage = Assets.ship2Stock;
    }
    public void shipLevel2()
    {
        shipLevel = 2;
        shipImage = Assets.ship2Engines;
    }
    public void shipLevel3()
    {
        shipLevel = 3;
        shipImage = Assets.ship2Guns;
        hasPlasmaBlaster = true;
    } 
    public void shipLevel4()
    {
        shipLevel = 4;
        shipImage = Assets.ship2Full;
        hasPlasmaBlaster = true;
    }

    public int getSHOT_DMG() {
        return SHOT_DMG;
    }

    public void setSHOT_DMG(int SHOT_DMG) {
        this.SHOT_DMG = SHOT_DMG;
    }

    public int getATTACK_COOLDOWN() {
        return ATTACK_COOLDOWN;
    }

    public void setATTACK_COOLDOWN(int ATTACK_COOLDOWN) {
        this.ATTACK_COOLDOWN = ATTACK_COOLDOWN;
    }

    public double getMAX_SPEED() {
        return MAX_SPEED;
    }

    public void setMAX_SPEED(double MAX_SPEED) {
        this.MAX_SPEED = MAX_SPEED;
    }

    public float getMAX_ACCELERATION() {
        return MAX_ACCELERATION;
    }

    public void setMAX_ACCELERATION(float ACCELERATION) {
        this.MAX_ACCELERATION = ACCELERATION;
    }

    public double getTURNING_SPEED() {
        return TURNING_SPEED;
    }

    public void setTURNING_SPEED(double TURNING_SPEED) {
        this.TURNING_SPEED = TURNING_SPEED;
    }

    public int getMAX_HEALTH() {
        return MAX_HEALTH;
    }

    public void setMAX_HEALTH(int MAX_HEALTH) {
        this.MAX_HEALTH = MAX_HEALTH;
    }

    public long getAttackTimer() {
        return attackTimer;
    }
    
    public int getShipLevel()
    {
        return shipLevel;
    }
    
    public float getGravityStrength()
    {
        return gravityStrength;
    }
    
    public void setGravityStrength(float gs)
    {
        gravityStrength = gs;
    }
    
    public boolean isPlayerDead()
    {
        return dead;
    }
    
    
    //STORYLINE GETTERS
    public ArrayList<Entity> getObjectsOfInterest()
    {
        return objectsOfInterest;
    }
    public void setObjectsOfInterest(ArrayList<Entity> e)
    {
        objectsOfInterest = e;
    }
            
    
    public boolean isNearEarth() {
        return nearEarth;
    }

    public void setNearEarth(boolean nearEarth) {
        this.nearEarth = nearEarth;
    }
    
    public Object[] isNearAsteroid() {
        return nearAsteroid;
    }

    public void setNearAsteroid(boolean nearAsteroid, Entity e) {
        this.nearAsteroid[0] = nearAsteroid;
        this.nearAsteroid[1] = e;
    }

    public Object[] isNearAsteroidField() {
        return nearAsteroidField;
    }

    public void setNearAsteroidField(boolean nearAsteroidField, Entity e) {
        this.nearAsteroidField[0] = nearAsteroidField;
        this.nearAsteroidField[1] = e;
    }
    
    public Object[] isNearPlanet() {
        return nearPlanet;
    }

    public void setNearPlanet(boolean nearPlanet, Entity e) {
        this.nearPlanet[0] = nearPlanet;
        this.nearPlanet[1] = e;
    }

    public Object[] isNearNebula() {
        return nearNebula;
    }

    public void setNearNebula(boolean nearNebula, Entity e) {
        this.nearNebula[0] = nearNebula;
        this.nearNebula[1] = e;
    }

    public Object[] isNearBlackHole() {
        return nearBlackHole;
    }

    public void setNearBlackHole(boolean nearBlackHole, Entity e) {
        this.nearBlackHole[0] = nearBlackHole;
        this.nearBlackHole[1] = e;
    }

    public Object[] isNearWormhole() {
        return nearWormhole;
    }

    public void setNearWormhole(boolean nearWormhole, Entity e) {
        this.nearWormhole[0] = nearWormhole;
        this.nearWormhole[1] = e;
    }
    
    public void resetNearby()
    {
        this.nearAsteroid[0] = false;
        this.nearBlackHole[0] = false;
        this.nearNebula[0] = false;
        this.nearPlanet[0] = false;
        this.nearWormhole[0] = false;
        this.nearAsteroidField[0] = false;
    }
    
    public boolean getRespawnText()
    {
        return respawnText;
    }
    
    public void setRespawnText(boolean respawnText)
    {
        this.respawnText = respawnText;
    }
    
    public void setInvincible(boolean invincible, long invincibleTime)
    {
        this.invincible = invincible;
        this.invincibleTime = invincibleTime;
        this.invincibleTimeStart = System.currentTimeMillis();
        
    }
    
    public boolean getInvincible()
    {
        return this.invincible;
    }
    
    public long getInvincibleTimeLeft()
    {
        return invincibleTimeLeft;
    }
    
    
    public long getInvincibleTime()
    {
        return this.invincibleTime;
    }
    
    
    //Respawn stats info
    public void addNebulaSeen()
    {
        numOfNebulasSeen += 1;
    }
    public void addPlanetSeen()
    {
        numOfPlanetsSeen += 1;
    }
    public void addEnemyDestroyed()
    {
        numOfEnemiesDestroyed += 1;
    }
    public void addAsteroidDestroyed()
    {
        numOfAsteroidsDestroyed += 1;
    }
    public void addMaterialsCollected()
    {
        numOfMaterialsCollected += 1;
    }
    
    public int getNebulasSeen()
    {
        return numOfNebulasSeen;
    }
    public int getPlanetsSeen()
    {
        return numOfPlanetsSeen;
    }
    public int getEnemiesDestroyed()
    {
        return numOfEnemiesDestroyed;
    }
    public int getAsteroidsDestroyed()
    {
        return numOfAsteroidsDestroyed;
    }
    public int getMaterialsCollected()
    {
        return numOfMaterialsCollected;
    }
    
    public void resetStats()
    {
        numOfNebulasSeen = 0;
        numOfPlanetsSeen = 0;
        numOfEnemiesDestroyed = 0;
        numOfAsteroidsDestroyed = 0;
        numOfMaterialsCollected = 0;
    }
    
    public Object[] getLanded()
    {
        return landed;
    }
    
    public void setLanded(boolean landedTF, Entity landedOn)
    {
        landed = new Object[]{landedTF, landedOn};
    }
    
    public int getSelectedWeapon()
    {
        return selectedWeapon;
    }
    
    public long getafterBurnerTimer()
    {
        return afterBurnerTimer;
    }
    
    
}
