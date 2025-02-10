package org.App.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OptionsManager {
    private static final String FILE_PATH = "src/main/resources/config.properties";
    private static Properties properties = new Properties();

    static {
        loadOptions();
    }

    private static void loadOptions() {
        try (InputStream input = OptionsManager.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                System.err.println("Fichier de configuration non trouvé !");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTheme() {
        return properties.getProperty("theme", "Clair");
    }

    public static String getMode() {
        return properties.getProperty("mode", "Classique");
    }

    public static void saveOptions(String theme, String mode) {
        properties.setProperty("theme", theme);
        properties.setProperty("mode", mode);

        try (FileOutputStream output = new FileOutputStream(FILE_PATH)) {
            properties.store(output, "Configuration du jeu Skyjo");
            System.out.println("Options sauvegardées !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
