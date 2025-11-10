package src.tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/res/FieldsTile_04.png"));

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/res/FieldsTile_05.png"));

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("/res/FieldsTile_06.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // helper method for tile drawings
    private void drawTileGrid(Graphics2D g2, BufferedImage tileImage, int startX, int startY, int cols, int rows) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int screenX = startX + (col * gp.tileSize) - gp.cameraX;
                int screenY = startY + (row * gp.tileSize) - gp.cameraY;
                g2.drawImage(tileImage, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }
    }

    public void draw(Graphics2D g2) {

        /*
         * g2.drawImage(tile[0].image, 0, 0, gp.tileSize, gp.tileSize, null);
         * g2.drawImage(tile[1].image, 48, 0, gp.tileSize, gp.tileSize, null);
         * g2.drawImage(tile[2].image, 96, 0, gp.tileSize, gp.tileSize, null);
         */

        drawTileGrid(g2, tile[0].image, 0, 0, 3, 3);

        drawTileGrid(g2, tile[0].image, 140, 0, 3, 2);

        drawTileGrid(g2, tile[1].image, 140, 96, 3, 1);

        drawTileGrid(g2, tile[0].image, 284, 0, 3, 2);

        drawTileGrid(g2, tile[2].image, 284, 96, 3, 1);

        drawTileGrid(g2, tile[0].image, 428, 0, 3, 2);

        drawTileGrid(g2, tile[3].image, 428, 96, 3, 1);

        drawTileGrid(g2, tile[0].image, 540, 0, 3, 2);

        drawTileGrid(g2, tile[4].image, 540, 96, 3, 1);
    }
}
