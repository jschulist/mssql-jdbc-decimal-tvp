package com.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbProperties {
    final static Logger _logger = LoggerFactory.getLogger(DbProperties.class);

    private static final String PROPERTY_FILENAME = "db.properties";
    public static final DbProperties DEFAULT = new DbProperties();

    public Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTY_FILENAME)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            _logger.error("", ex);
        }

        return properties;
    }
}