package ru.pshiblo;

import ru.pshiblo.property.ConfigProperties;
import ru.pshiblo.property.Property;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TextConfig {

    private static final TextConfig instance = new TextConfig();

    public static TextConfig getInstance() {
        return instance;
    }

    private Map<String, String> texts;
    private ConfigProperties property;

    public TextConfig() {
        try {
            property = new ConfigProperties(Config.getInstance().getPath() + "\\configText.properties");
            texts = (Map)property;

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            property.store(new FileOutputStream(Config.getInstance().getPath() + "\\configText.properties"), "by Pshiblo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getTexts() {
        return texts;
    }
}
