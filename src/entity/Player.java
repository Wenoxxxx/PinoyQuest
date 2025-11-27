package src.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.core.GamePanel;
import src.core.KeyHandler;
import src.entity.skills.Skill;
import src.entity.skills.SkillManager;

public class Player extends Entity {

    GamePanel gamePanel;
    KeyHandler keyHandler;
    private final SkillManager skillManager;

    private int baseSpeed;
    private int speedModifier;

    private int maxHealth = 100;
    private int health = maxHealth;
    private int maxEnergy = 100;
    private int energy = maxEnergy;
    private int energyRegenCounter = 0;
    private boolean channeling = false;

    // WALKING ANIMATION ARRAYS (6 frames)
    public BufferedImage[] upFrames;
    public BufferedImage[] downFrames;
    public BufferedImage[] leftFrames;
    public BufferedImage[] rightFrames;

    // IDLE ANIMATIONS
    public BufferedImage[] idleUpFrames;    // 4 frames (uses idleB1-4)
    public BufferedImage[] idleDownFrames;  // 12 frames (uses idleF1-12)
    public BufferedImage[] idleLeftFrames;  // 12 frames (uses idleL1-12)
    public BufferedImage[] idleRightFrames; // 12 frames (uses idleR1-12)

    // ANIMATION CONTROL
    int frameIndex = 0;
    int frameCounter = 0;
    int animationSpeed = 8;       // walking
    int idleAnimationSpeed = 8;   // idle (down/left/right)
    int idleUpAnimationSpeed = 24; // slower idle for up (4 frames)

    boolean moving = false;
    boolean wasMoving = false;
    String lastDirection = "down";

