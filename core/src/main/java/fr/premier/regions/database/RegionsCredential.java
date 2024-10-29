package fr.premier.regions.database;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.sql.SqlCredential;
import fr.premier.regions.sql.SqlDriverType;
import org.bukkit.configuration.file.FileConfiguration;

public class RegionsCredential implements SqlCredential {

    private final FileConfiguration configuration;

    public RegionsCredential(RegionsPlugin plugin) {
        this.configuration = plugin.getConfig();
    }

    @Override
    public SqlDriverType getDriverType() {
        return SqlDriverType.valueOf(this.configuration.getString("driver", SqlDriverType.MYSQL.name()).toUpperCase());
    }

    @Override
    public String getCustomUrl() {
        return this.configuration.getString("custom-url", "");
    }

    @Override
    public String getHost() {
        return this.configuration.getString("host", "localhost");
    }

    @Override
    public int getPort() {
        return 3306;
    }

    @Override
    public String getDatabase() {
        return this.configuration.getString("database", "premier_regions");
    }

    @Override
    public String getUser() {
        return this.configuration.getString("user", "root");
    }

    @Override
    public String getPassword() {
        return this.configuration.getString("password", "");
    }

    @Override
    public int getPoolSize() {
        return this.configuration.getInt("max-pool-size", 5);
    }
}
