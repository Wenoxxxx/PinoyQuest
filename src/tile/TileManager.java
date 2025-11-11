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
        // Get project root directory and construct base path
        String projectRoot = System.getProperty("user.dir");
        String basePath = projectRoot + File.separator + "src" + File.separator + "assets" + File.separator + "tiles" + File.separator;
        
        try {
            tile[0] = new Tile();
            File file0 = new File(basePath + "FieldsTile_01.png");
            if (file0.exists()) {
                tile[0].image = ImageIO.read(file0);
            } else {
                System.err.println("Tile image not found: " + file0.getPath());
            }

            tile[1] = new Tile();
            File file1 = new File(basePath + "FieldsTile_02.png");
            if (file1.exists()) {
                tile[1].image = ImageIO.read(file1);
            } else {
                System.err.println("Tile image not found: " + file1.getPath());
            }

            tile[2] = new Tile();
            File file2 = new File(basePath + "FieldsTile_03.png");
            if (file2.exists()) {
                tile[2].image = ImageIO.read(file2);
            } else {
                System.err.println("Tile image not found: " + file2.getPath());
            }

            tile[3] = new Tile();
            File file3 = new File(basePath + "FieldsTile_04.png");
            if (file3.exists()) {
                tile[3].image = ImageIO.read(file3);
            } else {
                System.err.println("Tile image not found: " + file3.getPath());
            }

            tile[4] = new Tile();
            File file4 = new File(basePath + "FieldsTile_05.png");
            if (file4.exists()) {
                tile[4].image = ImageIO.read(file4);
            } else {
                System.err.println("Tile image not found: " + file4.getPath());
            }

            tile[5] = new Tile();
            File file5 = new File(basePath + "FieldsTile_06.png");
            if (file5.exists()) {
                tile[5].image = ImageIO.read(file5);
            } else {
                System.err.println("Tile image not found: " + file5.getPath());
            }

        } catch (IOException e) {
            System.err.println("Error loading tile images: " + e.getMessage());
            e.printStackTrace();
        }

    }

    // helper method for tile drawings
    private void drawTileGrid(Graphics2D g2, BufferedImage tileImage, int startX, int startY, int cols, int rows) {
        if (tileImage == null) {
            return; // Skip drawing if tile image is null
        }
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