    public int screenX;
    public int screenY;

    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        setDefaultValues();
        loadPlayerImages();
        skillManager = new SkillManager(this);
    }

    // INITIALIZATION OF PLAYER CHARACTER
    public void setDefaultValues() {
        // Spawn at world center
        int centerCol = gamePanel.maxWorldCol / 2;
        int centerRow = gamePanel.maxWorldRow / 2;
        worldX = centerCol * gamePanel.tileSize;
        worldY = centerRow * gamePanel.tileSize;

        // Center camera on player
        gamePanel.cameraX = worldX - (gamePanel.screenWidth / 2);
        gamePanel.cameraY = worldY - (gamePanel.screenHeight / 2);

        // Default movement and facing
        baseSpeed = 4;
        speedModifier = 0;
        refreshSpeed();
        direction = "down";

        maxHealth = 100;
        health = maxHealth;
        maxEnergy = 100;
        energy = maxEnergy;
        energyRegenCounter = 0;
        channeling = false;

        // Hitbox (1 tile wide, 1 tile tall, positioned at lower half of sprite)
        int hitboxWidth = gamePanel.tileSize;
        int hitboxHeight = gamePanel.tileSize;
        int offsetX = (gamePanel.tileSize * 2 - hitboxWidth) / 2;
        int offsetY = gamePanel.tileSize;
        solidArea.setBounds(offsetX, offsetY, hitboxWidth, hitboxHeight);
        solidAreaDefaultX = offsetX;
        solidAreaDefaultY = offsetY;
    }

    public void update() {
        int dx = 0, dy = 0;
        moving = false;
        boolean moved = false;

        // ----- INPUT -----
        if (keyHandler.upPressed) {
            dy -= 1;
            direction = "up";
            moving = true;
        }
        if (keyHandler.downPressed) {
            dy += 1;
            direction = "down";
            moving = true;
        }
        if (keyHandler.leftPressed) {
            dx -= 1;
            direction = "left";
            moving = true;
        }
        if (keyHandler.rightPressed) {
            dx += 1;
            direction = "right";
            moving = true;
        }

        // ----- MOVEMENT + COLLISION -----
        int moveX = 0;
        int moveY = 0;

        if (dx != 0 || dy != 0) {
            // Normalize diagonal speed
            if (dx != 0 && dy != 0) {
                double diagonalFactor = 1.0 / Math.sqrt(2.0);
                moveX = (int) Math.round(dx * speed * diagonalFactor);
                moveY = (int) Math.round(dy * speed * diagonalFactor);
            } else {
                moveX = dx * speed;
                moveY = dy * speed;
            }

            // Diagonal move attempt
            if (moveX != 0 && moveY != 0) {
                int nextX = worldX + moveX;
                int nextY = worldY + moveY;

                if (!gamePanel.collision.willCollide(this, nextX, nextY)) {
                    worldX = nextX;
                    worldY = nextY;
                    moved = true;
                } else {
                    // Try X only
                    boolean movedX = false;
                    boolean movedY = false;

                    if (!gamePanel.collision.willCollide(this, worldX + moveX, worldY)) {
                        worldX += moveX;
                        moved = true;
                        movedX = true;
                    }

                    // Try Y only
                    if (!gamePanel.collision.willCollide(this, worldX, worldY + moveY)) {
                        worldY += moveY;
                        moved = true;
                        movedY = true;
                    }

                    if (!movedX && !movedY) {
                        moving = false;
                    }
                }
            } else {
                // Axis-aligned move
                if (moveX != 0) {
                    if (!gamePanel.collision.willCollide(this, worldX + moveX, worldY)) {
                        worldX += moveX;
                        moved = true;
                    } else {
                        moving = false;
                    }
                }

                if (moveY != 0) {
                    if (!gamePanel.collision.willCollide(this, worldX, worldY + moveY)) {
                        worldY += moveY;
                        moved = true;
                    } else {
                        moving = false;
                    }
                }
            }
        }

        if (!moved) {
            moving = false;
        }

        handleSkillInput();

        // ----- ANIMATION -----
        updateAnimation();

        // ----- TELEPORT CHECK (AFTER MOVEMENT) -----
        handleTeleport();

        skillManager.update();
        regenerateEnergy();
    }

    // MAIN ANIMATION LOGIC (MOVING VS IDLE)
    private void updateAnimation() {
        // Reset animation when going from moving → idle or idle → moving
        if (moving != wasMoving) {
            frameIndex = 0;
            frameCounter = 0;
        }

        // Reset when direction changes
        if (!direction.equals(lastDirection)) {
            frameIndex = 0;
            frameCounter = 0;
        }

        if (moving) {
            // WALKING
            frameCounter++;
            if (frameCounter > animationSpeed) {
                frameCounter = 0;
                frameIndex = (frameIndex + 1) % 6; // 6 walking frames
            }
        } else {
            // IDLE
            frameCounter++;
            int currentIdleSpeed = direction.equals("up") ? idleUpAnimationSpeed : idleAnimationSpeed;

            if (frameCounter > currentIdleSpeed) {
                frameCounter = 0;
                switch (direction) {
                    case "up":
                        frameIndex = (frameIndex + 1) % 4;  // 4 idle up frames
                        break;
                    default:
                        frameIndex = (frameIndex + 1) % 12; // 12 idle for other dirs
                        break;
                }
            }
        }

        wasMoving = moving;
        lastDirection = direction;
    }

    // LOADING IMAGE HELPER (FROM FILE SYSTEM)
    private BufferedImage loadImageFromFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Image not found: " + path);
                return null;
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Failed to load: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // LOAD ALL PLAYER SPRITES
    public void loadPlayerImages() {

        upFrames = new BufferedImage[6];
        downFrames = new BufferedImage[6];
        leftFrames = new BufferedImage[6];
        rightFrames = new BufferedImage[6];

        idleUpFrames = new BufferedImage[12];
        idleDownFrames = new BufferedImage[12];
        idleLeftFrames = new BufferedImage[12];
        idleRightFrames = new BufferedImage[12];

        // Relative base path for assets
        String basePath =
                "src" + File.separator +
                "assets" + File.separator +
                "sprites" + File.separator +
                "player" + File.separator;

        // Walking frames
        for (int i = 0; i < 6; i++) {
            upFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "up" + (i + 1) + ".png");
            downFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "down" + (i + 1) + ".png");
            leftFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "left" + (i + 1) + ".png");
            rightFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "right" + (i + 1) + ".png");
        }

        // Idle UP (4 frames) → fill rest with last frame
        for (int i = 0; i < 4; i++) {
            idleUpFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleB" + (i + 1) + ".png");
        }
        BufferedImage lastUpFrame = idleUpFrames[3];
        if (lastUpFrame == null && idleUpFrames[0] != null) {
            lastUpFrame = idleUpFrames[0];
        }
        for (int i = 4; i < 12; i++) {
            idleUpFrames[i] = lastUpFrame;
        }

        // Idle DOWN (12 frames)
        for (int i = 0; i < 12; i++) {
            idleDownFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleF" + (i + 1) + ".png");
        }

        // Idle LEFT (12 frames)
        for (int i = 0; i < 12; i++) {
            idleLeftFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleL" + (i + 1) + ".png");
        }

        // Idle RIGHT (12 frames)
        for (int i = 0; i < 12; i++) {
            idleRightFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleR" + (i + 1) + ".png");
        }
    }

    // ====== TELEPORT HANDLER ======
    private void handleTeleport() {
        // find tile under the center of the player's hitbox
        int playerCol = (worldX + solidArea.x + solidArea.width / 2) / gamePanel.tileSize;
        int playerRow = (worldY + solidArea.y + solidArea.height / 2) / gamePanel.tileSize;

        int tileNum = gamePanel.tileManager.getTileNum(playerCol, playerRow);

        // --------- GLOBAL TELEPORT TILE IDS ---------
        // MAP 1: teleport is local ID 5 (see TileManager: "[Tile ID 5] TELEPORT")
        int map1TeleportGlobalId = gamePanel.tileManager.tilesetStart[0] + 5;

        // MAP 2: teleport is local ID 1 (see TileManager: "[Tile ID 1] FloorTP (MAP 2)")
        int map2TeleportGlobalId = gamePanel.tileManager.tilesetStart[1] + 1;

        // ===== MAP1 TO MAP2 =====
        if (gamePanel.currentMap == 0 && tileNum == map1TeleportGlobalId) {
            gamePanel.switchToMap(
                    1,      // newMapIndex -> map2.txt
                    16,     // playerTileCol in map2
                    1,      // playerTileRow in map2
                    "down"  // facing
            );
            return;
        }

        // ===== MAP2 TO MAP1 =====
        if (gamePanel.currentMap == 1 && tileNum == map2TeleportGlobalId) {
            gamePanel.switchToMap(
                    0,      // back to map1.txt
                    16,     // playerTileCol in map1
                    18,     // playerTileRow in map1
                    "up"    // facing
            );
            return;
        }
    }

    private void handleSkillInput() {
        if (skillManager == null || keyHandler == null) {
            return;
        }

        for (int slot = 0; slot < KeyHandler.SKILL_SLOT_COUNT; slot++) {
            if (keyHandler.consumeSkillTap(slot)) {
                skillManager.activateSlot(slot);
            }
        }
    }

    private void regenerateEnergy() {
        if (energy >= maxEnergy) {
            energyRegenCounter = 0;
            return;
        }

        int delay = channeling ? 10 : 25;
        energyRegenCounter++;
        if (energyRegenCounter >= delay) {
            energy = Math.min(maxEnergy, energy + 1);
            energyRegenCounter = 0;
        }
    }

    public void drawHud(Graphics2D g2) {
        if (g2 == null) {
            return;
        }

        int padding = 28;
        int panelWidth = 320;
        int panelHeight = 140;

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(padding - 18, padding - 30, panelWidth, panelHeight, 12, 12);

        int barWidth = 240;
        int barHeight = 14;
        int barX = padding;
        int barY = padding;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(Color.WHITE);
        g2.drawString("Health", barX, barY - 6);
        drawBar(g2, barX, barY, barWidth, barHeight, health / (double) maxHealth, new Color(210, 82, 82));
        g2.drawString(health + " / " + maxHealth, barX + barWidth + 12, barY + barHeight - 2);

        int energyY = barY + 32;
        g2.drawString("Energy", barX, energyY - 6);
        drawBar(g2, barX, energyY, barWidth, barHeight, energy / (double) maxEnergy, new Color(80, 165, 220));
        g2.drawString(energy + " / " + maxEnergy, barX + barWidth + 12, energyY + barHeight - 2);

        int skillYStart = energyY + 36;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));

        for (int i = 0; i < skillManager.getSlotCount(); i++) {
            Skill skill = skillManager.getSkill(i);
            if (skill == null) {
                continue;
            }

            String label = keyHandler.getSkillKeyLabel(i) + ": " + skill.getName();
            g2.setColor(Color.WHITE);
            g2.drawString(label, barX, skillYStart + (i * 18));

            String status;
            if (skill.isActive()) {
                status = "Active";
            } else if (skill.isOnCooldown()) {
                double seconds = skill.getCooldownTimer() / 60.0;
                status = String.format("CD %.1fs", seconds);
            } else if (energy < skill.getEnergyCost()) {
                status = "Need " + skill.getEnergyCost() + " EN";
            } else {
                status = "Ready";
            }

            g2.setColor(new Color(200, 200, 200));
            g2.drawString(status, barX + 170, skillYStart + (i * 18));
        }
    }

    private void drawBar(Graphics2D g2, int x, int y, int width, int height, double percent, Color fillColor) {
        g2.setColor(new Color(45, 45, 45));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        int actualWidth = (int) (width * Math.max(0, Math.min(1, percent)));
        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, actualWidth, height, 8, 8);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public String getDirection() {
        return direction;
    }

    public void addSpeedModifier(int amount) {
        speedModifier += amount;
        refreshSpeed();
    }

    private void refreshSpeed() {
        speed = Math.max(1, baseSpeed + speedModifier);
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        health = Math.min(maxHealth, health + amount);
    }

    // UPDATED: core damage logic
    public void damage(int amount) {
        if (amount <= 0) {
            return;
        }
        health = Math.max(0, health - amount);
        System.out.println("Player hit! Health: " + health);
        // if (health == 0) { /* handle death later */ }
    }

    // For enemies like White Lady to call
    public void takeDamage(int amount) {
        damage(amount);
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public boolean consumeEnergy(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (energy < amount) {
            return false;
        }
        energy -= amount;
        return true;
    }

    public void restoreEnergy(int amount) {
        if (amount <= 0) {
            return;
        }
        energy = Math.min(maxEnergy, energy + amount);
    }

    public void setChanneling(boolean channeling) {
        this.channeling = channeling;
    }

    public boolean isChanneling() {
        return channeling;
    }

    // RENDER SPRITE
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (moving) {
            switch (direction) {
                case "up":    image = upFrames[frameIndex]; break;
                case "down":  image = downFrames[frameIndex]; break;
                case "left":  image = leftFrames[frameIndex]; break;
                case "right": image = rightFrames[frameIndex]; break;
            }
        } else {
            switch (direction) {
                case "up":    image = idleUpFrames[frameIndex]; break;
                case "down":  image = idleDownFrames[frameIndex]; break;
                case "left":  image = idleLeftFrames[frameIndex]; break;
                case "right": image = idleRightFrames[frameIndex]; break;
            }
        }

        if (image != null) {
            int spriteWidth = gamePanel.tileSize * 4;
            int spriteHeight = gamePanel.tileSize * 4;

            // Get actual screen size from the panel
            int screenW = gamePanel.getWidth();
            int screenH = gamePanel.getHeight();

            // Use default size if panel not sized yet
            if (screenW <= 0) screenW = gamePanel.screenWidth;
            if (screenH <= 0) screenH = gamePanel.screenHeight;

            // Draw player in the center of the screen
            int drawX = (screenW / 2) - (spriteWidth / 2);
            int drawY = (screenH / 2) - (spriteHeight / 2);

            // keep these updated for enemies (like White Lady)
            this.screenX = drawX;
            this.screenY = drawY;

            g2.drawImage(image, drawX, drawY, spriteWidth, spriteHeight, null);
        }
    }
}
