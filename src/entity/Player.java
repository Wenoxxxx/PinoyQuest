package src.entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.core.KeyHandler;

import src.entity.skills.SkillManager;
import src.items.Item;
import src.items.weapons.Hanger;
import src.items.weapons.Tsinelas;

public class Player extends Entity {

    GamePanel gamePanel;
    KeyHandler keyHandler;
    private final SkillManager skillManager;

    private int baseSpeed;
    private int speedModifier;
    protected int speed;

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
    public BufferedImage[] idleUpFrames;
    public BufferedImage[] idleDownFrames;
    public BufferedImage[] idleLeftFrames;
    public BufferedImage[] idleRightFrames;

    // ATTACK ANIMATION ARRAYS (6 frames)
    public BufferedImage[] attackUpFrames;
    public BufferedImage[] attackDownFrames;
    public BufferedImage[] attackLeftFrames;
    public BufferedImage[] attackRightFrames;

    // ANIMATION CONTROL
    int frameIndex = 0;
    int frameCounter = 0;
    int animationSpeed = 8;
    int idleAnimationSpeed = 8;
    int idleUpAnimationSpeed = 24;
    int attackAnimationSpeed = 4; // lower => faster

    boolean moving = false;
    boolean wasMoving = false;
    boolean attacking = false;
    int attackFrameIndex = 0;
    int attackFrameCounter = 0;
    private boolean attackHitApplied = false;
    private long lastDamageTime = 0;
    private static final int DAMAGE_COOLDOWN_MS = 750;
    String lastDirection = "down";

    // track last equipped so we only reload when changed
    public Item lastEquippedWeapon = null;

    public int screenX;
    public int screenY;

    // ===== INVENTORY (3x2 GRID) =====
    public static final int INVENTORY_ROWS = 2;
    public static final int INVENTORY_COLS = 3;

    public Item[][] inventory = new Item[INVENTORY_ROWS][INVENTORY_COLS];

    // ===== ITEM-RELATED FLAGS & SLOTS =====
    public Item weapon;
    public boolean hasMap2Key = false;
    public Item[] hotbar = new Item[3];

    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        setDefaultValues();

        // initialize arrays and load base sprites
        upFrames = new BufferedImage[6];
        downFrames = new BufferedImage[6];
        leftFrames = new BufferedImage[6];
        rightFrames = new BufferedImage[6];

        idleUpFrames = new BufferedImage[12];
        idleDownFrames = new BufferedImage[12];
        idleLeftFrames = new BufferedImage[12];
        idleRightFrames = new BufferedImage[12];

        attackUpFrames = new BufferedImage[6];
        attackDownFrames = new BufferedImage[6];
        attackLeftFrames = new BufferedImage[6];
        attackRightFrames = new BufferedImage[6];

        loadPlayerImages();

