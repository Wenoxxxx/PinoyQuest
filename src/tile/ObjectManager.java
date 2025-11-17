package src.tile;

import src.core.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
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

    // 1. Define object TYPES (0 = house, 1 = rock, ...)
    private void loadObjectTypes() {
        try {
            // HOUSE = type index 0
            GameObject house = new GameObject();
            house.name = "house";
            house.image = ImageIO.read(new File(OBJECT_DIR + "house2.png"));
            house.collision = true;
            house.width = 5;     // 4 tiles wide
            house.height = 4;    // 3 tiles tall
            objectTypes[objectTypeCount++] = house;

            // ROCK = type index 1
            GameObject rock = new GameObject();
            rock.name = "rock";
            rock.image = ImageIO.read(new File(OBJECT_DIR + "rock.png"));
            rock.collision = true;
            rock.width = 1;
            rock.height = 1;
            objectTypes[objectTypeCount++] = rock;

        } catch (IOException e) {
            System.out.println("ERROR: Cannot load object images.");
            e.printStackTrace();
        }
    }

    // 2. Load objects1.txt as a grid exactly like the tile map
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

    // 3. Draw objects using the same camera as TileManager
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
}
