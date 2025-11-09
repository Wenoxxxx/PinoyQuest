package src.tile;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import src.core.GamePanel;

public class TileManager {

    GamePanel gp;
    Tile[] tile; // Array data structure for tiles

    public TileManager(GamePanel gp) {

        this.gp = gp;

        tile = new Tile[10];

        getTileImage();

    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/res/FieldsTile_01.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/res/FieldsTile_02.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/res/FieldsTile_03.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g2) {

        /*
         * g2.drawImage(tile[0].image, 0, 0, gp.tileSize, gp.tileSize, null);
         * g2.drawImage(tile[1].image, 48, 0, gp.tileSize, gp.tileSize, null);
         * g2.drawImage(tile[2].image, 96, 0, gp.tileSize, gp.tileSize, null);
         */

        int column = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        /*
         * int column = 0;
         * int row = 0;
         * int x = 0;
         * int y = 0;
         */
        while (column < gp.maxScreenCol && row < gp.maxScreenRow) {
            int worldX = x;
            int worldY = y;
            int screenX = worldX - gp.cameraX;
            int screenY = worldY - gp.cameraY;

            g2.drawImage(tile[0].image, screenX, screenY, gp.tileSize, gp.tileSize,
                    null);
            column++;
            x += gp.tileSize;

            if (column == gp.maxScreenCol) {
                column = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
        /*
         * while (column < gp.maxScreenCol && row < gp.maxScreenRow) {
         * g2.drawImage(tile[0].image, x, y, gp.tileSize, gp.tileSize, null);
         * column++;
         * x += gp.tileSize;
         * 
         * if (column == gp.maxScreenCol) {
         * column = 0;
         * x = 0;
         * row++;
         * y += gp.tileSize;
         * }
         * }
         */
    }
}