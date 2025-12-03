package src.entity.mobs;

import src.ai.EnemyAI;
import src.core.GamePanel;
import src.entity.Player;

import java.awt.*;
import java.util.List;

public abstract class Enemy {

    protected GamePanel gp;
    protected Player player;
    protected EnemyAI ai;

    // ===== WORLD POSITION =====
    public int worldX, worldY;

    // ===== ENEMY SIZE =====
    protected int width = 32;
    protected int height = 32;

    // Hitbox (same size)
    public Rectangle hitbox = new Rectangle(0, 0, width, height);

    // ===== COMBAT STATS =====
    protected int speed = 1;
    protected int attackRange = 32;
    protected int visionRange = 200;

    protected int maxHealth = 20;
    protected int health = maxHealth;
    protected boolean dead = false;

    // ===== PATROL SYSTEM =====
    protected boolean hasPatrol = false;
    protected int patrolIndex = 0;
    protected int[][] patrolPoints;

    // ==========================
    // CONSTRUCTOR
    // ==========================
    public Enemy(GamePanel gp, int worldX, int worldY, Player player) {
        this.gp = gp;
        this.player = player;

        this.worldX = worldX;
        this.worldY = worldY;

        ai = new EnemyAI(this);
    }

    // ==========================
    // UPDATE LOOP
    // ==========================
    public void update() {
        if (!dead) {
            ai.update();
        }
    }

    // ==========================
    // BEHAVIOR TREE SUPPORT
    // ==========================
    public Player getPlayer() {
        return player;
    }

    public boolean canSeePlayer() {
        int dx = player.worldX - worldX;
        int dy = player.worldY - worldY;

        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < visionRange;
    }

    public boolean isInAttackRange() {
        int dx = player.worldX - worldX;
        int dy = player.worldY - worldY;

        return Math.sqrt(dx * dx + dy * dy) < attackRange;
    }

    public void moveTowards(Player p) {
        if (dead) return;

        int dx = p.worldX - worldX;
        int dy = p.worldY - worldY;

        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        worldX += (dx / dist) * speed;
        worldY += (dy / dist) * speed;
    }

    public void attack() {
        if (dead) return;

        System.out.println("[Enemy] Hits player for 3 damage!");
        player.damage(3);
    }

    // ==========================
    // PATROL BEHAVIOR
    // ==========================
    public boolean hasPatrolPath() {
        return hasPatrol && patrolPoints != null && patrolPoints.length > 0;
    }

    public void followPatrolPath() {
        if (!hasPatrolPath() || dead) return;

        int targetX = patrolPoints[patrolIndex][0];
        int targetY = patrolPoints[patrolIndex][1];

        int dx = targetX - worldX;
        int dy = targetY - worldY;

        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        worldX += (dx / dist) * speed;
        worldY += (dy / dist) * speed;

        if (dist < 4) {
            patrolIndex = (patrolIndex + 1) % patrolPoints.length;
        }
    }

    // ==========================
    // HEALTH SYSTEM
    // ==========================
    public void damage(int amount) {
        if (amount <= 0 || dead) return;

        health -= amount;

        if (health <= 0) {
            dead = true;
            onDeath();
        }
    }

    public boolean isDead() {
        return dead;
    }

    protected void onDeath() {
        System.out.println("[Enemy] Enemy died.");
    }

    // ==========================
    // ENEMY → ENEMY COLLISION (FIXED)
    // ==========================
    public void avoidOverlap(List<Enemy> enemies) {
        for (Enemy other : enemies) {
            if (other == this) continue;
            if (other.dead) continue;

            int dx = this.worldX - other.worldX;
            int dy = this.worldY - other.worldY;

            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < this.width && dist > 0.001) {

                double push = (this.width - dist) * 0.3;

                this.worldX += (dx / dist) * push;
                this.worldY += (dy / dist) * push;
            }
        }
    }

    // ==========================
    // HEALTH BAR DRAW
    // ==========================
    protected void drawHealthBar(Graphics2D g2, int screenX, int screenY) {

        int barWidth = width;
        int barHeight = 6;

        int hpWidth = (int)((double)health / maxHealth * barWidth);

        g2.setColor(Color.BLACK);
        g2.fillRect(screenX, screenY - 10, barWidth, barHeight);

        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY - 10, hpWidth, barHeight);
    }

    // ==========================
    // HELPERS
    // ==========================
    public int getCenterX() { return worldX + width / 2; }
    public int getCenterY() { return worldY + height / 2; }

    public void setSpeed(int s) { speed = s; }
    public void setVisionRange(int r) { visionRange = r; }
    public void setAttackRange(int r) { attackRange = r; }

    public void setPatrolPoints(int[][] pts) {
        hasPatrol = true;
        patrolPoints = pts;
    }

    // Must be implemented by subclasses
    public abstract void draw(Graphics2D g2);
}
