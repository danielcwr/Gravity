package gravity;

import static gravity.Assets.blockText;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class FileLoader 
{
    public static BufferedImage loadImage(String path)
    {
        try 
        {
            /*
            InputStream input = ClassLoader.class.getResourceAsStream(path);
            ImageInputStream is = ImageIO.createImageInputStream(input);
            return ImageIO.read(is);
            */
            return ImageIO.read(FileLoader.class.getResourceAsStream(path));
        } 
        
        catch (IOException ex) 
        {
            Logger.getLogger(FileLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        return null;
    }
    
    public static Font loadFont(String path) throws FontFormatException, IOException
    {
        try 
        {
            InputStream is = FileLoader.class.getResourceAsStream(path);
            return Font.createFont(Font.TRUETYPE_FONT, is);
        }
        catch(IOException ex)
        {
            Logger.getLogger(FileLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        return null;
    }
    
    public static Scanner loadText(String path) throws FileNotFoundException, IOException
    {
        try
        {
            InputStream is = FileLoader.class.getResourceAsStream(path);
            Scanner meme = new Scanner(is);
            return meme;
        }
        catch(Exception e){
            Scanner meme = new Scanner(new File(path));
            return meme;
        }
        //return null;
    }
    
    public static byte[] loadAudio(String path) throws FileNotFoundException, IOException
    {
        InputStream is = FileLoader.class.getResourceAsStream(path);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
          buffer.write(data, 0, nRead);
        }
        
        buffer.flush();
        data = buffer.toByteArray();
        
        return data;
    }
    
}
