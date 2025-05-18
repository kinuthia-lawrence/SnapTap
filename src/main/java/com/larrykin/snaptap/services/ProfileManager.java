package com.larrykin.snaptap.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.larrykin.snaptap.enums.ActionType;
import com.larrykin.snaptap.models.Hotkey;
import com.larrykin.snaptap.models.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ProfileManager {
    private static final Logger logger = LoggerFactory.getLogger(ProfileManager.class);
    private static final String PROFILES_DIR = "profiles";

    private Map<String, Profile> profiles;
    private String activeProfileId;
    private final ObjectMapper objectMapper;
    private final HotkeyManager hotkeyManager;

    public ProfileManager() {
        this(HotkeyManager.getInstance());
    }

    public ProfileManager(HotkeyManager hotkeyManager) {
        logger.debug("ProfileManager initialized");
        this.profiles = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.hotkeyManager = hotkeyManager;

        // Create profiles directory if it doesn't exist
        try {
            Path profilesPath = Paths.get(PROFILES_DIR);
            if (!Files.exists(profilesPath)) {
                Files.createDirectories(profilesPath);
                createDefaultProfiles();
            } else {
                loadAllProfiles();
            }
        } catch (IOException e) {
            logger.error("Failed to create profiles directory: {}", e.getMessage());
        }
    }

    private void createDefaultProfiles() {
        // Create default profile
        Profile defaultProfile = createProfile("Default");
        setActiveProfile(defaultProfile.getId());
        saveProfile(defaultProfile);
    }

    public Profile createProfile(String name) {
        Profile profile = new Profile(name);
        profiles.put(profile.getId(), profile);
        saveProfile(profile);
        return profile;
    }

    public void deleteProfile(String profileId) {
        if (profiles.containsKey(profileId)) {
            profiles.remove(profileId);
            File profileFile = new File(PROFILES_DIR + File.separator + profileId + ".json");
            if (profileFile.exists()) {
                profileFile.delete();
            }
        }
    }

    public void saveProfile(Profile profile) {
        try {
            File profileFile = new File(PROFILES_DIR + File.separator + profile.getId() + ".json");
            objectMapper.writeValue(profileFile, profile);
            profiles.put(profile.getId(), profile);
        } catch (IOException e) {
            logger.error("Failed to save profile {}: {}", profile.getName(), e.getMessage());
        }
    }

    public void saveAllProfiles() {
        for (Profile profile : profiles.values()) {
            saveProfile(profile);
        }
    }

    public Profile loadProfile(String profileId) {
        File profileFile = new File(PROFILES_DIR + File.separator + profileId + ".json");
        if (profileFile.exists()) {
            try {
                Profile profile = objectMapper.readValue(profileFile, Profile.class);
                profiles.put(profile.getId(), profile);
                return profile;
            } catch (IOException e) {
                logger.error("Failed to load profile {}: {}", profileId, e.getMessage());
            }
        }
        return null;
    }

    public void loadAllProfiles() {
        logger.info("Loading profiles from directory: {}", PROFILES_DIR);
        File profilesDir = new File(PROFILES_DIR);

        if (!profilesDir.exists()) {
            logger.warn("Profiles directory does not exist: {}", profilesDir.getAbsolutePath());
            return;
        }

        if (!profilesDir.isDirectory()) {
            logger.warn("Profiles path exists but is not a directory: {}", profilesDir.getAbsolutePath());
            return;
        }

        logger.info("Scanning for profile files in: {}", profilesDir.getAbsolutePath());
        File[] profileFiles = profilesDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (profileFiles == null || profileFiles.length == 0) {
            logger.info("No profile files found in directory");
            return;
        }

        logger.info("Found {} profile files", profileFiles.length);

        for (File file : profileFiles) {
            try {
                logger.debug("Loading profile from file: {}", file.getName());
                Profile profile = objectMapper.readValue(file, Profile.class);
                profiles.put(profile.getId(), profile);

                if (profile.isActive()) {
                    activeProfileId = profile.getId();
                    logger.info("Set active profile: {}", profile.getName());
                }
                logger.info("Profile loaded: {}", profile.getName());
            } catch (IOException e) {
                logger.error("Failed to load profile from file {}: {}", file.getName(), e.getMessage());
            }
        }

        // If no active profile found, set the first one as active
        if (activeProfileId == null && !profiles.isEmpty()) {
            String firstProfileId = profiles.values().iterator().next().getId();
            setActiveProfile(firstProfileId);
            logger.info("No active profile found. Setting profile as active: {}",
                    profiles.get(firstProfileId).getName());
        }

        logger.info("Profile loading complete. Loaded {} profiles", profiles.size());
    }

    public Profile getActiveProfile() {
        return activeProfileId != null ? profiles.get(activeProfileId) : null;
    }

    public void setActiveProfile(String profileId) {
        if (profiles.containsKey(profileId)) {
            // Deactivate current profile
            if (activeProfileId != null && profiles.containsKey(activeProfileId)) {
                Profile currentActive = profiles.get(activeProfileId);
                currentActive.setActive(false);
                saveProfile(currentActive);

                // Unregister all hotkeys from the current profile
                unregisterProfileHotkeys(currentActive);
                logger.info("Deactivated profile: {}", currentActive.getName());
            }

            // Activate new profile
            activeProfileId = profileId;
            Profile newActive = profiles.get(profileId);
            newActive.setActive(true);
            saveProfile(newActive);

            // Register all hotkeys from the new active profile
            registerProfileHotkeys(newActive);
            logger.info("Active profile set to: {}", newActive.getName());
        }
    }

    public void registerProfileHotkeys(Profile profile) {
        for (Hotkey hotkey : profile.getHotkeys()) {
            hotkeyManager.registerHotkey(hotkey);
        }
    }

    private void unregisterProfileHotkeys(Profile profile) {
        // Implement when you add an unregister method to HotkeyManager
    }

    public List<Profile> getAllProfiles() {
        return new ArrayList<>(profiles.values());
    }

    public void addHotkeyToActiveProfile(Hotkey hotkey) {
        Profile activeProfile = getActiveProfile();
        if (activeProfile != null) {
            activeProfile.addHotkey(hotkey);
            saveProfile(activeProfile);
            hotkeyManager.registerHotkey(hotkey);
        }
    }

    public boolean updateHotkey(String profileId, Hotkey updatedHotkey) {
        if (profiles.containsKey(profileId)) {
            Profile profile = profiles.get(profileId);
            for (int i = 0; i < profile.getHotkeys().size(); i++) {
                if (profile.getHotkeys().get(i).getId().equals(updatedHotkey.getId())) {
                    profile.getHotkeys().set(i, updatedHotkey);
                    saveProfile(profile);
                    return true;
                }
            }
        }
        return false;
    }
}