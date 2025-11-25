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

    // For each map, starting index of its object types in objectTypes[]
    public final int[] objectSetStart;

    // === MULTI-MAP SUPPORT ===
    // placedObjects[mapIndex][i]
    public final GameObject[][] placedObjects;
    // how many objects are used per map
    public final int[] placedObjectCount;

    // Base directories
    private static final String OBJECT_ROOT_DIR =
            "src" + File.separator + "assets" + File.separator + "objects" + File.separator;
    private static final String MAP_DIR =
            "src" + File.separator + "assets" + File.separator + "maps" + File.separator;

    // Number of maps (match TileManager)
    private static final int MAP_COUNT = TileManager.MAP_COUNT;

    public ObjectManager(GamePanel gp) {
        this.gp = gp;

        objectTypes = new GameObject[64];   // all object types across all maps
        objectSetStart = new int[MAP_COUNT];

        // For each map, allow up to width * height objects
        int maxPerMap = gp.maxWorldCol * gp.maxWorldRow;
        placedObjects = new GameObject[MAP_COUNT][maxPerMap];
        placedObjectCount = new int[MAP_COUNT];

        loadObjectTypes();
        System.out.println("Loaded object types: " + objectTypeCount);

        // === Load per-map object layouts ===
        loadObjectMap("objects1.txt", 0); // objects for map index 0 (map1.txt)
        loadObjectMap("objects2.txt", 1); // objects for map index 1 (map2.txt)
        // loadObjectMap("objects3.txt", 2); // if you add a 3rd map
    }

    // Helper: register one object type
    private void addObjectType(
            String basePath,
            String fileName,
            String debugName,
            String objectName,
            boolean collision,
            boolean overlapWithPlayer,   // 👈 NEW PARAM
            int widthTiles,
            int heightTiles
    ) throws IOException {

        if (objectTypeCount >= objectTypes.length) {
            System.out.println("WARNING: objectTypes[] is full, cannot register: " + debugName);
            return;
        }

        GameObject obj = new GameObject();
        obj.name = objectName;
        obj.collision = collision;
        obj.width = widthTiles;
        obj.height = heightTiles;
        obj.overlapWithPlayer = overlapWithPlayer; // 👈 set flag

        File imgFile = new File(basePath + fileName);
        if (imgFile.exists()) {
            obj.image = ImageIO.read(imgFile);
        } else {
            System.out.println("WARNING: Object image not found: " + imgFile.getPath());
        }

        // ===== DEFAULT HITBOX = BASE OF THE REAL SPRITE (IMAGE PIXELS) =====
        if (obj.image != null) {
            int spriteW = obj.image.getWidth();
            int spriteH = obj.image.getHeight();

            // Only bottom 30% collidable (front/base area)
            int baseTop = (int) (spriteH * 0.7);
            int baseHeight = spriteH - baseTop;
            if (baseHeight < 1) baseHeight = 1;

            obj.solidArea = new Rectangle(
                    0,
                    baseTop,
                    spriteW,
                    baseHeight
            );
        } else {
            // Fallback if image missing
            obj.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * widthTiles,
                    gp.tileSize * heightTiles
            );
        }

        objectTypes[objectTypeCount] = obj;
        System.out.println("OBJECT TYPE " + objectTypeCount + " = " + debugName +
                " (file: " + imgFile.getPath() + ")");
        objectTypeCount++;
    }

    // OBJECT LOADER
    private void loadObjectTypes() {
        String basePath1 = OBJECT_ROOT_DIR + "map01" + File.separator;
        String basePath2 = OBJECT_ROOT_DIR + "map02" + File.separator;

        try {
            // =============== MAP 1 OBJECT TYPES ===============
            objectSetStart[0] = objectTypeCount;
            int localId = 0;

            // big houses → overlap true
            addObjectType(basePath1,
                    "house1.png",
                    "MAP1_HOUSE1",
                    "house",
                    true,   // collision
                    true,   // overlapWithPlayer (can walk behind)
                    6, 5);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = HOUSE1");

            addObjectType(basePath1,
                    "house2.png",
                    "MAP1_HOUSE2",
                    "house",
                    true,
                    true,   // overlaps
                    6, 5);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = HOUSE2");

            // tree → overlap true (walk behind tree)
            addObjectType(basePath1,
                    "tree1.png",
                    "MAP1_TREE1",
                    "tree",
                    true,
                    true,   // overlaps
                    2, 2);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = TREE1");

          
            addObjectType(basePath1,
                    "bench1.png",
                    "MAP1_BENCH1",
                    "bench",
                    false,
                    false,  // NO overlap (always drawn in front)
                    2, 2);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = BENCH1");


            addObjectType(basePath1,
                    "well1.png",
                    "MAP1_WELL1",
                    "well",
                    true,
                    true,   // overlaps
                    2, 2);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = WELL1");

            addObjectType(basePath1,
                    "boxes1.png",
                    "MAP1_BOXES1",
                    "boxes",
                    false,
                    false,  // like "small rocks" → NO overlap
                    3, 3);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = BOXES1");

            addObjectType(basePath1,
                    "tent1.png",
                    "MAP1_TENT1",
                    "tent",
                    true,
                    true,   // big tent: overlap
                    3, 3);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = TENT1");

            addObjectType(basePath1,
                    "tent2.png",
                    "MAP1_TENT2",
                    "tent",
                    true,
                    true,   // overlap
                    3, 2);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = TENT2");

            // small-ish wheelbarrow → up to you
            addObjectType(basePath1,
                    "wheelbarrow1.png",
                    "MAP1_WHEELBARROW1",
                    "wheelbarrow",
                    true,
                    false,  // example: no overlap
                    2, 1);
            System.out.println("MAP1 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = WHEELBARROW1");


            
            // =============== MAP 2 OBJECT TYPES ===============
            objectSetStart[1] = objectTypeCount;
            localId = 0;

            addObjectType(basePath2,
                    "Crystal_shadow1_1.png",
                    "CRYSTAL1",
                    "crystal1",
                    true,
                    true,   // big crystal: overlap
                    3, 3);
            System.out.println("MAP2 OBJ " + localId++ + " (global " + (objectTypeCount - 1) + ") = CRYSTAL1");

        } catch (IOException e) {
            System.out.println("ERROR: Cannot load object images.");
            e.printStackTrace();
        }
    }


    // === MULTI-MAP OBJECT LOADING ===
    // Load objectsX.txt as a grid with LOCAL IDs, like tiles
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
        int setStart = objectSetStart[mapIndex];

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

                    int globalIndex = -1; // default = empty

                    if (col < values.length) {
                        int localId = Integer.parseInt(values[col]); // 0,1,2,... per map
                        if (localId >= 0) {
                            globalIndex = setStart + localId;
                        }
                    }

                    if (globalIndex >= 0 && globalIndex < objectTypeCount) {

                        GameObject baseType = objectTypes[globalIndex];
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

            // --- SCALE HITBOX FROM IMAGE SPACE → WORLD SPACE ---

            // The sprite is drawn scaled to tileSize * width/height
            int drawW = gp.tileSize * obj.width;
            int drawH = gp.tileSize * obj.height;

            int imgW = obj.image.getWidth();
            int imgH = obj.image.getHeight();

            if (imgW <= 0 || imgH <= 0) {
                continue; // safety
            }

            double scaleX = (double) drawW / imgW;
            double scaleY = (double) drawH / imgH;

            int hitX = obj.worldX + (int) Math.round(obj.solidArea.x * scaleX);
            int hitY = obj.worldY + (int) Math.round(obj.solidArea.y * scaleY);
            int hitW = (int) Math.round(obj.solidArea.width  * scaleX);
            int hitH = (int) Math.round(obj.solidArea.height * scaleY);

            // Object collision box in world coordinates
            Rectangle objBox = new Rectangle(hitX, hitY, hitW, hitH);

            if (entityBox.intersects(objBox)) { 
                return true;
            }
        }

        return false;
    }

    // ================== LAYERED DRAWING FOR OVERLAP EFFECT ==================
    public void drawBehindPlayer(Graphics2D g2, int playerFeetRow) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        int mapIndex = gp.currentMap;
        int count = placedObjectCount[mapIndex];
        GameObject[] mapObjects = placedObjects[mapIndex];

        for (int i = 0; i < count; i++) {
            GameObject obj = mapObjects[i];
            if (obj == null || obj.image == null) continue;

            // Objects that do not overlap the player are always drawn in front
            if (!obj.overlapWithPlayer) {
                continue;
            }

            int worldX = obj.worldX;
            int worldY = obj.worldY;

            int screenX = worldX - gp.cameraX;
            int screenY = worldY - gp.cameraY;

            boolean visible =
                    screenX + gp.tileSize * obj.width  > 0 &&
                    screenX < screenW &&
                    screenY + gp.tileSize * obj.height > 0 &&
                    screenY < screenH;

            if (!visible) continue;

            // Bottom of sprite in world → base tile row
            int objBottomY = obj.worldY + gp.tileSize * obj.height;
            int objBaseRow = (objBottomY - 1) / gp.tileSize;

            // ---- RULE: if object's base row is ABOVE player's feet row → BEHIND ----
            if (objBaseRow < playerFeetRow) {
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


    public void drawInFrontOfPlayer(Graphics2D g2, int playerFeetRow) {

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

            if (!visible) continue;

            int objBottomY = obj.worldY + gp.tileSize * obj.height;
            int objBaseRow = (objBottomY - 1) / gp.tileSize;

            if (obj.overlapWithPlayer) {
                // ---- RULE: base row AT or BELOW player's feet row → IN FRONT ----
                if (objBaseRow >= playerFeetRow) {
                    g2.drawImage(
                            obj.image,
                            screenX,
                            screenY,
                            gp.tileSize * obj.width,
                            gp.tileSize * obj.height,
                            null
                    );
                }
            } else {
                // Non-overlap objects: always drawn in this "front" pass
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

}
