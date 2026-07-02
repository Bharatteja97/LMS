package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
    private static Properties properties;
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    static {
        try {
            String path = "src/main/resources/config.properties";
            FileInputStream input = new FileInputStream(path);
            properties = new Properties();
            properties.load(input);
            input.close();
        } catch (IOException e) {
            logger.error("Configuration properties file cannot be found at src/main/resources/config.properties", e);
            throw new RuntimeException("config.properties not found");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
