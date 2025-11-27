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

    // mob id map: [mapIndex][col][row]
    private final int[][][] mobLayout;

    // Mob IDs
    public static final int MOB_NONE       = 0;
    public static final int MOB_WHITELADY  = 1;

    // Base directory for mob map files
    private static final String MOB_MAP_DIR =
            "src" + File.separator +
            "assets" + File.separator +
            "maps" + File.separator;

    public MobManager(GamePanel gp) {
        this.gp = gp;

        mobLayout = new int[TileManager.MAP_COUNT][gp.maxWorldCol][gp.maxWorldRow];

        loadMobMaps();
        spawnMobsForMap(gp.currentMap);
    }

    // =============== LOAD MOB MAP FILES =================
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
                        int mobId = Integer.parseInt(numbers[col]);
                        mobLayout[mapIndex][col][row] = mobId;
                    } catch (NumberFormatException e) {
                        mobLayout[mapIndex][col][row] = MOB_NONE;
                    }
                }

                // if line shorter than maxWorldCol, fill remaining with 0
                while (col < gp.maxWorldCol) {
                    mobLayout[mapIndex][col][row] = MOB_NONE;
                    col++;
                }

                row++;
            }

            // fill remaining rows with no mobs
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

    // =============== SPAWN MOBS FROM LAYOUT ==============
    public void spawnMobsForMap(int mapIndex) {
        if (mapIndex < 0 || mapIndex >= TileManager.MAP_COUNT) return;

        // Clear existing mobs for now (only White Lady list for you)
        gp.whiteLadies.clear();

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                int mobId = mobLayout[mapIndex][col][row];

                if (mobId == MOB_NONE) continue;

                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;

                switch (mobId) {
                    case MOB_WHITELADY -> {
                        gp.whiteLadies.add(new WhiteLady(gp, worldX, worldY));
                    }
                    // add more mob types later (e.g. MOB_SLIME, MOB_BAT, etc.)
                }
            }
        }

        System.out.println("[MobManager] Spawned mobs for map " + mapIndex);
    }

    // =============== UPDATE & DRAW =======================
    public void update() {
        // right now you only have WhiteLady
        for (WhiteLady wl : gp.whiteLadies) {
            wl.update();
        }
    }

    public void draw(Graphics2D g2) {
        for (WhiteLady wl : gp.whiteLadies) {
            wl.draw(g2);
        }
    }
}
