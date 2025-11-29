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
        } else if (gp.gameState == GamePanel.STATE_PLAY) {
            handlePlayInput(code);
        } else if (gp.gameState == GamePanel.STATE_SETTINGS) {
            handleSettingsInput(code);
        }
    }

    private void handleMenuInput(int code) {
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            gp.menuSelectedIndex--;
            if (gp.menuSelectedIndex < 0) {
                gp.menuSelectedIndex = gp.menuOptions.length - 1;
            }
        } else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            gp.menuSelectedIndex++;
            if (gp.menuSelectedIndex >= gp.menuOptions.length) {
                gp.menuSelectedIndex = 0;
            }
        } else if (code == KeyEvent.VK_ENTER) {
            int i = gp.menuSelectedIndex;

            if (i == 0) {               // Start
                gp.startNewGame();
            } else if (i == 1) {        // Resume
                if (gp.canResume) {
                    gp.resumeGame();
                }
            } else if (i == 2) {        // Settings
                gp.openSettings();
            } else if (i == 3) {        // Quit
                System.exit(0);
            }
        }
    }

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

        // ========== INVENTORY TOGGLE ==========
        if (code == KeyEvent.VK_I) {
            gp.showInventory = !gp.showInventory;

            // OPTIONAL: freeze movement when inventory is open
            if (gp.showInventory) {
                upPressed = false;
                downPressed = false;
                leftPressed = false;
                rightPressed = false;
            }
        }


        
        // pause back to menu
        if (code == KeyEvent.VK_ESCAPE) {
            gp.canResume = true;
            gp.gameState = GamePanel.STATE_MENU;
        }
    }

    private void handleSettingsInput(int code) {
        // just ESC to go back for now
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

    public boolean consumeSkillTap(int slot) {
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) {
            return false;
        }
        boolean tapped = skillTapped[slot];
        skillTapped[slot] = false;
        return tapped;
    }

    public String getSkillKeyLabel(int slot) {
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) {
            return "-";
        }
        return SKILL_KEY_LABELS[slot];
    }

    private int getSkillSlotFromCode(int code) {
        for (int i = 0; i < SKILL_KEY_CODES.length; i++) {
            if (SKILL_KEY_CODES[i] == code) {
                return i;
            }
        }
        return -1;
    }
}
