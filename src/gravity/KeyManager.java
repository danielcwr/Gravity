package gravity;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener
{
    private boolean[] heldKeys;
    private boolean[] toggledKeys;
    public boolean up, down, left, right, r, attack, inventory, readText, lArrow, rArrow, shift;
    public boolean anyKeyPressed = false;
    
    public KeyManager()
    {
        heldKeys = new boolean[128];
        toggledKeys = new boolean[128];
    }
    
    public void tick()
    {
        up = heldKeys[KeyEvent.VK_W];
        down = heldKeys[KeyEvent.VK_S];
        left = heldKeys[KeyEvent.VK_A];
        right = heldKeys[KeyEvent.VK_D];
        r = toggledKeys[KeyEvent.VK_R];
        attack = heldKeys[KeyEvent.VK_SPACE];
        
        lArrow = heldKeys[KeyEvent.VK_LEFT];
        rArrow = heldKeys[KeyEvent.VK_RIGHT];
        
        shift = heldKeys[KeyEvent.VK_SHIFT];
        
        inventory = toggledKeys[KeyEvent.VK_I];
        readText = heldKeys[KeyEvent.VK_X];
        
    }
    
    @Override
    public void keyTyped(KeyEvent e) 
    {
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() < 0 || e.getKeyCode() >= heldKeys.length)
            return;
        heldKeys[e.getKeyCode()] = true;
        anyKeyPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        if (e.getKeyCode() < 0 || e.getKeyCode() >= heldKeys.length)
            return;
        heldKeys[e.getKeyCode()] = false;
        anyKeyPressed = false;
        toggledKeys[e.getKeyCode()] = !toggledKeys[e.getKeyCode()];
    }
    
    public int getKeyPressed()
    {
        for (int c = 0; c < heldKeys.length; c++)
        {
            if (heldKeys[c] == true)
                return c;
                
        }
        return -1;
    }
    
    
}
