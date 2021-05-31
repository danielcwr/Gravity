package gravity;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Gravity
{

    public static void main(String[] args) //Just defines screen size
    {
        int recomendedWidth = 1920;
        int recomendedHeight = 1080;
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        //Display gameFrame = new Display("Gravity", screenWidth, screenHeight);
        //System.out.println(screenHeight);
        
        double scaleWidth = (double)(screenWidth) / (double)(recomendedWidth);
        double scaleHeight = (double)(screenHeight) / (double)(recomendedHeight);
        //System.out.println("Scale Width: " + scaleWidth);
        //System.out.println("Scale Height: " + scaleHeight);
        
        Game gravity = new Game("Gravity", screenWidth, screenHeight, scaleWidth, scaleHeight);
        gravity.start();
    }
    
}
