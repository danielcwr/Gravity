package gravity;

public class TypeText 
{
    private int speed = 30, speedLong = 300, index;
    private long lastTime, timer;
    private String text;
    
    public TypeText(String text)
    {
        this.text = text;
        index = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }
    
    public void tickLong()
    {
        timer += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        
        if (timer > speedLong && (index != text.length()))
        {
            index ++;
            timer = 0;
        }
    }
    
    public void tick()
    {
        timer += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        
        if (timer > speed && (index != text.length()))
        {
            index ++;
            timer = 0;
        }
    }
    
    public String getCurrentFrame()
    {
        return text.substring(0, index);
    }
    
    public String getFullString()
    {
        return text;
    }
}
