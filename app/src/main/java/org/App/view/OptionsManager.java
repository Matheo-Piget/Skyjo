package org.App.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application configuration options such as theme and game mode.
 * This class is responsible for loading and saving configuration options from/to a properties file.
 * 
 * @version 1.0
 * @author Piget Mathéo
 * @see Properties
 */
public class OptionsManager {
    private static final String FILE_PATH = "src/main/resources/config.properties";
    private static final Properties properties = new Properties();

    static {
        loadOptions();
    }

    /**
     * Loads the configuration options from the properties file.
     * If the file is not found, an error message is printed to the standard error stream.
     */
    private static void loadOptions() {
        try (InputStream input = OptionsManager.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                System.err.println("Configuration file not found!");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading configuration options: " + e.getMessage());
        }
    }

    /**
     * Retrieves the currently selected theme.
     * 
     * @return The selected theme, or "Clair" (Light) as the default if not specified.
     */
    public static String getTheme() {
        return properties.getProperty("theme", "Clair");
    }

    /**
     * Retrieves the currently selected game mode.
     * 
     * @return The selected game mode, or "Classique" (Classic) as the default if not specified.
     */
    public static String getMode() {
        return properties.getProperty("mode", "Classique");
    }

    /**
     * Saves the selected theme and game mode to the configuration file.
     * 
     * @param theme The theme to save.
     * @param mode The game mode to save.
     * @throws IOException If an error occurs while writing to the configuration file.
     */
    public static void saveOptions(String theme, String mode) throws IOException {
        if (theme == null || mode == null) {
            throw new IllegalArgumentException("Theme and mode cannot be null.");
        }

        properties.setProperty("theme", theme);
        properties.setProperty("mode", mode);

        try (FileOutputStream output = new FileOutputStream(FILE_PATH)) {
            properties.store(output, "Skyjo Game Configuration");
            System.out.println("Options saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving options: " + e.getMessage());
            throw e; // Re-throw the exception for the caller to handle
        }
    }
}