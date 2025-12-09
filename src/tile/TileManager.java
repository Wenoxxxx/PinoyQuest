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

    // [mapIndex][col][row] = tile ID from tile[]
    public int[][][] mapTileNum;

    // counts how many tiles are registered in tile[]
    private int tileTypeCount = 0;

    // number of maps (map1.txt, map2.txt, map3.txt)
    public static final int MAP_COUNT = 3; // up to 3 maps

    // starting index in tile[] for each map's tileset
    public int[] tilesetStart = new int[MAP_COUNT];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        // main tile list (all tiles from all maps)
        tile = new Tile[32];

        // mapTileNum[which map][x][y]
        mapTileNum = new int[MAP_COUNT][gp.maxWorldCol][gp.maxWorldRow];

        getTileImage(); // load tile images
        loadMap(); // load map text files
    }

    // ========================================================
    // NEW: Reload a single map when switching maps
    // ========================================================
    public void loadMap(int mapIndex) {
        if (mapIndex < 0 || mapIndex >= MAP_COUNT) {
            System.out.println("[TileManager] Invalid map index " + mapIndex);
            return;
        }

        String fileName = "map" + (mapIndex + 1) + ".txt";
        loadMapFileByIndex(fileName, mapIndex);

        System.out.println("[TileManager] Reloaded map " + mapIndex + " from " + fileName);
    }

    // Wrapper: uses your existing loadMapFile(String, int)
    private void loadMapFileByIndex(String fileName, int mapIndex) {
        try {
            String path = "assets" + File.separator + "maps" + File.separator + fileName;
            loadMapFile(fileName, mapIndex); // your original logic already handles file paths internally
        } catch (Exception e) {
            System.out.println("[TileManager] Failed to reload map " + mapIndex);
            e.printStackTrace();
        }
    }

    // ================== LOAD TILE IMAGES ==================
    // loads all tile sprites and assigns global IDs
    public void getTileImage() {

        String basePath1 = "assets" + File.separator + "tiles" + File.separator + "map01"
                + File.separator;
        String basePath2 = "assets" + File.separator + "tiles" + File.separator + "map02"
                + File.separator;

        try {

            // =============== MAP 1 TILES ===============
            // local IDs in map1.txt will start from tilesetStart[0]
            tilesetStart[0] = tileTypeCount;
            int localId = 0; // local ID for map 1 (0,1,2,...)

            // [Tile ID 0] GRASS (MAP 1)
            tile[tileTypeCount] = new Tile();
            File grassFile = new File(basePath1 + "tile0_Grass.png");
            if (grassFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(grassFile);
            }
            tile[tileTypeCount].collision = false; // walkable
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = GRASS");
            tileTypeCount++;
            localId++;

            // [Tile ID 1] PATH HORIZONTAL
            tile[tileTypeCount] = new Tile();
            File pathHFile = new File(basePath1 + "tile1_StraightPathHori.png");
            if (pathHFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(pathHFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = PATH_HORI");
            tileTypeCount++;
            localId++;

            // [Tile ID 2] PATH VERTICAL
            tile[tileTypeCount] = new Tile();
            File pathVFile = new File(basePath1 + "tile1_StraightPathVerti.png");
            if (pathVFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(pathVFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = PATH_VERT");
            tileTypeCount++;
            localId++;

            // [Tile ID 3] ROAD
            tile[tileTypeCount] = new Tile();
            File roadFile = new File(basePath1 + "tile7_RockyRoad.png");
            if (roadFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(roadFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = ROAD");
            tileTypeCount++;
            localId++;

            // [Tile ID 4] BORDER
            tile[tileTypeCount] = new Tile();
            File borderFile = new File(basePath1 + "tile8_Border.png");
            if (borderFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(borderFile);
            }
            tile[tileTypeCount].collision = true; // blocks movement
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = BORDER");
            tileTypeCount++;
            localId++;

            // [Tile ID 5] TELEPORT
            tile[tileTypeCount] = new Tile();
            File teleportFile = new File(basePath1 + "tile9_GrassTP.png");
            if (teleportFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(teleportFile);
            }
            tile[tileTypeCount].collision = false; // walkable, used as teleport trigger
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = TELEPORT");
            tileTypeCount++;
            localId++;

            // [Tile ID 6] BUSH
            tile[tileTypeCount] = new Tile();
            File bushFile = new File(basePath1 + "tile10_Bush.png");
            if (bushFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(bushFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = BUSH");
            tileTypeCount++;
            localId++;

            // [Tile ID 7] GRASS WITH FLOWERS
            tile[tileTypeCount] = new Tile();
            File grassFlowersFile = new File(basePath1 + "tile11_GrassWFlowers.png");
            if (grassFlowersFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(grassFlowersFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = GRASS_WITH_FLOWERS");
            tileTypeCount++;
            localId++;

            // [Tile ID 8] GRASS WITH FLOWERS
            tile[tileTypeCount] = new Tile();
            File FarmFile = new File(basePath1 + "tile12_Farm.png");
            if (FarmFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(FarmFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP1 TILE " + localId + " (global " + tileTypeCount + ") = FARM");
            tileTypeCount++;
            localId++;

            // =============== MAP 2 TILES ===============
            // local IDs in map2.txt will start from tilesetStart[1]
            tilesetStart[1] = tileTypeCount;
            localId = 0; // reset local ID for map 2

            // [Tile ID 0] Floor (MAP 2)
            tile[tileTypeCount] = new Tile();
            File flooorFile = new File(basePath2 + "tile0_Floor.png"); // can be same or different sprite
            if (flooorFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(flooorFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = FLOOR");
            tileTypeCount++;
            localId++;

            // [Tile ID 1] FloorTP (MAP 2)
            tile[tileTypeCount] = new Tile();
            File teleportFile2 = new File(basePath2 + "tile1_FloorTP.png");
            if (teleportFile2.exists()) {
                tile[tileTypeCount].image = ImageIO.read(teleportFile2);
            }
            tile[tileTypeCount].collision = false; // walkable, used as teleport trigger
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = TELEPORT");
            tileTypeCount++;
            localId++;

            // [Tile ID 2] PATINTERO (MAP 2)
            tile[tileTypeCount] = new Tile();
            File patinteroFile = new File(basePath2 + "tile02_road1.png"); // can be same or different sprite
            if (patinteroFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(patinteroFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = PATINTERO");
            tileTypeCount++;
            localId++;

            // [Tile ID 3] Road (MAP 2)
            tile[tileTypeCount] = new Tile();
            File road = new File(basePath2 + "road1.png"); // can be same or different sprite
            if (road.exists()) {
                tile[tileTypeCount].image = ImageIO.read(road);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = ROAD");
            tileTypeCount++;
            localId++;

            // [Tile ID 1] TP (MAP 3)
            tile[tileTypeCount] = new Tile();
            File teleportFile3 = new File(basePath2 + "tile1_FloorTP.png");
            if (teleportFile3.exists()) {
                tile[tileTypeCount].image = ImageIO.read(teleportFile3);
            }
            tile[tileTypeCount].collision = false; // walkable, used as teleport trigger
            System.out.println("MAP3 TILE " + localId + " (global " + tileTypeCount + ") = TELEPORT");
            tileTypeCount++;
            localId++;

            // more MAP 2 tiles can be added here (paths, borders, etc.)

            // =============== MAP 3 TILES ===============
            tilesetStart[2] = tileTypeCount;
            localId = 0;

            // [Tile ID 1] FLOOR2 (MAP 3)
            tile[tileTypeCount] = new Tile();
            File flooorFile2 = new File(basePath2 + "tile0_Floor.png"); // can be same or different sprite
            if (flooorFile2.exists()) {
                tile[tileTypeCount].image = ImageIO.read(flooorFile2);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = FLOOR2");
            tileTypeCount++;
            localId++;

            // [Tile ID 1] PATINTERO (MAP 2)
            tile[tileTypeCount] = new Tile();
            File patinteroFile1 = new File(basePath2 + "tile02_road1.png"); // can be same or different sprite
            if (patinteroFile1.exists()) {
                tile[tileTypeCount].image = ImageIO.read(patinteroFile1);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = PATINTERO");
            tileTypeCount++;
            localId++;

            tile[tileTypeCount] = new Tile();
            File waterFile = new File(basePath2 + "waterpuddle1.png"); // can be same or different sprite
            if (waterFile.exists()) {
                tile[tileTypeCount].image = ImageIO.read(waterFile);
            }
            tile[tileTypeCount].collision = false;
            System.out.println("MAP2 TILE " + localId + " (global " + tileTypeCount + ") = FLOOR2");
            tileTypeCount++;
            localId++;

        } catch (IOException e) {
            System.err.println("Error loading tile images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================== LOAD MAP FILES ==================

    // loads all map text files
    public void loadMap() {
        // map index 0 uses map1.txt
        loadMapFile("map1.txt", 0);

        // map index 1 uses map2.txt
        loadMapFile("map2.txt", 1);

        // map index 2 can use map3.txt if needed:
        loadMapFile("map3.txt", 2);
    }

    // reads one map file and fills mapTileNum[mapIndex]
    // map file uses local tile IDs (0,1,2,...) then converted to global IDs
    private void loadMapFile(String fileName, int mapIndex) {
        try {
            String mapPath = "assets" + File.separator + "maps" + File.separator + fileName;
            File mapFile = new File(mapPath);

            int tilesetOffset = tilesetStart[mapIndex]; // start index for this map's tiles

            if (!mapFile.exists()) {
                System.out.println("Map file not found: " + fileName + " → filling map " + mapIndex + " with grass.");
                for (int row = 0; row < gp.maxWorldRow; row++) {
                    for (int col = 0; col < gp.maxWorldCol; col++) {
                        // local ID 0 is grass, so use tilesetOffset
                        mapTileNum[mapIndex][col][row] = tilesetOffset;
                    }
                }
                return;
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new java.io.FileInputStream(mapFile)));

            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                // split by whitespace (spaces or tabs)
                String[] numbers = line.trim().split("\\s+");
                for (int col = 0; col < numbers.length && col < gp.maxWorldCol; col++) {
                    int localId = Integer.parseInt(numbers[col]); // 0,1,2,... from map file
                    int globalId = tilesetOffset + localId; // convert to global ID
                    mapTileNum[mapIndex][col][row] = globalId;
                }
                row++;
            }

            br.close();
            System.out.println("Map loaded successfully: " + fileName + " -> index " + mapIndex);

        } catch (Exception e) {
            System.err.println("Error loading map " + fileName + ": " + e.getMessage());
            e.printStackTrace();

            int tilesetOffset = tilesetStart[mapIndex];

            // fallback: fill with grass (local ID 0)
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[mapIndex][col][row] = tilesetOffset;
                }
            }
        }
    }

    // returns tile ID for current map, with bounds check
    public int getTileNum(int col, int row) {
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            // out of bounds → use local 0 of current map
            return tilesetStart[gp.currentMap];
        }
        return mapTileNum[gp.currentMap][col][row];
    }

    // ================== COLLISION CHECK ==================
    // checks if tile at [col,row] is blocking
    public boolean isBlocked(int col, int row) {

        // out-of-bounds tiles are treated as blocked
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            return true;
        }

        int tileNum = mapTileNum[gp.currentMap][col][row];

        // invalid tile index → not blocking
        if (tileNum < 0 || tileNum >= tile.length)
            return false;
        if (tile[tileNum] == null)
            return false;

        // use collision flag set in getTileImage()
        return tile[tileNum].collision;
    }

    // ================== DRAW TILES ==================
    public void draw(Graphics2D g2) {

        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {

                int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

                if (tileNum < 0 || tileNum >= tile.length ||
                        tile[tileNum] == null || tile[tileNum].image == null) {
                    // fallback to local 0 of current map
                    tileNum = tilesetStart[gp.currentMap];
                }

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int screenX = worldX - gp.cameraX;
                int screenY = worldY - gp.cameraY;

                // use panel size if getWidth/getHeight returns 0
                int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
                int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

                boolean visible = screenX + gp.tileSize > 0 &&
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
