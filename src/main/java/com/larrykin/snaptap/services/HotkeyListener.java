package com.larrykin.snaptap.services;

import com.larrykin.snaptap.models.Hotkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.Map;

public class HotkeyListener implements NativeKeyListener {
    private static final Logger logger = LoggerFactory.getLogger(HotkeyListener.class);
    private final HotkeyManager hotkeyManager;

    public HotkeyListener(HotkeyManager hotkeyManager) {
        this.hotkeyManager = HotkeyManager.getInstance();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyCombo = NativeKeyEvent.getKeyText(e.getKeyCode());
        logger.info("Key pressed: {}", keyCombo);
        logger.info("Registered hotkeys: {}", hotkeyManager.getRegisteredHotkeys());
        Map<String, Hotkey> registeredHotkeys = hotkeyManager.getRegisteredHotkeys();

        for (Hotkey hotkey : registeredHotkeys.values()) {
            if (hotkey.getKeyCombo().contains(keyCombo) && hotkey.isEnabled()) {
                logger.info("Hotkey triggered: {}", hotkey.getName());
                hotkeyManager.executeAction(hotkey);
                break;
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // No action needed on key release
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // No action needed on key typed
    }

    public void startListening() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            logger.info("Hotkey listener started");
        } catch (Exception e) {
            logger.error("Failed to start hotkey listener", e);
        }
    }
}