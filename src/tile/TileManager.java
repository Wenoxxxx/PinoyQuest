package src.tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.*;
import src.core.GamePanel;

public class TileManager {

    GamePanel gp;
    Tile[] tile; 
    int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap();
    }


    // LOAD TILE IMAGES
    public void getTileImage() {
        String basePath = "src" + File.separator + "assets" + File.separator + "tiles" + File.separator;

        try {
            // GRASS
            tile[0] = new Tile();
            File file0 = new File(basePath + "tile0_Grass.png");
            if (file0.exists()) tile[0].image = ImageIO.read(file0);
            tile[0].collision = false; // walkable tile

            // PATH
            tile[1] = new Tile();
            File file1 = new File(basePath + "tile1_StraightPath.png");
            if (file1.exists()) tile[1].image = ImageIO.read(file1);
            tile[1].collision = false;

            // CORNER BL
            tile[2] = new Tile();
            File file2 = new File(basePath + "tile2_BottomLeftCorner.png");
            if (file2.exists()) tile[2].image = ImageIO.read(file2);
            tile[2].collision = true; // block movement

            // CORNER BR
            tile[3] = new Tile();
            File file3 = new File(basePath + "tile3_BottomRightCorner.png");
            if (file3.exists()) tile[3].image = ImageIO.read(file3);
            tile[3].collision = true;

            // CORNER UL
            tile[4] = new Tile();
            File file4 = new File(basePath + "tile4_UpperLeftCorner.png");
            if (file4.exists()) tile[4].image = ImageIO.read(file4);
            tile[4].collision = true;

            // CORNER UR
            tile[5] = new Tile();
            File file5 = new File(basePath + "tile5_UpperRightCorner.png");
            if (file5.exists()) tile[5].image = ImageIO.read(file5);
            tile[5].collision = true;

            // Rocky Road w/ Grass – walkable
            tile[6] = new Tile();
            File file6 = new File(basePath + "tile6_RockyRoadwLilGrass.png");
            if (file6.exists()) tile[6].image = ImageIO.read(file6);
            tile[6].collision = false;

            // Rocky Road – walkable
            tile[7] = new Tile();
            File file7 = new File(basePath + "tile7_RockyRoad.png");
            if (file7.exists()) tile[7].image = ImageIO.read(file7);
            tile[7].collision = false;

        } catch (IOException e) {
            System.err.println("Error loading tile images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // LOAD MAP FROM TEXT FILE
    public void loadMap() {
        try {
            String mapPath = "src" + File.separator + "assets" + File.separator + "maps" + File.separator + "map1.txt";
            File mapFile = new File(mapPath);

            if (!mapFile.exists()) {
                System.out.println("Map not found → generating default map.");
                for (int row = 0; row < gp.maxWorldRow; row++) {
                    for (int col = 0; col < gp.maxWorldCol; col++) {
                        mapTileNum[col][row] = 0; // default grass
                    }
                }
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(mapFile)));

            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                String[] numbers = line.split(" ");
                for (int col = 0; col < numbers.length && col < gp.maxWorldCol; col++) {
                    mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                }
                row++;
            }

            br.close();
            System.out.println("Map loaded successfully!");

        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();

            // fallback: all grass
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[col][row] = 0;
                }
            }
        }
    }


    // NEW: CHECK IF TILE BLOCKS MOVEMENT
    public boolean isBlocked(int col, int row) {

        // out-of-bounds tiles = BLOCKED to prevent crashes
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            return true;
        }

        int tileNum = mapTileNum[col][row];

        // invalid tile number → treat as non-blocking
        if (tileNum < 0 || tileNum >= tile.length) return false;
        if (tile[tileNum] == null) return false;

        // return the collision flag set during getTileImage()
        return tile[tileNum].collision;
    }


    // DRAW TILES
    public void draw(Graphics2D g2) {

        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {

                int tileNum = mapTileNum[worldCol][worldRow];

                if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null || tile[tileNum].image == null) {
                    tileNum = 0; // fallback to grass
                }

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int screenX = worldX - gp.cameraX;
                int screenY = worldY - gp.cameraY;

                // screen size fallback
                int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
                int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

                boolean visible =
                        screenX + gp.tileSize > 0 &&
                        screenX < screenW &&
                        screenY + gp.tileSize > 0 &&
                        screenY < screenH;

                if (visible) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY,
                            gp.tileSize, gp.tileSize, null);
                }
            }
        }
    }
}
