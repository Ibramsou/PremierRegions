package fr.premier.regions.database;

import fr.premier.regions.sql.SqlCredential;
import fr.premier.regions.sql.SqlDriverType;

public class RegionsCredential implements SqlCredential {
    @Override
    public SqlDriverType getDriverType() {
        return SqlDriverType.MYSQL;
    }

    @Override
    public String getCustomUrl() {
        return ""; // None
    }

    // TODO: Replace by config values
    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public int getPort() {
        return 3306;
    }

    @Override
    public String getDatabase() {
        return "premier_regions";
    }

    @Override
    public String getUser() {
        return "root";
    }

    @Override
    public String getPassword() {
        return "root";
    }

    @Override
    public int getPoolSize() {
        return 5;
    }
}
