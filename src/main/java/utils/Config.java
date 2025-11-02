package utils;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties PROPS = new Properties();

    static {
        try {
            try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) PROPS.load(in);
            }
            try (InputStream inLocal = Config.class.getClassLoader().getResourceAsStream("config.local.properties")) {
                if (inLocal != null) PROPS.load(inLocal);
            }
        } catch (Exception ignored) {}
    }

    public static String get(String key, String def) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) return v;

        String envKey = key.replace('.', '_').toUpperCase();
        v = System.getenv(envKey);
        if (v != null && !v.isBlank()) return v;

        v = PROPS.getProperty(key);
        if (v != null && !v.isBlank()) return v;

        return def;
    }

    public static String get(String key) { return get(key, null); }
}
