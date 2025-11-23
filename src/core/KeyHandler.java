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



    // DETECT TYPED CHARACTERS
    @Override
    public void keyTyped(KeyEvent e){}



    // KEY MOVEMENT:  DETECT IF PRESSED
    @Override
    public void keyPressed(KeyEvent e){

        int code = e.getKeyCode();// Get the integer of the pressed key

        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;

        int slot = getSkillSlotFromCode(code);
        if (slot != -1) {
            if (!skillPressed[slot]) {
                skillTapped[slot] = true;
            }
            skillPressed[slot] = true;
        }

    }

    // KEY MOVEMENT: DETECT IF RELEASED
    @Override
    public void keyReleased(KeyEvent e){

        int code = e.getKeyCode();// Get the integer of the pressed key

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;

        int slot = getSkillSlotFromCode(code);
        if (slot != -1) {
            skillPressed[slot] = false;
        }

    }
    public boolean consumeSkillTap(int slot){
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) {
            return false;
        }
        boolean tapped = skillTapped[slot];
        skillTapped[slot] = false;
        return tapped;
    }

    public String getSkillKeyLabel(int slot){
        if (slot < 0 || slot >= SKILL_SLOT_COUNT) {
            return "-";
        }
        return SKILL_KEY_LABELS[slot];
    }

    private int getSkillSlotFromCode(int code){
        for (int i = 0; i < SKILL_KEY_CODES.length; i++) {
            if (SKILL_KEY_CODES[i] == code) {
                return i;
            }
        }
        return -1;
    }
}
