package org.tyler.husher.ui;

import javafx.scene.text.Font;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourceManager {
    
    public static InputStream openFontStream(String name, String fontWeight, String extension) {
        String path = String.format("%s-%s.%s", name, fontWeight, extension);
        return ResourceManager.class.getClassLoader().getResourceAsStream(path);
    }

    public static InputStream openFontStream(String name, String fontWeight) {
        return openFontStream(name, fontWeight, "ttf");
    }

    public static Font getFont(InputStream stream, float size) {
        return Font.loadFont(stream, size);
    }

    public static Font getFont(String name, String fontWeight, float size) {
        return getFont(openFontStream(name, fontWeight), size);
    }

    public static Font getRobotoFont(String fontWeight, float size) {
        return getFont(openFontStream("Roboto", fontWeight), size);
    }

    public static Font getMonoFont(String fontWeight, float size) {
        return getFont(openFontStream("RobotoMono", fontWeight), size);
    }

    public static Font getMontserratFont(String fontWeight, float size) {
        return getFont(openFontStream("Montserrat", fontWeight), size);
    }

    public static Font getDiscordFont(String fontWeight, float size) {
        return getFont(openFontStream("Discord", fontWeight, "otf"), size);
    }

    public static InputStream openResource(String path) {
        // FIXME resource loader
        try {
            // return ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            return ResourceManager.class.getResourceAsStream(path);
            // return Objects.requireNonNull(ResourceManager.class.getResource(path), "Resource \"" + path + "\" does not exist").openStream();
        } catch (Exception e) {
            System.err.println("FAILED TO LOAD RESOURCE");
            return null;
        }
    }

}
