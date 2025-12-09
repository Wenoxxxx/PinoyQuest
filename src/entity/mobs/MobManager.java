package src.entity.mobs;

import src.core.GamePanel;
import src.tile.TileManager;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MobManager {

    private final GamePanel gp;

    // mob map: [mapIndex][col][row]
    private final int[][][] mobLayout;

    // Mob IDs
    public static final int MOB_NONE      = 0;
    public static final int MOB_WHITELADY = 1;
    public static final int MOB_SAWTRAP   = 2;

    private static final String MOB_MAP_DIR =
            
            "assets" + File.separator +
            "maps" + File.separator;

    public MobManager(GamePanel gp) {
        this.gp = gp;

        mobLayout = new int[TileManager.MAP_COUNT][gp.maxWorldCol][gp.maxWorldRow];

        loadMobMaps();  // only loads layout files
        // no spawning here anymore — prevents duplication
    }

    // ============================================================
    //                  LOAD ALL MOB MAP FILES
    // ============================================================
    private void loadMobMaps() {
        for (int mapIndex = 0; mapIndex < TileManager.MAP_COUNT; mapIndex++) {
            String fileName = MOB_MAP_DIR + "mobsmap" + (mapIndex + 1) + ".txt";
            loadMobMapFile(mapIndex, fileName);
        }
    }

    private void loadMobMapFile(int mapIndex, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[MobManager] No mob map file: " + filePath + " (skipping)");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                String[] numbers = line.trim().split("\\s+");
                int col = 0;

                for (; col < numbers.length && col < gp.maxWorldCol; col++) {
                    try {
                        mobLayout[mapIndex][col][row] = Integer.parseInt(numbers[col]);
                    } catch (NumberFormatException e) {
                        mobLayout[mapIndex][col][row] = MOB_NONE;
                    }
                }

                while (col < gp.maxWorldCol) {
                    mobLayout[mapIndex][col][row] = MOB_NONE;
                    col++;
                }

                row++;
            }

            while (row < gp.maxWorldRow) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mobLayout[mapIndex][col][row] = MOB_NONE;
                }
                row++;
            }

            System.out.println("[MobManager] Loaded mob map: " + filePath);

        } catch (IOException e) {
            System.err.println("[MobManager] Failed to read mob map: " + filePath);
            e.printStackTrace();
        }
    }

    // ============================================================
    //                   RESPAWN MOBS PER MAP
    // ============================================================
    public void spawnMobsForMap(int mapIndex) {
        if (mapIndex < 0 || mapIndex >= TileManager.MAP_COUNT) return;

        System.out.println("[MobManager] Respawning mobs for map " + mapIndex);

        // Always clear current mobs before spawning
        gp.whiteLadies.clear();
        gp.sawTraps.clear();

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {

                int mobId = mobLayout[mapIndex][col][row];
                if (mobId == MOB_NONE) continue;

                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;

                switch (mobId) {
                    case MOB_WHITELADY -> gp.whiteLadies.add(
                            new WhiteLady(gp, worldX, worldY)
                    );

                    case MOB_SAWTRAP -> gp.sawTraps.add(
                            new SawTrap(gp, worldX, worldY)
                    );
                }
            }
        }
    }

    // ============================================================
    //                        UPDATE & DRAW
    // ============================================================
    public void update() {
        for (WhiteLady wl : gp.whiteLadies) wl.update();
        for (SawTrap st : gp.sawTraps) st.update();
    }

    public void draw(Graphics2D g2) {
        for (WhiteLady wl : gp.whiteLadies) wl.draw(g2);
        for (SawTrap st : gp.sawTraps) st.draw(g2);
    }
}
