package src.entity.mobs;

import src.core.GamePanel;
import src.entity.Player;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Map3EnemySpawner {

    private GamePanel gp;
    private List<Enemy> enemies = new ArrayList<>();

    // Boss
    private BossEnemy boss = null;
    private boolean bossSpawned = false;

    private boolean wavesStarted = false;
    private int currentWave = 0;

    private long waveStartTime = 0;

    // Number of enemies per wave (split between tambay and dogs)
    private final int TAMBAY_PER_WAVE = 4;
    private final int DOGS_PER_WAVE = 4;

    // Time per wave (ms)
    private final long[] WAVE_TIMERS = {
            5,
            5_000,   // Wave 1
            5_000,   // Wave 2///////////////////////////////////////////////////////////
            5_000    // Wave 3
    };

    private Random rand = new Random();

    public Map3EnemySpawner(GamePanel gp) {
        this.gp = gp;
    }

    // Called once when entering map 3
    public void startWaves(Player player) {
        if (wavesStarted) return;

        wavesStarted = true;
        currentWave = 1;

        waveStartTime = System.currentTimeMillis();
        showWaveMessage();
        spawnWave(player);
    }

    // Spawn regular wave enemies (tambay and dogs together)
    private void spawnWave(Player player) {
        // Spawn Tambay enemies
        for (int i = 0; i < TAMBAY_PER_WAVE; i++) {
            int x = rand.nextInt(gp.worldWidth - 200) + 100;
            int y = rand.nextInt(gp.worldHeight - 200) + 100;

            TambayEnemy tambay = new TambayEnemy(gp, x, y, player);
            enemies.add(tambay);
        }

        // Spawn Dog enemies
        for (int i = 0; i < DOGS_PER_WAVE; i++) {
            int x = rand.nextInt(gp.worldWidth - 200) + 100;
            int y = rand.nextInt(gp.worldHeight - 200) + 100;

            DogEnemy dog = new DogEnemy(gp, x, y, player);
            enemies.add(dog);
        }
    }

    // Update logic
    public void update(Player player) {
        if (!wavesStarted) return;

        long now = System.currentTimeMillis();
        long elapsed = now - waveStartTime;

        // Update enemies
        enemies.removeIf(e -> e.isDead());
        for (Enemy e : enemies) e.update();
        for (Enemy e : enemies) e.avoidOverlap(enemies);

        // If boss exists, update boss too
        if (boss != null && !boss.isDead()) {
            boss.update();
        }

        // Timed wave control
        if (currentWave <= 3 && elapsed >= WAVE_TIMERS[currentWave]) {

            currentWave++;

            // Boss triggers AFTER wave 3
            if (currentWave == 4 && !bossSpawned) {
                spawnBoss(player);
                return;
            }

            // Start next wave
            if (currentWave <= 3) {
                waveStartTime = now;
                showWaveMessage();
                spawnWave(player);
            }
        }
    }

    // Boss spawning
    private void spawnBoss(Player player) {
        bossSpawned = true;

        gp.ui.showMessage("BOSS INCOMING!");

        int x = gp.worldWidth / 2;
        int y = gp.worldHeight / 2;

        boss = new BossEnemy(gp, x, y, player);

        System.out.println("[BOSS] Boss spawned!");
    }

    // Drawing all mobs
    public void draw(java.awt.Graphics2D g2) {
        for (Enemy e : enemies) e.draw(g2);

        if (boss != null && !boss.isDead()) {
            boss.draw(g2);
        }
    }

    public boolean applyPlayerAttack(Rectangle hitbox, int damage) {
        if (hitbox == null || damage <= 0) return false;

        boolean hitAny = false;

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Rectangle enemyBox = new Rectangle(
                    enemy.worldX + enemy.hitbox.x,
                    enemy.worldY + enemy.hitbox.y,
                    enemy.hitbox.width,
                    enemy.hitbox.height
            );
            if (hitbox.intersects(enemyBox)) {
                enemy.damage(damage);
                if (enemy.isDead()) {
                    iterator.remove();
                }
                hitAny = true;
            }
        }

        if (boss != null && !boss.isDead()) {
            Rectangle bossBox = new Rectangle(
                    boss.worldX + boss.hitbox.x,
                    boss.worldY + boss.hitbox.y,
                    boss.hitbox.width,
                    boss.hitbox.height
            );
            if (hitbox.intersects(bossBox)) {
                boss.damage(damage);
                if (boss.isDead()) {
                    gp.ui.showMessage("Boss defeated!");
                }
                hitAny = true;
            }
        }

        return hitAny;
    }

    private void showWaveMessage() {
        gp.ui.showMessage("WAVE " + currentWave);
    }
}
