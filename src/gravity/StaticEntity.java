package gravity;

public abstract class StaticEntity extends Entity
{
    protected static final float cm3Tokm3 = 100f;
    protected static final int PLANET_DIAMETER = 64;
    protected static final int BLACK_HOLE_DIAMETER = 128;
    
    public StaticEntity(Handler handler, float x, float y, int width, int height, boolean gravity)
    {
        super(handler, x, y, width, height, gravity);
    }
    
}
