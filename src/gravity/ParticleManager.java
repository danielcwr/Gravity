package gravity;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class ParticleManager extends Manager
{
    private Handler handler;
    private ArrayList<Particle> particles;
    
    public ParticleManager(Handler handler, Player player)
    {
        super(handler, player);
        this.handler = handler;
        
        particles = new ArrayList<Particle>();
    }
    
    public void tick()
    {
        Iterator<Particle> it = particles.iterator();
        while(it.hasNext())
        {
            Particle p = it.next();
            boolean[] array = updateScreenBoundaries(p.x, p.y, 0);
            p.setOnTick(array[0]);
            p.setOnRender(array[1]);
            
            if (p.getOnRender()) 
            {
                p.tick();
                if (System.currentTimeMillis() - p.spawnedAtTime > p.activeTime)
                {
                    it.remove();
                }
            }
            else
            {
                it.remove();
            }
            
        }
        
        //System.out.println("Left: " + left + ", Right: " + right + ", Top: " + top + ", Bot: " + bot);
    }
    
    public void render(Graphics g)
    {
        for (Particle p : particles) //Renders all entities
        {
            p.render(g);
        }
    }
    
   
    
    //AddEntity
    public void addParticle(Particle p)
    {
        particles.add(p);
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
    
    public ArrayList<Particle> getParticles()
    {
        return particles;
    }
    
}