        skillManager = new SkillManager(this);
    }

    // INITIALIZATION
    public void setDefaultValues() {

        int centerCol = gamePanel.maxWorldCol / 2;
        int centerRow = gamePanel.maxWorldRow / 2;
        worldX = centerCol * gamePanel.tileSize;
        worldY = centerRow * gamePanel.tileSize;

        gamePanel.cameraX = worldX - (gamePanel.screenWidth / 2);
        gamePanel.cameraY = worldY - (gamePanel.screenHeight / 2);

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

        int hitboxWidth = gamePanel.tileSize;
        int hitboxHeight = gamePanel.tileSize;
        int offsetX = (gamePanel.tileSize * 2 - hitboxWidth) / 2;
        int offsetY = gamePanel.tileSize;

        solidArea.setBounds(offsetX, offsetY, hitboxWidth, hitboxHeight);
        solidAreaDefaultX = offsetX;
        solidAreaDefaultY = offsetY;
    }

    // ========================= SPEED FIX =========================
    private void refreshSpeed() {
        speed = Math.max(1, baseSpeed + speedModifier);
    }

    public void addSpeedModifier(int amount) {
        speedModifier += amount;
        refreshSpeed();
    }

    // ========================= GETTERS =========================
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public String getDirection() {
        return direction;
    }

    // ========================= UPDATE =========================
    public void update() {

        // STOP PLAYER MOVEMENT WHEN INVENTORY IS OPEN
        if (gamePanel.showInventory) {
            moving = false;
            return;
        }

        int dx = 0, dy = 0;
        moving = false;
        boolean moved = false;

        // Input
        if (keyHandler.upPressed) { dy -= 1; direction = "up"; moving = true; }
        if (keyHandler.downPressed) { dy += 1; direction = "down"; moving = true; }
        if (keyHandler.leftPressed) { dx -= 1; direction = "left"; moving = true; }
        if (keyHandler.rightPressed) { dx += 1; direction = "right"; moving = true; }

        // Movement + collision
        int moveX = 0;
        int moveY = 0;

        if (dx != 0 || dy != 0) {

            // diagonal speed normalize
            if (dx != 0 && dy != 0) {
                double diagonalFactor = 1.0 / Math.sqrt(2.0);
                moveX = (int) Math.round(dx * speed * diagonalFactor);
                moveY = (int) Math.round(dy * speed * diagonalFactor);
            } else {
                moveX = dx * speed;
                moveY = dy * speed;
            }

            // Attempt diagonal
            if (moveX != 0 && moveY != 0) {

                int nextX = worldX + moveX;
                int nextY = worldY + moveY;

                if (!gamePanel.collision.willCollide(this, nextX, nextY)) {
                    worldX = nextX;
                    worldY = nextY;
                    moved = true;
                } else {

                    boolean movedX = false;
                    boolean movedY = false;

                    if (!gamePanel.collision.willCollide(this, worldX + moveX, worldY)) {
                        worldX += moveX;
                        moved = true;
                        movedX = true;
                    }

                    if (!gamePanel.collision.willCollide(this, worldX, worldY + moveY)) {
                        worldY += moveY;
                        moved = true;
                        movedY = true;
                    }

                    if (!movedX && !movedY) moving = false;
                }

            } else {
                // axis movement
                if (moveX != 0 && !gamePanel.collision.willCollide(this, worldX + moveX, worldY)) {
                    worldX += moveX;
                    moved = true;
                }

                if (moveY != 0 && !gamePanel.collision.willCollide(this, worldX, worldY + moveY)) {
                    worldY += moveY;
                    moved = true;
                }
            }
        }

        if (!moved) moving = false;

        handleSkillInput();
        handleAttackInput();      // <- must check attack input every update
        checkWeaponChange();      // <- ensure sprites change when weapon changes
        updateAnimation();
        applyAttackDamageIfNeeded();
        handleTeleport();
        skillManager.update();
        regenerateEnergy();
        updateShield();
    }

    // ========================= ANIMATION =========================
    private void updateAnimation() {

        // Handle attack animation (highest priority)
        if (attacking) {
            attackFrameCounter++;
            if (attackFrameCounter > attackAnimationSpeed) {
                attackFrameCounter = 0;
                attackFrameIndex++;
                if (attackFrameIndex >= 6) {
                    attacking = false;
                    attackFrameIndex = 0;
                }
            }
            // while attacking, do not progress movement/idle animations
            return;
        }

        if (moving != wasMoving) {
            frameIndex = 0;
            frameCounter = 0;
        }

        if (!direction.equals(lastDirection)) {
            frameIndex = 0;
            frameCounter = 0;
        }

        if (moving) {
            frameCounter++;
            if (frameCounter > animationSpeed) {
                frameCounter = 0;
                frameIndex = (frameIndex + 1) % 6;
            }
        } else {
            frameCounter++;

            int currentIdleSpeed = direction.equals("up") ? idleUpAnimationSpeed : idleAnimationSpeed;

            if (frameCounter > currentIdleSpeed) {
                frameCounter = 0;

                if (direction.equals("up")) {
                    frameIndex = (frameIndex + 1) % 4;
                } else {
                    frameIndex = (frameIndex + 1) % 12;
                }
            }
        }

        wasMoving = moving;
        lastDirection = direction;
    }

    // ========================= IMAGE LOADING =========================
    private BufferedImage loadImageFromFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            return ImageIO.read(file);

        } catch (IOException e) {
            System.err.println("Failed to load: " + path);
            return null;
        }
    }

    public void loadPlayerImages() {

        String basePath =
                "src" + File.separator +
                "assets" + File.separator +
                "sprites" + File.separator +
                "player" + File.separator;

        // default movement sprites
        for (int i = 0; i < 6; i++) {
            upFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "up" + (i+1) + ".png");
            downFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "down" + (i+1) + ".png");
            leftFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "left" + (i+1) + ".png");
            rightFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "right" + (i+1) + ".png");
        }

        // idle up: 4 frames then repeat last
        for (int i = 0; i < 4; i++) {
            idleUpFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleB" + (i+1) + ".png");
        }
        BufferedImage lastUp = idleUpFrames[3] != null ? idleUpFrames[3] : idleUpFrames[0];
        for (int i = 4; i < 12; i++) idleUpFrames[i] = lastUp;

        for (int i = 0; i < 12; i++) {
            idleDownFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleF" + (i+1) + ".png");
            idleLeftFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleL" + (i+1) + ".png");
            idleRightFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleR" + (i+1) + ".png");
        }

        // Default: clear attack frames (they'll be loaded when weapon equipped)
        for (int i = 0; i < 6; i++) {
            attackUpFrames[i] = null;
            attackDownFrames[i] = null;
            attackLeftFrames[i] = null;
            attackRightFrames[i] = null;
        }

        // If a weapon is already equipped at start, load its sprites
        if (weapon != null) reloadSpritesBasedOnWeapon();
    }

    // Reload sprites based on equipped weapon (handles movement override and attack folders)
    public void reloadSpritesForWeapon() {
        reloadSpritesBasedOnWeapon();
    }

    public void equipWeaponItem(Item item) {
        if (item == null) return;

        if (!(item instanceof Hanger) && !(item instanceof Tsinelas)) {
            return;
        }

        System.out.println("[Player] Equipping weapon: " + item.getClass().getSimpleName());
        weapon = item;
        reloadSpritesForWeapon();
        lastEquippedWeapon = weapon;

        if (gamePanel.ui != null) {
            gamePanel.ui.showMessage(item.name + " equipped!");
        }
    }

    public void resetToDefaultAnimation() {
        boolean wasEquipped = weapon != null;
        weapon = null;
        reloadSpritesForWeapon();
        lastEquippedWeapon = null;

        if (wasEquipped && gamePanel.ui != null) {
            gamePanel.ui.showMessage("Default stance equipped");
        }
    }

    private void reloadSpritesBasedOnWeapon() {
        String basePath =
                "src" + File.separator +
                "assets" + File.separator +
                "sprites" + File.separator +
                "player" + File.separator;

        // Default movement folder (no weapon)
        String movementFolder = "movement";
        String attackFolder = null;

        if (weapon != null) {
            String weaponName = weapon.getClass().getSimpleName();
            System.out.println("[Player] Loading sprites for weapon: " + weaponName);

            if (weaponName.equals("Hanger")) {
                movementFolder = "movementHanger";
                attackFolder = "attackHanger";
                System.out.println("[Player] Using movement folder: " + movementFolder);
            }
            else if (weaponName.equals("Tsinelas")) {
                movementFolder = "movementTsinelas";
                attackFolder = "attackTsinelas";
                System.out.println("[Player] Using movement folder: " + movementFolder);
            }
            else {
                movementFolder = "movement";
                attackFolder = weaponName.toLowerCase() + "Attack";
                System.out.println("[Player] Unknown weapon, using default movement folder");
            }
        } else {
            System.out.println("[Player] No weapon equipped, using default movement folder");
        }

        // Load movement sprites (with fallback)
        for (int i = 0; i < 6; i++) {
            upFrames[i] = loadImageFromFile(basePath + movementFolder + File.separator + "up" + (i+1) + ".png");
            downFrames[i] = loadImageFromFile(basePath + movementFolder + File.separator + "down" + (i+1) + ".png");
            leftFrames[i] = loadImageFromFile(basePath + movementFolder + File.separator + "left" + (i+1) + ".png");
            rightFrames[i] = loadImageFromFile(basePath + movementFolder + File.separator + "right" + (i+1) + ".png");

            // Fallback to default movement if missing
            if (upFrames[i] == null)
                upFrames[i] = loadImageFromFile(basePath + "movement/up" + (i+1) + ".png");
            if (downFrames[i] == null)
                downFrames[i] = loadImageFromFile(basePath + "movement/down" + (i+1) + ".png");
            if (leftFrames[i] == null)
                leftFrames[i] = loadImageFromFile(basePath + "movement/left" + (i+1) + ".png");
            if (rightFrames[i] == null)
                rightFrames[i] = loadImageFromFile(basePath + "movement/right" + (i+1) + ".png");
        }

        // Load attacks
        if (attackFolder != null) {
            if (weapon != null && weapon.getClass().getSimpleName().equals("Hanger")) {
                for (int i = 0; i < 6; i++) {
                    attackUpFrames[i] = loadImageFromFile(basePath + attackFolder + "/up" + (i+1) + ".png");
                    attackDownFrames[i] = loadImageFromFile(basePath + attackFolder + "/down" + (i+1) + ".png");
                    attackLeftFrames[i] = loadImageFromFile(basePath + attackFolder + "/left" + (i+1) + ".png");
                    attackRightFrames[i] = loadImageFromFile(basePath + attackFolder + "/right" + (i+1) + ".png");
                }
            }
            else if (weapon != null && weapon.getClass().getSimpleName().equals("Tsinelas")) {
                for (int i = 0; i < 6; i++) {
                    attackDownFrames[i] = loadImageFromFile(basePath + attackFolder + "/Sword_Walk_Attack_front" + (i+1) + ".png");
                    attackUpFrames[i] = attackDownFrames[i];
                    attackLeftFrames[i] = attackDownFrames[i];
                    attackRightFrames[i] = attackDownFrames[i];
                }
            }
        }
        else {
            // clear if no attack sprites exist
            for (int i = 0; i < 6; i++) {
                attackUpFrames[i] = null;
                attackDownFrames[i] = null;
                attackLeftFrames[i] = null;
                attackRightFrames[i] = null;
            }
        }
    }

    // ========================= TELEPORT LOGIC =========================
    private void handleTeleport() {
        int playerCol = (worldX + solidArea.x + solidArea.width / 2) / gamePanel.tileSize;
        int playerRow = (worldY + solidArea.y + solidArea.height / 2) / gamePanel.tileSize;

        int tileNum = gamePanel.tileManager.getTileNum(playerCol, playerRow);

        // TILE ID
        int map1TP = gamePanel.tileManager.tilesetStart[0] + 5;
        int map2TP = gamePanel.tileManager.tilesetStart[1] + 1;
        int map3TP = gamePanel.tileManager.tilesetStart[1] + 4;

        if (gamePanel.currentMap == 0 && tileNum == map1TP) {
            gamePanel.switchToMap(1, 16, 1, "down");
        }

        if (gamePanel.currentMap == 1 && tileNum == map2TP) {
            gamePanel.switchToMap(0, 16, 18, "up");
        }

        if (gamePanel.currentMap == 1 && tileNum == map3TP) {
            gamePanel.switchToMap(2, 2, 11, "down");
        }
    }

    // ========================= SKILLS =========================
    private void handleSkillInput() {
        for (int slot = 0; slot < KeyHandler.SKILL_SLOT_COUNT; slot++) {
            if (keyHandler.consumeSkillTap(slot)) {
                skillManager.activateSlot(slot);
            }
        }
    }

    // ========================= ATTACK =========================
    private void handleAttackInput() {
        // replace consumeAttackTap with whatever KeyHandler uses for attack (assumes method exists)
        if (keyHandler.consumeAttackTap() && !attacking && weapon != null) {
            if (weapon instanceof Tsinelas) {
                fireTsinelasProjectile();
                return;
            }

            attacking = true;
            attackFrameIndex = 0;
            attackFrameCounter = 0;
            moving = false;
            attackHitApplied = false;
        }
    }

    private void applyAttackDamageIfNeeded() {
        if (!attacking) return;
        if (!(weapon instanceof Hanger)) return; // focus on Hanger first
        if (attackHitApplied) return;

        // Trigger damage when animation reaches swing frame
        if (attackFrameIndex >= 2) {
            Rectangle hitbox = buildHangerHitbox();
            int damage = getCurrentWeaponDamage();
            if (hitbox != null && damage > 0) {
                gamePanel.applyPlayerAttack(hitbox, damage);
                attackHitApplied = true;
            }
        }
    }

    private Rectangle buildHangerHitbox() {
        int range = gamePanel.tileSize;
        int baseX = worldX + solidArea.x;
        int baseY = worldY + solidArea.y;
        int width = solidArea.width;
        int height = solidArea.height;

        switch (direction) {
            case "up":
                return new Rectangle(baseX, baseY - range, width, range);
            case "down":
                return new Rectangle(baseX, baseY + height, width, range);
            case "left":
                return new Rectangle(baseX - range, baseY, range, height);
            case "right":
                return new Rectangle(baseX + width, baseY, range, height);
            default:
                return null;
        }
    }

    private int getCurrentWeaponDamage() {
        if (weapon instanceof Hanger) {
            return 35;
        }
        if (weapon instanceof Tsinelas) {
            return 20;
        }
        return 10;
    }

    private void fireTsinelasProjectile() {
        int centerX = worldX + solidArea.x + solidArea.width / 2;
        int centerY = worldY + solidArea.y + solidArea.height / 2;
        int startX = centerX - gamePanel.tileSize / 2;
        int startY = centerY - gamePanel.tileSize / 2;
        gamePanel.spawnTsinelasProjectile(startX, startY, direction);
    }

    // ========================= INVENTORY / HOTBAR ========================= 
    public boolean addToInventory(Item item) {
        for (int r = 0; r < INVENTORY_ROWS; r++) {
            for (int c = 0; c < INVENTORY_COLS; c++) {

                if (inventory[r][c] == null) {
                    inventory[r][c] = item;

                    // if placed in row 0 → sync action bar
                    if (r == 0) syncHotbarFromInventory();

                    System.out.println("[Inventory] Added " + item.name + " at ["+r+","+c+"]");
                    return true;
                }
            }
        }

        System.out.println("[Inventory] FULL — cannot add " + item.name);
        return false;
    }

    public void syncHotbarFromInventory() {
        for (int c = 0; c < 3; c++) {
            hotbar[c] = inventory[0][c];
        }
        System.out.println("[Hotbar] Sync complete.");
    }

    public void useHotbarItem(int slot) {

        System.out.println("[Hotbar] ENTER pressed on slot " + slot);

        if (slot < 0 || slot >= 3) return;

        Item item = hotbar[slot];
        if (item == null) {
            System.out.println("[Hotbar] Slot empty. Resetting to default animation.");
            resetToDefaultAnimation();
            return;
        }

        System.out.println("[Hotbar] Using item: " + item.name);

        // If it's a weapon -> equip it (do not consume)
        if (item instanceof Hanger || item instanceof Tsinelas) {
            equipWeaponItem(item);
            return;
        }

        // Otherwise treat as a consumable/default use
        item.use(this);

        // Only remove if it's a consumable (potions, buffs)
        if (item.isConsumable) {
            hotbar[slot] = null;
            inventory[0][slot] = null;
        }

        syncHotbarFromInventory();

        // UI feedback
        if (gamePanel != null && gamePanel.ui != null) {
            gamePanel.ui.showMessage(item.name + " used!");
        }
    }

    // Detect weapon change (additional safety)
    private void checkWeaponChange() {
        if (weapon != lastEquippedWeapon) {
            reloadSpritesBasedOnWeapon();
            lastEquippedWeapon = weapon;
        }
    }

    // ===== SHIELD / DAMAGE IMMUNITY BUFF =====
    public boolean shieldActive = false;
    private long shieldEndTime = 0;

    public void enableShield(long durationMs) {
        shieldActive = true;
        shieldEndTime = System.currentTimeMillis() + durationMs;
        System.out.println("[BUFF] Shield activated for " + durationMs + "ms!");
    }

    private void updateShield() {
        if (shieldActive && System.currentTimeMillis() > shieldEndTime) {
            shieldActive = false;
            System.out.println("[BUFF] Shield expired.");
        }
    }

    // ========================= ENERGY =========================
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

    // ========================= STATS =========================
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }

    public void heal(int amount) {
        if (amount > 0) health = Math.min(maxHealth, health + amount);
    }

    public void activateShield(int durationTicks) {
        // Placeholder for shield logic
        System.out.println("Player shield activated for " + durationTicks + " ticks!");
    }

    public void damage(int amount) {

        if (shieldActive) {
            System.out.println("[SHIELD] Damage blocked!");
            return;
        }

        if (amount <= 0) return;

        long now = System.currentTimeMillis();
        if (now - lastDamageTime < DAMAGE_COOLDOWN_MS) return;

        lastDamageTime = now;
        health = Math.max(0, health - amount);
        System.out.println("Player hit! Health: " + health);

        if (health <= 0) {
            System.out.println("[Player] Game Over");
            gamePanel.gameState = GamePanel.STATE_GAME_OVER;
        }
    }

    public void takeDamage(int amount) { damage(amount); }

    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }

    public boolean consumeEnergy(int amount) {
        if (amount <= 0) return true;
        if (energy < amount) return false;
        energy -= amount;
        return true;
    }

    public void restoreEnergy(int amount) {
        if (amount > 0) energy = Math.min(maxEnergy, energy + amount);
    }

    public boolean isChanneling() { return channeling; }
    public void setChanneling(boolean ch) { channeling = ch; }

    public SkillManager getSkillManager() { return skillManager; }

    // ========================= DRAW PLAYER SPRITE =========================
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        // Priority: Attack > Moving > Idle
        if (attacking && weapon != null && attackFrameIndex < 6 &&
                attackUpFrames != null && attackDownFrames != null &&
                attackLeftFrames != null && attackRightFrames != null) {

            switch (direction) {
                case "up":    image = (attackFrameIndex < attackUpFrames.length && attackUpFrames[attackFrameIndex] != null) ? attackUpFrames[attackFrameIndex] : null; break;
                case "down":  image = (attackFrameIndex < attackDownFrames.length && attackDownFrames[attackFrameIndex] != null) ? attackDownFrames[attackFrameIndex] : null; break;
                case "left":  image = (attackFrameIndex < attackLeftFrames.length && attackLeftFrames[attackFrameIndex] != null) ? attackLeftFrames[attackFrameIndex] : null; break;
                case "right": image = (attackFrameIndex < attackRightFrames.length && attackRightFrames[attackFrameIndex] != null) ? attackRightFrames[attackFrameIndex] : null; break;
            }

        } else if (moving) {
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

            int screenW = gamePanel.getWidth();
            int screenH = gamePanel.getHeight();

            if (screenW <= 0) screenW = gamePanel.screenWidth;
            if (screenH <= 0) screenH = gamePanel.screenHeight;

            int drawX = (screenW / 2) - (spriteWidth / 2);
            int drawY = (screenH / 2) - (spriteHeight / 2);

            this.screenX = drawX;
            this.screenY = drawY;

            g2.drawImage(image, drawX, drawY, spriteWidth, spriteHeight, null);
        }
    }
}
