package gravity;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JFrame;

public class Display 
{
    private JFrame gameFrame;
    private Canvas canvas;
    
    private String Title;
    private int width, height;
    
    public Display(String Title, int width, int height)
    {
        this.Title = Title;
        this.width = width;
        this.height = height;
        CreateDisplay();
    }
    
    private void CreateDisplay()
    {
        gameFrame = new JFrame(Title);
        gameFrame.setSize(width, height);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        
        canvas = new Canvas();
        canvas.setSize(width, height);
        //gameFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        canvas.setFocusable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        
        gameFrame.add(canvas);
        gameFrame.pack();
        
    }
    
    public Canvas getCanvas()
    {
        return canvas;
    }
    
    public JFrame getFrame()
    {
        return gameFrame;
    }
}
