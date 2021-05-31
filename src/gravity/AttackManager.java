package gravity;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class AttackManager extends Manager
{
    private Handler handler;
    private ArrayList<Attack> attacks;
    
    
    public AttackManager(Handler handler, Player player)
    {
        super(handler, player);
        
        attacks = new ArrayList<Attack>();
    }
    
    public void tick()
    {
        Iterator<Attack> it = attacks.iterator();
        while(it.hasNext())
        {
            Attack attack = it.next();
            if (!attack.isActive)
            {
                it.remove();
            }
            boolean[] array = updateScreenBoundaries(attack.x, attack.y, 1);
            attack.setOnTick(array[0]);
            attack.setOnRender(array[1]);
            if (attack.getOnTick()) 
            {
                attack.tick();
                if (attack.activeTime != -1)
                {
                    if (System.currentTimeMillis() - attack.spawnedAtTime > attack.activeTime)
                    {
                        attack.isActive = false;
                        it.remove();
                    }
                }
            }
            else
            {
                attack.isActive = false;
                it.remove();
            }
            
        }
        
        //System.out.println("Left: " + left + ", Right: " + right + ", Top: " + top + ", Bot: " + bot);
    }
    
    public void render(Graphics g)
    {
        for (Attack attack : attacks) //Renders all entities
        {
            if (attack.getOnRender())
            {
                attack.render(g);
            }
        }
    }
    
   
    
    //AddEntity
    public void addAttack(Attack attack)
    {
        attacks.add(attack);
    }
    
    //Getters and Setters
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }
    
    public ArrayList<Attack> getAttacks()
    {
        return attacks;
    }
    
}
