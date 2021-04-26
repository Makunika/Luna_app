package ru.pshiblo.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties extends Properties {

    public ConfigProperties(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists())
            file.createNewFile();

        FileInputStream fis = new FileInputStream(file);
        load(fis);
    }

    public long getLongProperty(String property, long defaultValue) {
        String property1 = getProperty(property, null);

        if (property1 == null) {
            return defaultValue;
        }

        return Long.parseLong(property1);
    }

}
