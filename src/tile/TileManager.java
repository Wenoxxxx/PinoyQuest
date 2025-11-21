package src.tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import src.core.GamePanel;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;

    // how many tile types are actually registered
    private int tileTypeCount = 0;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        // Increase if you need more tile types later
        tile = new Tile[32];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap();
    }

    // LOAD TILE IMAGES (auto-assign tile IDs by order)
    public void getTileImage() {
        String basePath = "src" + File.separator + "assets" + File.separator + "tiles" + File.separator;

        try {
            
            // [Tile ID 0] GRASS 
            tile[tileTypeCount] = new Tile();
            File grassFile = new File(basePath + "tile0_Grass.png");
            if (grassFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(grassFile);
            }
            tile[tileTypeCount].collision = false; // walkable
            System.out.println("TILE " + tileTypeCount + " = GRASS");
            tileTypeCount++;

            // [Tile ID 1] PATH HORIZONTAL 
            tile[tileTypeCount] = new Tile();
            File pathHFile = new File(basePath + "tile1_StraightPathHori.png");
            if (pathHFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(pathHFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("TILE " + tileTypeCount + " = PATH_HORI");
            tileTypeCount++;

            // [Tile ID 2] PATH VERTICAL 
            tile[tileTypeCount] = new Tile();
            File pathVFile = new File(basePath + "tile1_StraightPathVerti.png");
            if (pathVFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(pathVFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("TILE " + tileTypeCount + " = PATH_VERT");
            tileTypeCount++;

            // [Tile ID 3] ROAD 
            tile[tileTypeCount] = new Tile();
            File roadFile = new File(basePath + "tile7_RockyRoad.png");
            if (roadFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(roadFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("TILE " + tileTypeCount + " = ROAD");
            tileTypeCount++;

            // [Tile ID 4] BORDER
            tile[tileTypeCount] = new Tile();
            File borderFile = new File(basePath + "tile8_Border.png");
            if (borderFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(borderFile);
            }
            tile[tileTypeCount].collision = true;
            System.out.println("TILE " + tileTypeCount + " = ROAD");
            tileTypeCount++;

            

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

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new java.io.FileInputStream(mapFile))
            );

            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                // Use \\s+ so multiple spaces or tabs are okay
                String[] numbers = line.trim().split("\\s+");
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

    // CHECK IF TILE BLOCKS MOVEMENT
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

                if (tileNum < 0 || tileNum >= tile.length ||
                    tile[tileNum] == null || tile[tileNum].image == null) {
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
