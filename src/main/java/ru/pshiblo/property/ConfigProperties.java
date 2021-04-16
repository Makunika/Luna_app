package ru.pshiblo.property;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties extends Properties {

    public ConfigProperties(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
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
