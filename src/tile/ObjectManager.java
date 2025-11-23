package src.tile;

import src.core.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;   // includes Rectangle and Graphics2D
import java.io.*;

public class ObjectManager {

    private final GamePanel gp;

    // Object type definitions (house, rock, etc.) – templates
    private final GameObject[] objectTypes;
    private int objectTypeCount = 0;

    // === MULTI-MAP SUPPORT ===
    // placedObjects[mapIndex][i]
    public final GameObject[][] placedObjects;
    // how many objects are used per map
    public final int[] placedObjectCount;

    // Base directories
    private static final String OBJECT_DIR = "src/assets/objects/"; // adjust if different
    private static final String MAP_DIR    = "src/assets/maps/";

    // Number of maps (match TileManager)
    private static final int MAP_COUNT = TileManager.MAP_COUNT;

    public ObjectManager(GamePanel gp) {
        this.gp = gp;

        objectTypes = new GameObject[32];   // distinct kinds: house, rock, etc.

        // For each map, allow up to width * height objects
        int maxPerMap = gp.maxWorldCol * gp.maxWorldRow;
        placedObjects = new GameObject[MAP_COUNT][maxPerMap];
        placedObjectCount = new int[MAP_COUNT];

        loadObjectTypes();
        System.out.println("Loaded object types: " + objectTypeCount);

        // === Load per-map object layouts ===
        loadObjectMap("objects1.txt", 0); // objects for map index 0 (map1.txt)
        loadObjectMap("objects2.txt", 1); // objects for map index 1 (map2.txt)
    }

    // Define object TYPES (0 = house1, 1 = house2, 2 = tree, 3 = bench, ...)
    private void loadObjectTypes() {
        try {
            // [TYPE ID 0] HOUSE1
            GameObject house1 = new GameObject();
            house1.name = "house";
            house1.image = ImageIO.read(new File(OBJECT_DIR + "house1.png"));
            house1.collision = true;      // not walkable
            house1.width = 6;             // tiles wide
            house1.height = 5;            // tiles tall
            // full-sprite hitbox (you can shrink this later if needed)
            house1.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * house1.width,
                    gp.tileSize * house1.height
            );
            objectTypes[objectTypeCount++] = house1;

            // [TYPE ID 1] HOUSE2
            GameObject house2 = new GameObject();
            house2.name = "house";
            house2.image = ImageIO.read(new File(OBJECT_DIR + "house2.png"));
            house2.collision = true;      // not walkable
            house2.width = 6;             // tiles wide
            house2.height = 5;            // tiles tall
            house2.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * house2.width,
                    gp.tileSize * house2.height
            );
            objectTypes[objectTypeCount++] = house2;

