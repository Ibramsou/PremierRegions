package fr.premier.regions.database;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.sql.SqlDatabase;

public class RegionsDatabase extends SqlDatabase {

    private static final String CREATE_REGIONS_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS regions (" +
            "uuid VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
            "min_x INTEGER NOT NULL, " +
            "min_y INTEGER NOT NULL, " +
            "min_z INTEGER NOT NULL, " +
            "max_x INTEGER NOT NULL, " +
            "max_y INTEGER NOT NULL, " +
            "max_z INTEGER NOT NULL " +
            "flags VARBINARY(10000))";
    private static final String CREATE_PLAYERS_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS players (" +
            "uuid VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
            "whitelisted_regions VARBINARY(10000))";
    private static final String LOAD_USER_STATEMENT = "SELECT * FROM players WHERE uuid = ?";
    private static final String SAVE_USER_STATEMENT = "INSERT INTO players (uuid, whitelisted_regions) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE whitelisted_regions = ?";
    private static final String LOAD_REGIONS_STATEMENT = "SELECT * FROM regions";
    private static final String SAVE_REGION_STATEMENT = "INSERT INTO regions (uuid, min_x, min_y, min_z, max_x, max_y, max_z, flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
            "ON DUPLICATE KEY UPDATE flags = ?";
    private static final String DELETE_REGION_STATEMENT = "DELETE FROM regions WHERE uuid = ?";

    private final RegionsPlugin plugin;

    public RegionsDatabase(RegionsPlugin plugin) {
        super(new RegionsCredential());
        this.plugin = plugin;
        this.loadTables();
        this.loadRegions();
    }

    private void loadTables() {
        this.createClosingStatement(statement -> {
            statement.executeUpdate(CREATE_REGIONS_TABLE_STATEMENT);
            statement.executeUpdate(CREATE_PLAYERS_TABLE_STATEMENT);
        });
    }

    private void loadRegions() {

    }
}
