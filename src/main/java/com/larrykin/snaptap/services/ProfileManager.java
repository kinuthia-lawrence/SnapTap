package com.larrykin.snaptap.services;

import com.larrykin.snaptap.models.Hotkey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final Logger logger = LoggerFactory.getLogger(ProfileManager.class);
    private String currentProfileName = "default";
    private List<Hotkey> hotkeys = new ArrayList<>();

    public void saveProfile(String profileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("profiles/" + profileName + ".dat"))) {
            oos.writeObject(hotkeys);
            logger.info("Saved profile: {}", profileName);
        } catch (IOException e) {
            logger.error("Failed to save profile", e);
        }
    }

    public void loadProfile(String profileName) {
        File file = new File("profiles/" + profileName + ".dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                hotkeys = (List<Hotkey>) ois.readObject();
                currentProfileName = profileName;
                logger.info("Loaded profile: {}", profileName);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Failed to load profile", e);
            }
        }
    }
}