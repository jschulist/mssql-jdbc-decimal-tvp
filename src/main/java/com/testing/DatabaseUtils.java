package com.testing;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class DatabaseUtils {
    public static DataSource getDataSource(Properties properties) {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName(properties.getProperty("DB_SERVERNAME"));
        dataSource.setDatabaseName(properties.getProperty("DB_DATABASE"));
        dataSource.setUser(properties.getProperty("DB_USERNAME"));
        dataSource.setPassword(properties.getProperty("DB_PASSWORD"));

        return dataSource;
    }
}

