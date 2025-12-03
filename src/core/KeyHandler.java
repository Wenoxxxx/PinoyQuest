package src.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private boolean rightShiftHeld = false;

    public static final int SKILL_SLOT_COUNT = 3;
    private static final String[] SKILL_KEY_LABELS = {"J", "K", "L"};
    private static final int[] SKILL_KEY_CODES = {
            KeyEvent.VK_J,
            KeyEvent.VK_K,
            KeyEvent.VK_L
    };

    public boolean upPressed, downPressed, leftPressed, rightPressed;

    // Skill Buttons
    private final boolean[] skillPressed = new boolean[SKILL_SLOT_COUNT];
    private final boolean[] skillTapped = new boolean[SKILL_SLOT_COUNT];

    // ===== ATTACK INPUT =====
    private boolean attackPressed = false;
    private boolean attackTapped = false;

    private final GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        // ==================================
        // SHIFT HOLD ONLY WORKS IN INVENTORY
        // ==================================
        if (gp.gameState == GamePanel.STATE_INVENTORY &&
                code == KeyEvent.VK_SHIFT &&
                !rightShiftHeld) {

            rightShiftHeld = true;
            gp.ui.getInventoryUI().beginHoldSelectedItem();
            return;
        }

        if (gp.gameState == GamePanel.STATE_MENU) {
            handleMenuInput(code);
        }
        else if (gp.gameState == GamePanel.STATE_PLAY) {
            handlePlayInput(code);
        }
        else if (gp.gameState == GamePanel.STATE_SETTINGS) {
            handleSettingsInput(code);
        }
        else if (gp.gameState == GamePanel.STATE_INVENTORY) {
            handleInventoryInput(code);
        }
    }

    // ===================== MENU =====================
    private void handleMenuInput(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.menuSelectedIndex--;
            if (gp.menuSelectedIndex < 0)
                gp.menuSelectedIndex = gp.menuOptions.length - 1;
        }
        else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.menuSelectedIndex++;
            if (gp.menuSelectedIndex >= gp.menuOptions.length)
                gp.menuSelectedIndex = 0;
        }
        else if (code == KeyEvent.VK_ENTER) {
            int i = gp.menuSelectedIndex;

            if (i == 0) gp.startNewGame();
            else if (i == 1 && gp.canResume) gp.resumeGame();
            else if (i == 2) gp.openSettings();
            else if (i == 3) System.exit(0);
        }
    }

    // ===================== PLAY =====================
    private void handlePlayInput(int code) {

        // Movement
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;

        // Skills (J K L)
        int slot = getSkillSlotFromCode(code);
        if (slot >= 0 && slot < SKILL_SLOT_COUNT) {
            if (!skillPressed[slot]) skillTapped[slot] = true;
            skillPressed[slot] = true;
        }

        // Action bar hotkeys
        if (code == KeyEvent.VK_1) gp.actionBarUI.activeSlot = 0;
        if (code == KeyEvent.VK_2) gp.actionBarUI.activeSlot = 1;
        if (code == KeyEvent.VK_3) gp.actionBarUI.activeSlot = 2;

        // Open inventory
        if (code == KeyEvent.VK_I) {
            gp.gameState = GamePanel.STATE_INVENTORY;
            upPressed = downPressed = leftPressed = rightPressed = false;
            return;
        }

        // =====================
        // ENTER = USE HOTBAR ITEM (Equip / Consumable)
        // =====================
        if (code == KeyEvent.VK_ENTER) {
            if (gp.player != null) {
                gp.player.useHotbarItem(gp.actionBarUI.activeSlot);
            }
            return;
        }

        // =====================
        // SPACE = ATTACK (if weapon)
        // =====================
        if (code == KeyEvent.VK_SPACE) {
            if (gp.player != null && gp.player.weapon != null) {

                if (!attackPressed) attackTapped = true;
                attackPressed = true;

            } else {
                // If no weapon → treat SPACE as item use
                gp.player.useHotbarItem(gp.actionBarUI.activeSlot);
            }
            return;
        }

        // Escape → Menu
        if (code == KeyEvent.VK_ESCAPE) {
            gp.canResume = true;
            gp.gameState = GamePanel.STATE_MENU;
        }
    }

    // ===================== INVENTORY =====================
    private void handleInventoryInput(int code) {

        if (code == KeyEvent.VK_I || code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GamePanel.STATE_PLAY;
            return;
        }

        var inv = gp.ui.getInventoryUI();
        if (inv == null) return;

        // Movement inside inventory
        if (code == KeyEvent.VK_W) {
            inv.moveCursorUp();
            return;
        }
        if (code == KeyEvent.VK_S) {
            inv.moveCursorDown();
            return;
        }
        if (code == KeyEvent.VK_A) {
            inv.moveCursorLeft();
            return;
        }
        if (code == KeyEvent.VK_D) {
            inv.moveCursorRight();
            return;
        }

        // ENTER or SPACE = use item
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            inv.useSelectedItem();
        }
    }

    // ===================== SETTINGS =====================
    private void handleSettingsInput(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            gp.gameState = GamePanel.STATE_MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (gp.gameState == GamePanel.STATE_PLAY) {

            if (code == KeyEvent.VK_W) upPressed = false;
            if (code == KeyEvent.VK_S) downPressed = false;
            if (code == KeyEvent.VK_A) leftPressed = false;
            if (code == KeyEvent.VK_D) rightPressed = false;

            int slot = getSkillSlotFromCode(code);
            if (slot != -1) skillPressed[slot] = false;

            // Attack released
            if (code == KeyEvent.VK_SPACE) attackPressed = false;
        }

        // Shift release → end item swap
        if (gp.gameState == GamePanel.STATE_INVENTORY &&
                code == KeyEvent.VK_SHIFT &&
                rightShiftHeld) {

            rightShiftHeld = false;
            gp.ui.getInventoryUI().finishHoldSwap();
        }
    }

    // ===================== SKILLS =====================
    public boolean consumeSkillTap(int slot) {
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) return false;
        boolean tapped = skillTapped[slot];
        skillTapped[slot] = false;
        return tapped;
    }

    // ===================== ATTACK SYSTEM =====================
    public boolean consumeAttackTap() {
        boolean tapped = attackTapped;
        attackTapped = false;
        return tapped;
    }

    // Helpers
    public String getSkillKeyLabel(int slot) {
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) return "-";
        return SKILL_KEY_LABELS[slot];
    }

    private int getSkillSlotFromCode(int code) {
        for (int i = 0; i < SKILL_KEY_CODES.length; i++) {
            if (SKILL_KEY_CODES[i] == code) return i;
        }
        return -1;
    }
}