            // [TYPE ID 2] TREE1
            GameObject tree1 = new GameObject();
            tree1.name = "tree";
            tree1.image = ImageIO.read(new File(OBJECT_DIR + "tree1.png"));
            tree1.collision = true;      // not walkable
            tree1.width = 2;             // tiles wide
            tree1.height = 2;            // tiles tall
            tree1.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * tree1.width,
                    gp.tileSize * tree1.height
            );
            objectTypes[objectTypeCount++] = tree1;

            // [TYPE ID 3] BENCH
            GameObject bench1 = new GameObject();
            bench1.name = "bench";
            bench1.image = ImageIO.read(new File(OBJECT_DIR + "bench1.png"));
            bench1.collision = false;     // walkable
            bench1.width = 2;             // tiles wide
            bench1.height = 2;            // tiles tall
            bench1.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * bench1.width,
                    gp.tileSize * bench1.height
            );
            objectTypes[objectTypeCount++] = bench1;

            // [TYPE ID 4] WELL
            GameObject well = new GameObject();
            well.name = "well";
            well.image = ImageIO.read(new File(OBJECT_DIR + "well1.png"));
            well.collision = true;     // walkable
            well.width = 2;             // tiles wide
            well.height = 2;            // tiles tall
            well.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * well.width,
                    gp.tileSize * well.height
            );
            objectTypes[objectTypeCount++] = well;

            // [TYPE ID 5] Boxes
            GameObject boxes = new GameObject();
            boxes.name = "boxes";
            boxes.image = ImageIO.read(new File(OBJECT_DIR + "boxes1.png"));
            boxes.collision = false;     // walkable
            boxes.width = 3;             // tiles wide
            boxes.height = 3;            // tiles tall
            boxes.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * boxes.width,
                    gp.tileSize * boxes.height
            );
            objectTypes[objectTypeCount++] = boxes;

            // [TYPE ID 6] Tent 1
            GameObject tent = new GameObject();
            tent.name = "tent";
            tent.image = ImageIO.read(new File(OBJECT_DIR + "tent1.png"));
            tent.collision = true;     // walkable
            tent.width = 3;             // tiles wide
            tent.height = 3;            // tiles tall
            tent.solidArea = new Rectangle(
                    0,
                    0, 
                    gp.tileSize * tent.width,
                    gp.tileSize * tent.height
            );
            objectTypes[objectTypeCount++] = tent;

            // [TYPE ID 7] Tent 2
            GameObject tent2 = new GameObject();
            tent2.name = "tent";
            tent2.image = ImageIO.read(new File(OBJECT_DIR + "tent2.png"));
            tent2.collision = true;     // walkable
            tent2.width = 3;             // tiles wide
            tent2.height = 2;            // tiles tall
            tent2.solidArea = new Rectangle(
                    0,
                    0, 
                    gp.tileSize * tent2.width,
                    gp.tileSize * tent2.height
            );
            objectTypes[objectTypeCount++] = tent2;

            // [TYPE ID 8] Wheelbarrow
            GameObject wheelbarrow = new GameObject();
            wheelbarrow.name = "wheelbarrow";
            wheelbarrow.image = ImageIO.read(new File(OBJECT_DIR + "wheelbarrow1.png"));
            wheelbarrow.collision = true;     // walkable
            wheelbarrow.width = 2;             // tiles wide
            wheelbarrow.height = 1;            // tiles tall
            wheelbarrow.solidArea = new Rectangle(
                    0,
                    0, 
                    gp.tileSize * wheelbarrow.width,
                    gp.tileSize * wheelbarrow.height
            );
            objectTypes[objectTypeCount++] = wheelbarrow;

            // [TYPE ID 9] Big Rock
            GameObject bigRock = new GameObject();
            bigRock.name = "bigRock";
            bigRock.image = ImageIO.read(new File(OBJECT_DIR + "bigrockwgrass1.png"));
            bigRock.collision = true;     // walkable
            bigRock.width = 3;             // tiles wide
            bigRock.height = 3;            // tiles tall
            bigRock.solidArea = new Rectangle(
                    0,
                    0, 
                    gp.tileSize * bigRock.width,
                    gp.tileSize * bigRock.height
            );
            objectTypes[objectTypeCount++] = bigRock;



        } catch (IOException e) {
            System.out.println("ERROR: Cannot load object images.");
            e.printStackTrace();
        }
    }

    // === MULTI-MAP OBJECT LOADING ===
    // Load objectsX.txt as a grid exactly like the tile map, for a specific mapIndex
    private void loadObjectMap(String fileName, int mapIndex) {
        if (mapIndex < 0 || mapIndex >= MAP_COUNT) {
            System.out.println("Invalid mapIndex in loadObjectMap: " + mapIndex);
            return;
        }

        File mapFile = new File(MAP_DIR + fileName);

        if (!mapFile.exists()) {
            System.out.println("Object map not found: " + fileName +
                               " -> no objects for map " + mapIndex);
            return;
        }

        int maxPerMap = placedObjects[mapIndex].length;
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(mapFile))) {

            for (int row = 0; row < gp.maxWorldRow; row++) {

                String line = br.readLine();
                if (line == null) {
                    // No more lines: treat remaining rows as empty
                    break;
                }

                // Split on ANY whitespace
                String[] values = line.trim().split("\\s+");

                for (int col = 0; col < gp.maxWorldCol; col++) {

                    int index = -1; // default = empty

                    if (col < values.length) {
                        index = Integer.parseInt(values[col]);
                    }

                    // -1 = no object on that tile
                    if (index >= 0 && index < objectTypeCount) {

                        GameObject baseType = objectTypes[index];
                        if (baseType == null) continue;

                        if (count >= maxPerMap) {
                            System.out.println("WARNING: placedObjects[" + mapIndex + "] is full, skipping extra objects.");
                            placedObjectCount[mapIndex] = count;
                            return;
                        }

                        GameObject obj = new GameObject();
                        obj.name = baseType.name;
                        obj.image = baseType.image;
                        obj.collision = baseType.collision;
                        obj.width = baseType.width;
                        obj.height = baseType.height;

                        // copy hitbox size/offset
                        if (baseType.solidArea != null) {
                            obj.solidArea = new Rectangle(baseType.solidArea);
                        } else {
                            obj.solidArea = new Rectangle(
                                    0, 0,
                                    gp.tileSize * obj.width,
                                    gp.tileSize * obj.height
                            );
                        }

                        // This cell’s world coords line up with the TILE grid
                        obj.worldX = col * gp.tileSize;
                        obj.worldY = row * gp.tileSize;

                        placedObjects[mapIndex][count++] = obj;

                        // Debug
                        System.out.println("Map " + mapIndex + " → placed " + obj.name +
                                " at row=" + row + ", col=" + col +
                                " (worldX=" + obj.worldX + ", worldY=" + obj.worldY + ")");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR: Cannot load object map: " + fileName);
            e.printStackTrace();
        }

        placedObjectCount[mapIndex] = count;
        System.out.println("Map " + mapIndex + " total placed objects: " + count);
    }

    // Draw objects using the same camera as TileManager
    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        int mapIndex = gp.currentMap;

        int count = placedObjectCount[mapIndex];
        GameObject[] mapObjects = placedObjects[mapIndex];

        for (int i = 0; i < count; i++) {
            GameObject obj = mapObjects[i];
            if (obj == null || obj.image == null) continue;

            int worldX = obj.worldX;
            int worldY = obj.worldY;

            int screenX = worldX - gp.cameraX;
            int screenY = worldY - gp.cameraY;

            boolean visible =
                    screenX + gp.tileSize * obj.width  > 0 &&
                    screenX < screenW &&
                    screenY + gp.tileSize * obj.height > 0 &&
                    screenY < screenH;

            if (visible) {
                g2.drawImage(
                        obj.image,
                        screenX,
                        screenY,
                        gp.tileSize * obj.width,
                        gp.tileSize * obj.height,
                        null
                );
            }
        }
    }

    // Collision helper: used by GamePanel.isObjectBlocked → Collision.willCollide
    public boolean isBlocked(int nextWorldX, int nextWorldY, Rectangle entityArea) {

        if (entityArea == null) return false;

        // Entity collision box at its next world position
        Rectangle entityBox = new Rectangle(
                nextWorldX + entityArea.x,
                nextWorldY + entityArea.y,
                entityArea.width,
                entityArea.height
        );

        int mapIndex = gp.currentMap;
        int count = placedObjectCount[mapIndex];
        GameObject[] mapObjects = placedObjects[mapIndex];

        for (int i = 0; i < count; i++) {
            GameObject obj = mapObjects[i];
            if (obj == null || obj.image == null) continue;
            if (!obj.collision) continue;                // only block if collision = true
            if (obj.solidArea == null) continue;

            // Object collision box in world coordinates
            Rectangle objBox = new Rectangle(
                    obj.worldX + obj.solidArea.x,
                    obj.worldY + obj.solidArea.y,
                    obj.solidArea.width,
                    obj.solidArea.height
            );

            if (entityBox.intersects(objBox)) {
                return true;
            }
        }

        return false;
    }
}
