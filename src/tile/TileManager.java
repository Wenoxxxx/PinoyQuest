package src.tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.*;
import src.core.GamePanel;

public class TileManager {

    GamePanel gp;
    Tile[] tile; // Array data structure for tiles
    int mapTileNum [][];
    

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int [gp.maxWorldCol] [gp.maxWorldRow];
        
        getTileImage();
        loadMap();

    }
    
    public void getTileImage() {
        // Relative path from project root (no System.getProperty)
        String basePath = "src" + File.separator + "assets" + File.separator + "tiles" + File.separator;

        try {
            // GRASS
            tile[0] = new Tile();
            File file0 = new File(basePath + "tile0_Grass.png");
            if (file0.exists()) {
                tile[0].image = ImageIO.read(file0);
            } else {
                System.err.println("Tile image not found: " + file0.getPath());
            }

            // PATH
            tile[1] = new Tile();
            File file1 = new File(basePath + "tile1_StraightPath.png");
            if (file1.exists()) {
                tile[1].image = ImageIO.read(file1);
            } else {
                System.err.println("Tile image not found: " + file1.getPath());
            }
        
            // CORNER BL
            tile[2] = new Tile();
            File file2 = new File(basePath + "tile2_BottomLeftCorner.png");
            if (file2.exists()) {
                tile[2].image = ImageIO.read(file2);
            } else {
                System.err.println("Tile image not found: " + file2.getPath());
            }

            // CORNER BR
            tile[3] = new Tile();
            File file3 = new File(basePath + "tile3_BottomRightCorner.png");
            if (file3.exists()) {
                tile[3].image = ImageIO.read(file3);
            } else {
                System.err.println("Tile image not found: " + file3.getPath());
            }

            // CORNER UP
            tile[4] = new Tile();
            File file4 = new File(basePath + "tile4_UpperLeftCorner.png");
            if (file4.exists()) {
                tile[4].image = ImageIO.read(file4);
            } else {
                System.err.println("Tile image not found: " + file4.getPath());
            }
        
            // CORNER UR
            tile[5] = new Tile();
            File file5 = new File(basePath + "tile5_UpperRightCorner.png");
            if (file5.exists()) {
                tile[5].image = ImageIO.read(file5);
            } else {
                System.err.println("Tile image not found: " + file5.getPath());
            }

            // ROAD w lil grass
            tile[6] = new Tile();
            File file6 = new File(basePath + "tile6_RockyRoadwLilGrass.png");
            if (file6.exists()) {
                tile[6].image = ImageIO.read(file6);
            } else {
                System.err.println("Tile image not found: " + file6.getPath());
            }

            // ROAD like izza rocky road
            tile[7] = new Tile();
            File file7 = new File(basePath + "tile7_RockyRoad.png");
            if (file7.exists()) {
                tile[7].image = ImageIO.read(file7);
            } else {
                System.err.println("Tile image not found: " + file7.getPath());
            }

        } catch (IOException e) {
            System.err.println("Error loading tile images: " + e.getMessage());
            e.printStackTrace();
        }

    }

    // MAP LOADER FROM TXT
    public void loadMap() {
        try {
            String mapPath = "src" + File.separator + "assets" + File.separator + "maps" + File.separator + "map1.txt";
            File mapFile = new File(mapPath);

            if (!mapFile.exists()) {
                System.out.println("Map file not found. Generating default map with grass tiles.");
                for (int row = 0; row < gp.maxWorldRow; row++) {
                    for (int col = 0; col < gp.maxWorldCol; col++) {
                        mapTileNum[col][row] = 0; // Grass
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

                // Fallback default map
                for (int row = 0; row < gp.maxWorldRow; row++) {
                    for (int col = 0; col < gp.maxWorldCol; col++) {
                        mapTileNum[col][row] = 0; // Grass
                    }
                }
            }
    }   

    public void draw(Graphics2D g2) {

        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++){
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++){

                int tileNum = mapTileNum[worldCol][worldRow];

                // Safety check (prevents null image crash)
                if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null || tile[tileNum].image == null) {
                    tileNum = 0; // Default tile (e.g., grass)
                }

                // Convert world coordinates to screen coordinates
                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int screenX = worldX - gp.cameraX;
                int screenY = worldY - gp.cameraY;

                // Draw only visible tiles (basic culling)
                int screenW = gp.getWidth();
                int screenH = gp.getHeight();
                
                // Use default size if panel not sized yet
                if (screenW <= 0) {
                    screenW = gp.screenWidth;
                }
                if (screenH <= 0) {
                    screenH = gp.screenHeight;
                }
                
                if (
                    screenX + gp.tileSize > 0 &&
                    screenX < screenW &&
                    screenY + gp.tileSize > 0 &&
                    screenY < screenH
                ) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            }


        }

    }


}


