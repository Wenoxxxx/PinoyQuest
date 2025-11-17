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

    // Actual placed objects on the map (aligned to the tile grid)
    public final GameObject[] placedObjects;
    public int placedObjectCount = 0;

    // Base directories
    private static final String OBJECT_DIR = "src/assets/objects/"; // adjust if different
    private static final String MAP_DIR    = "src/assets/maps/";

    public ObjectManager(GamePanel gp) {
        this.gp = gp;

        objectTypes   = new GameObject[32];   // distinct kinds: house, rock, etc.
        // One possible object per tile cell: width * height (e.g., 31 * 21)
        placedObjects = new GameObject[gp.maxWorldCol * gp.maxWorldRow];

        loadObjectTypes();
        System.out.println("Loaded object types: " + objectTypeCount);

        loadObjectMap("objects1.txt");
        System.out.println("Placed objects: " + placedObjectCount);
    }

    // Define object TYPES (0 = house, 1 = rock, ...)
    private void loadObjectTypes() {
        try {
            // ===== HOUSE = type index 0 =====
            GameObject house1 = new GameObject();
            house1.name = "house";
            house1.image = ImageIO.read(new File(OBJECT_DIR + "house1.png"));
            house1.collision = true;      // not walkable
            house1.width = 5;             // tiles wide
            house1.height = 4;            // tiles tall
            // full-sprite hitbox (you can shrink this later if needed)
            house1.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * house1.width,
                    gp.tileSize * house1.height
            );
            objectTypes[objectTypeCount++] = house1;

            // ===== ROCK = type index 1 =====
            GameObject house2 = new GameObject();
            house2.name = "house";
            house2.image = ImageIO.read(new File(OBJECT_DIR + "house2.png"));
            house2.collision = true;      // not walkable
            house2.width = 5;             // tiles wide
            house2.height = 4;            // tiles tall
            house2.solidArea = new Rectangle(
                    0,
                    0,
                    gp.tileSize * house2.width,
                    gp.tileSize * house2.height
            );
            objectTypes[objectTypeCount++] = house2;

            // Example: WALKABLE decoration
            /*
            GameObject flower = new GameObject();
            flower.name = "flower";
            flower.image = ImageIO.read(new File(OBJECT_DIR + "flower.png"));
            flower.collision = false;    // walkable
            flower.width = 1;
            flower.height = 1;
            flower.solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
            objectTypes[objectTypeCount++] = flower;
            */

        } catch (IOException e) {
            System.out.println("ERROR: Cannot load object images.");
            e.printStackTrace();
        }
    }

    // Load objects1.txt as a grid exactly like the tile map
    private void loadObjectMap(String fileName) {
        File mapFile = new File(MAP_DIR + fileName);

        if (!mapFile.exists()) {
            System.out.println("Object map not found: " + fileName);
            return;
        }

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

                        if (placedObjectCount >= placedObjects.length) {
                            System.out.println("WARNING: placedObjects[] is full, skipping extra objects.");
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

                        placedObjects[placedObjectCount++] = obj;

                        // Debug
                        System.out.println("Placed " + obj.name +
                                " at row=" + row + ", col=" + col +
                                " (worldX=" + obj.worldX + ", worldY=" + obj.worldY + ")");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR: Cannot load object map.");
            e.printStackTrace();
        }
    }

    // Draw objects using the same camera as TileManager
    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        for (int i = 0; i < placedObjectCount; i++) {
            GameObject obj = placedObjects[i];
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

    //Collision helper: used by GamePanel.isObjectBlocked → Collision.willCollide
    public boolean isBlocked(int nextWorldX, int nextWorldY, Rectangle entityArea) {

        if (entityArea == null) return false;

        // Entity collision box at its next world position
        Rectangle entityBox = new Rectangle(
                nextWorldX + entityArea.x,
                nextWorldY + entityArea.y,
                entityArea.width,
                entityArea.height
        );

        for (int i = 0; i < placedObjectCount; i++) {
            GameObject obj = placedObjects[i];
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
