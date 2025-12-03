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
    
    // Attack state
    private boolean isAttacking = false;
    private int attackAnimIndex = 0;
    private int attackAnimCounter = 0;
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN_TIME = 60; // frames between attacks
    private static final int ATTACK_ANIMATION_SPEED = 8; // frames per attack frame
    
    // Scaling customization
    private static final int BASE_WIDTH = 64;
    private static final int BASE_HEIGHT = 64;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    // Constructor with default size (4 tiles wide x 5 tiles tall)
    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player) {
        this(gp, worldX, worldY, player, 4, 5);
    }

    // Constructor with custom tile dimensions (widthTiles x heightTiles)
    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player, int widthTiles, int heightTiles) {
        super(gp, worldX, worldY, player);

        // Set dimensions based on tile size
        this.width = widthTiles * gp.tileSize;
        this.height = heightTiles * gp.tileSize;
        
        // Calculate scale factors for reference
        this.scaleX = (float)width / BASE_WIDTH;
        this.scaleY = (float)height / BASE_HEIGHT;
        
        // Scale attack range based on average scale
        float avgScale = (scaleX + scaleY) / 2.0f;
        this.attackRange = (int)(42 * avgScale);

        this.maxHealth = 300;
        this.health = maxHealth;

        this.speed = 2;
        this.visionRange = 9999;

        this.hitbox = new Rectangle(0, 0, width, height);

        loadWalkSprites();
        loadAttackSprites();
    }

    // Constructor with scale based on tileSize (e.g., 2 = 2 tiles wide/tall - square)
    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player, int tilesWide) {
        this(gp, worldX, worldY, player, tilesWide, tilesWide);
    }

    // Constructor with custom scale (float multiplier - square)
    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player, float scale) {
        this(gp, worldX, worldY, player, 
             (int)(BASE_WIDTH * scale / gp.tileSize), 
             (int)(BASE_HEIGHT * scale / gp.tileSize));
    }

    // Method to set size in tiles after construction
    public void setSizeInTiles(int widthTiles, int heightTiles) {
        this.width = widthTiles * gp.tileSize;
        this.height = heightTiles * gp.tileSize;
        this.scaleX = (float)width / BASE_WIDTH;
        this.scaleY = (float)height / BASE_HEIGHT;
        float avgScale = (scaleX + scaleY) / 2.0f;
        this.attackRange = (int)(42 * avgScale);
        this.hitbox = new Rectangle(0, 0, width, height);
    }

    // Method to set scale after construction (square scaling)
    public void setScale(float newScale) {
        setSizeInTiles(
            (int)(BASE_WIDTH * newScale / gp.tileSize),
            (int)(BASE_HEIGHT * newScale / gp.tileSize)
        );
    }

    // Get current scale (average of X and Y)
    public float getScale() {
        return (scaleX + scaleY) / 2.0f;
    }

    private void loadWalkSprites() {
        String basePath = "src" + File.separator + 
                         "assets" + File.separator + 
                         "sprites" + File.separator + 
                         "mob" + File.separator + 
                         "boss" + File.separator;

        System.out.println("\n======== BossEnemy FRAME LOADING ========");
        System.out.println("Base path: " + basePath);

        for (int i = 0; i < 12; i++) {
            String rightFileName = basePath + "WalkR" + (i + 1) + ".png";
            String leftFileName = basePath + "WalkL" + (i + 1) + ".png";

            try {
                File rightFile = new File(rightFileName);
                File leftFile = new File(leftFileName);

                if (rightFile.exists()) {
                    walkRight[i] = ImageIO.read(rightFile);
                } else {
                    System.out.println("File not found: " + rightFileName);
                    walkRight[i] = null;
                }

                if (leftFile.exists()) {
                    walkLeft[i] = ImageIO.read(leftFile);
                } else {
                    System.out.println("File not found: " + leftFileName);
                    walkLeft[i] = null;
                }

                System.out.println("Frame " + (i + 1) + 
                        " | R=" + (walkRight[i] != null) + 
                        " | L=" + (walkLeft[i] != null));
            } catch (IOException e) {
                System.out.println("Failed to load frame " + (i + 1) + ": " + e.getMessage());
                walkRight[i] = null;
                walkLeft[i] = null;
            }
        }
        System.out.println("=========================================\n");
    }

    private void loadAttackSprites() {
        String basePath = "src" + File.separator + 
                         "assets" + File.separator + 
                         "sprites" + File.separator + 
                         "mob" + File.separator + 
                         "boss" + File.separator;

        System.out.println("\n======== BossEnemy ATTACK FRAME LOADING ========");
        System.out.println("Base path: " + basePath);

        for (int i = 0; i < 5; i++) {
            String rightFileName = basePath + "AttackR" + (i + 1) + ".png";
            String leftFileName = basePath + "AttackL" + (i + 1) + ".png";

            try {
                File rightFile = new File(rightFileName);
                File leftFile = new File(leftFileName);

                if (rightFile.exists()) {
                    attackRight[i] = ImageIO.read(rightFile);
                } else {
                    System.out.println("File not found: " + rightFileName);
                    attackRight[i] = null;
                }

                if (leftFile.exists()) {
                    attackLeft[i] = ImageIO.read(leftFile);
                } else {
                    System.out.println("File not found: " + leftFileName);
                    attackLeft[i] = null;
                }

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
        // Only attack if not on cooldown and not already attacking
        if (dead || attackCooldown > 0 || isAttacking) {
            return;
        }

        // Check if in attack range
        if (!isInAttackRange()) {
            return;
        }

        // Start attack animation
        isAttacking = true;
        attackAnimIndex = 0;
        attackAnimCounter = 0;
        
        // Damage player on attack start (middle of animation)
        player.damage(10);
        System.out.println("[BOSS] Smashes the player for 10 damage!");
        
        // Set cooldown
        attackCooldown = ATTACK_COOLDOWN_TIME;
    }

    @Override
    public void moveTowards(Player p) {
        if (dead || isAttacking) return; // Don't move while attacking

        int dx = p.worldX - worldX;
        int dy = p.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) return;

        double nx = dx / dist;
        double ny = dy / dist;

        worldX += nx * speed * 1.2;
        worldY += ny * speed * 1.2;

        // Walk animation speed
        animCounter++;
        if (animCounter >= 5) {
            animCounter = 0;
            animIndex++;
            if (animIndex >= 12) animIndex = 0;
        }
    }

    // Update attack animation and cooldown
    public void updateAttackAnimation() {
        // Update cooldown
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // Update attack animation
        if (isAttacking) {
            attackAnimCounter++;
            if (attackAnimCounter >= ATTACK_ANIMATION_SPEED) {
                attackAnimCounter = 0;
                attackAnimIndex++;
                
                // Attack animation complete
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

        // Choose animation based on state
        if (isAttacking) {
            // Attack animation
            if (facingLeft) {
                frame = attackLeft[attackAnimIndex];
            } else {
                frame = attackRight[attackAnimIndex];
            }
        } else {
            // Walk animation
            if (facingLeft) {
                frame = walkLeft[animIndex];
            } else {
                frame = walkRight[animIndex];
            }
        }

        // Only draw if sprite loaded successfully
        if (frame != null) {
            // Apply scaling to sprite rendering
            g2.drawImage(frame, screenX, screenY, width, height, null);
        } else {
            // Fallback: draw a colored rectangle if sprite failed to load
            g2.setColor(isAttacking ? Color.RED : Color.MAGENTA);
            g2.fillRect(screenX, screenY, width, height);
        }
        drawHealthBar(g2, screenX, screenY);
    }
    
    // Override update to handle attack animation
    @Override
    public void update() {
        if (!dead) {
            updateAttackAnimation();
            super.update(); // Call parent update for AI
        }
    }
}
