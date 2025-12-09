package src.entity.mobs;

import src.core.GamePanel;
import src.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BossEnemy extends Enemy {

    private BufferedImage[] walkRight = new BufferedImage[12];
    private BufferedImage[] walkLeft = new BufferedImage[12];
    private BufferedImage[] attackRight = new BufferedImage[5];
    private BufferedImage[] attackLeft = new BufferedImage[5];

    private int animIndex = 0;
    private int animCounter = 0;

    private boolean isAttacking = false;
    private int attackAnimIndex = 0;
    private int attackAnimCounter = 0;
    private int attackCooldown = 0;

    private static final int ATTACK_COOLDOWN_TIME = 60;
    private static final int ATTACK_ANIMATION_SPEED = 8;

    private static final int BASE_WIDTH = 64;
    private static final int BASE_HEIGHT = 64;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player) {
        this(gp, worldX, worldY, player, 4, 5);
    }

    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player, int widthTiles, int heightTiles) {
        super(gp, worldX, worldY, player);

        this.width = widthTiles * gp.tileSize;
        this.height = heightTiles * gp.tileSize;

        this.scaleX = (float) width / BASE_WIDTH;
        this.scaleY = (float) height / BASE_HEIGHT;

        float avgScale = (scaleX + scaleY) / 2.0f;
        this.attackRange = (int) (42 * avgScale);

        this.maxHealth = 300;
        this.health = maxHealth;

        this.speed = 2;
        this.visionRange = 9999;

        this.hitbox = new Rectangle(0, 0, width, height);

        loadWalkSprites();
        loadAttackSprites();
    }

    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player, int tilesWide) {
        this(gp, worldX, worldY, player, tilesWide, tilesWide);
    }

    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player, float scale) {
        this(gp, worldX, worldY, player,
                (int) (BASE_WIDTH * scale / gp.tileSize),
                (int) (BASE_HEIGHT * scale / gp.tileSize));
    }

    public void setSizeInTiles(int widthTiles, int heightTiles) {
        this.width = widthTiles * gp.tileSize;
        this.height = heightTiles * gp.tileSize;
        this.scaleX = (float) width / BASE_WIDTH;
        this.scaleY = (float) height / BASE_HEIGHT;

        float avgScale = (scaleX + scaleY) / 2.0f;
        this.attackRange = (int) (42 * avgScale);

        this.hitbox = new Rectangle(0, 0, width, height);
    }

    public void setScale(float newScale) {
        setSizeInTiles(
                (int) (BASE_WIDTH * newScale / gp.tileSize),
                (int) (BASE_HEIGHT * newScale / gp.tileSize));
    }

    public float getScale() {
        return (scaleX + scaleY) / 2.0f;
    }

    // ===============================
    // WALK SPRITES
    // ===============================
    private void loadWalkSprites() {

        String basePath = "assets" + File.separator +
                "sprites" + File.separator +
                "mob" + File.separator +
                "boss" + File.separator;

        System.out.println("\n======== BossEnemy FRAME LOADING ========");
        System.out.println("Base path: " + new File(basePath).getAbsolutePath());

        for (int i = 0; i < 12; i++) {
            String rightFileName = basePath + "WalkR" + (i + 1) + ".png";
            String leftFileName = basePath + "WalkL" + (i + 1) + ".png";

            try {
                File rightFile = new File(rightFileName);
                File leftFile = new File(leftFileName);

                walkRight[i] = rightFile.exists() ? ImageIO.read(rightFile) : null;
                walkLeft[i] = leftFile.exists() ? ImageIO.read(leftFile) : null;

                if (!rightFile.exists()) System.out.println("Missing: " + rightFileName);
                if (!leftFile.exists()) System.out.println("Missing: " + leftFileName);

                System.out.println("Walk Frame " + (i + 1) +
                        " | R=" + (walkRight[i] != null) +
                        " | L=" + (walkLeft[i] != null));

            } catch (IOException e) {
                System.out.println("Failed to load walk frame " + (i + 1) + ": " + e.getMessage());
                walkRight[i] = null;
                walkLeft[i] = null;
            }
        }
        System.out.println("=========================================\n");
    }

    // ===============================
    // ATTACK SPRITES
    // ===============================
    private void loadAttackSprites() {

        String basePath = "assets" + File.separator +
                "sprites" + File.separator +
                "mob" + File.separator +
                "boss" + File.separator;

        System.out.println("\n======== BossEnemy ATTACK FRAME LOADING ========");
        System.out.println("Base path: " + new File(basePath).getAbsolutePath());

        for (int i = 0; i < 5; i++) {
            String rightFileName = basePath + "AttackR" + (i + 1) + ".png";
            String leftFileName = basePath + "AttackL" + (i + 1) + ".png";

            try {
                File rightFile = new File(rightFileName);
                File leftFile = new File(leftFileName);

                attackRight[i] = rightFile.exists() ? ImageIO.read(rightFile) : null;
                attackLeft[i] = leftFile.exists() ? ImageIO.read(leftFile) : null;

                if (!rightFile.exists()) System.out.println("Missing: " + rightFileName);
                if (!leftFile.exists()) System.out.println("Missing: " + leftFileName);

                System.out.println("Attack Frame " + (i + 1) +
                        " | R=" + (attackRight[i] != null) +
                        " | L=" + (attackLeft[i] != null));

            } catch (IOException e) {
                System.out.println("Failed to load attack frame " + (i + 1) + ": " + e.getMessage());
                attackRight[i] = null;
                attackLeft[i] = null;
            }
        }
        System.out.println("=========================================\n");
    }

    @Override
    public void attack() {
        if (dead || attackCooldown > 0 || isAttacking) return;

        if (!isInAttackRange()) return;

        isAttacking = true;
        attackAnimIndex = 0;
        attackAnimCounter = 0;

        player.damage(10);
        System.out.println("[BOSS] Smashes the player for 10 damage!");

        attackCooldown = ATTACK_COOLDOWN_TIME;
    }

    @Override
    public void moveTowards(Player p) {
        if (dead || isAttacking) return;

        int dx = p.worldX - worldX;
        int dy = p.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) return;

        double nx = dx / dist;
        double ny = dy / dist;

        worldX += nx * speed * 1.2;
        worldY += ny * speed * 1.2;

        animCounter++;
        if (animCounter >= 5) {
            animCounter = 0;
            animIndex++;
            if (animIndex >= 12) animIndex = 0;
        }
    }

    public void updateAttackAnimation() {
        if (attackCooldown > 0) attackCooldown--;

        if (isAttacking) {
            attackAnimCounter++;
            if (attackAnimCounter >= ATTACK_ANIMATION_SPEED) {
                attackAnimCounter = 0;
                attackAnimIndex++;

                if (attackAnimIndex >= 5) {
                    isAttacking = false;
                    attackAnimIndex = 0;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        BufferedImage frame;
        boolean facingLeft = player.worldX < worldX;

        if (isAttacking) {
            frame = facingLeft ? attackLeft[attackAnimIndex] : attackRight[attackAnimIndex];
        } else {
            frame = facingLeft ? walkLeft[animIndex] : walkRight[animIndex];
        }

        if (frame != null) {
            g2.drawImage(frame, screenX, screenY, width, height, null);
        } else {
            g2.setColor(isAttacking ? Color.RED : Color.MAGENTA);
            g2.fillRect(screenX, screenY, width, height);
        }

        drawHealthBar(g2, screenX, screenY);
    }

    @Override
    public void update() {
        if (!dead) {
            updateAttackAnimation();
            super.update();
        }
    }
}
