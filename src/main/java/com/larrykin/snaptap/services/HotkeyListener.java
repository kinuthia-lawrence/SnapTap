package com.larrykin.snaptap.services;

import com.larrykin.snaptap.models.Hotkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Listens for global keyboard events and triggers actions for registered hotkeys.
 * Implements the `NativeKeyListener` interface to handle key press and release events.
 */
public class HotkeyListener implements NativeKeyListener {
    // Logger for logging events and errors
    private static final Logger logger = LoggerFactory.getLogger(HotkeyListener.class);
    // Instance of HotkeyManager to manage hotkeys
    private final HotkeyManager hotkeyManager;
    private final Set<String> pressedKeys = new HashSet<>(); // Track currently pressed keys

    /**
     * Constructor for HotkeyListener.
     *
     * @param hotkeyManager Instance of HotkeyManager to manage hotkeys.
     */
    public HotkeyListener(HotkeyManager hotkeyManager) {
        this.hotkeyManager = hotkeyManager;
    }

    /**
     * Handles key press events.
     * Checks if any registered hotkey is triggered and executes the corresponding action.
     *
     * @param e The NativeKeyEvent containing information about the key event.
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        pressedKeys.add(keyText); // Add the pressed key to the set
//        logger.info("Key pressed: {}", keyText);

        Map<String, Hotkey> registeredHotkeys = hotkeyManager.getRegisteredHotkeys();
//        logger.info("Registered hotkeys: {}", registeredHotkeys);

        for (Hotkey hotkey : registeredHotkeys.values()) {
            if (isHotkeyTriggered(hotkey)) {
                logger.info("Hotkey triggered: {}", hotkey.getName());
                hotkeyManager.executeAction(hotkey);
                break; // Prevent multiple hotkeys from triggering simultaneously
            }
        }
    }

    /**
     * Handles key release events.
     * Updates the set of pressed keys.
     *
     * @param e The NativeKeyEvent containing information about the key event.
     */
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        pressedKeys.remove(keyText); // Remove the released key from the set
//        logger.info("Key released: {}", keyText);
    }

    /**
     * Handles key typed events.
     *
     * @param e The NativeKeyEvent containing information about the key event.
     */
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // No action needed on key typed
    }

    /**
     * Checks if a hotkey is triggered based on the currently pressed keys.
     *
     * @param hotkey The Hotkey object to check.
     * @return true if the hotkey is triggered, false otherwise.
     */
    private boolean isHotkeyTriggered(Hotkey hotkey) {
        // Split the hotkey combination into individual keys
        String[] hotkeyKeys = hotkey.getKeyCombo().split("\\+");
        for (String key : hotkeyKeys) {
            if (!pressedKeys.contains(key.trim())) {
                return false; // If any key in the combination is not pressed, return false
            }
        }
        return true; // All keys in the combination are pressed
    }

    /**
     * Starts listening for global keyboard events.
     * Registers the listener with the GlobalScreen.
     */
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