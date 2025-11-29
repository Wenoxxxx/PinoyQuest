package src.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public static final int SKILL_SLOT_COUNT = 3;
    private static final String[] SKILL_KEY_LABELS = {"J", "K", "L"};
    private static final int[] SKILL_KEY_CODES = {
        KeyEvent.VK_J,
        KeyEvent.VK_K,
        KeyEvent.VK_L
    };

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    private final boolean[] skillPressed = new boolean[SKILL_SLOT_COUNT];
    private final boolean[] skillTapped = new boolean[SKILL_SLOT_COUNT];

    private final GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

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
            if (gp.menuSelectedIndex < 0) {
                gp.menuSelectedIndex = gp.menuOptions.length - 1;
            }
        } 
        else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.menuSelectedIndex++;
            if (gp.menuSelectedIndex >= gp.menuOptions.length) {
                gp.menuSelectedIndex = 0;
            }
        } 
        else if (code == KeyEvent.VK_ENTER) {
            int i = gp.menuSelectedIndex;

            if (i == 0) gp.startNewGame();    // Start
            else if (i == 1 && gp.canResume) gp.resumeGame(); // Resume
            else if (i == 2) gp.openSettings();               // Settings
            else if (i == 3) System.exit(0);                  // Quit
        }
    }

    // ===================== PLAY =====================
    private void handlePlayInput(int code) {
        // movement
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;

        // skills
        int slot = getSkillSlotFromCode(code);
        if (slot != -1) {
            if (!skillPressed[slot]) {
                skillTapped[slot] = true;
            }
            skillPressed[slot] = true;
        }

        // OPEN INVENTORY (Switch State)
        if (code == KeyEvent.VK_I) {

            // Move to Inventory State
            gp.gameState = GamePanel.STATE_INVENTORY;

            // Force stop all player movement immediately
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;

            return;
        }

        // GO BACK TO MENU
        if (code == KeyEvent.VK_ESCAPE) {
            gp.canResume = true;
            gp.gameState = GamePanel.STATE_MENU;
        }
    }

    // ===================== INVENTORY =====================
    private void handleInventoryInput(int code) {

        // CLOSE inventory on I or ESC
        if (code == KeyEvent.VK_I || code == KeyEvent.VK_ESCAPE) {

                // Reset movement keys to avoid ghost movement
                upPressed = false;
                downPressed = false;
                leftPressed = false;
                rightPressed = false;

                gp.gameState = GamePanel.STATE_PLAY;
        }

        // MOVE CURSOR WITH WASD
        if (code == KeyEvent.VK_W) gp.ui.getInventoryUI().moveCursorUp();
        if (code == KeyEvent.VK_S) gp.ui.getInventoryUI().moveCursorDown();
        if (code == KeyEvent.VK_A) gp.ui.getInventoryUI().moveCursorLeft();
        if (code == KeyEvent.VK_D) gp.ui.getInventoryUI().moveCursorRight();

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
            if (slot != -1) {
                skillPressed[slot] = false;
            }
        }
    }

    // ===================== Skill Helpers =====================
    public boolean consumeSkillTap(int slot) {
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) return false;
        boolean tapped = skillTapped[slot];
        skillTapped[slot] = false;
        return tapped;
    }

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
