package src.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SpriteLoader {

    public static BufferedImage load(String path) {
        try {
            return ImageIO.read(SpriteLoader.class.getResourceAsStream(path));
        }
        catch (Exception e) {
            System.out.println("FAILED TO LOAD RESOURCE: " + path);
            return null;
        }
    }
}
