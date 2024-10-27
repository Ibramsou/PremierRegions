package fr.premier.regions.database;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.binary.impl.BinaryFlags;
import fr.premier.regions.binary.impl.BinaryWhitelist;
import fr.premier.regions.data.PlayerData;
import fr.premier.regions.region.Region;
import fr.premier.regions.sql.SqlDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;

public class RegionsDatabase extends SqlDatabase {

    private static final String CREATE_REGIONS_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS regions (" +
            "uuid VARCHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "world VARCHAR(255) NOT NULL, " +
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
    private static final String LOAD_USER_STATEMENT = "SELECT whitelisted_regions FROM players WHERE uuid = ?";
    private static final String SAVE_USER_STATEMENT = "INSERT INTO players (uuid, whitelisted_regions) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE whitelisted_regions = ?";
    private static final String LOAD_REGIONS_STATEMENT = "SELECT * FROM regions";
    private static final String INSERT_REGION_STATEMENT = "INSERT INTO regions (name, min_x, min_y, min_z, max_x, max_y, max_z, flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_REGION_STATEMENT = "DELETE FROM regions WHERE name = ?";
    private static final String UPDATE_REGION_FLAGS_STATEMENT = "UPDATE regions SET flags = ? WHERE name = ?";

    private final RegionsPlugin plugin;

    public RegionsDatabase(RegionsPlugin plugin) {
        super(new RegionsCredential());
        this.plugin = plugin;
        this.loadTables();
        this.loadRegions();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveUsers, 20L * 60L, 20L * 60L);
    }

    public void disable() {
        this.saveUsers();
    }

    private void loadTables() {
        this.createClosingStatement(statement -> {
            statement.executeUpdate(CREATE_REGIONS_TABLE_STATEMENT);
            statement.executeUpdate(CREATE_PLAYERS_TABLE_STATEMENT);
        });
    }

    private void loadRegions() {
        this.createClosingStatement(statement -> {
            try (ResultSet resultSet = statement.executeQuery(LOAD_REGIONS_STATEMENT)) {
                while (resultSet.next()) {
                    final UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    final String name = resultSet.getString("name");
                    final World world = Bukkit.getWorld(name);
                    if (world == null) continue;
                    final int minX = resultSet.getInt("min_x");
                    final int minY = resultSet.getInt("min_y");
                    final int minZ = resultSet.getInt("min_z");
                    final int maxX = resultSet.getInt("max_x");
                    final int maxY = resultSet.getInt("max_y");
                    final int maxZ = resultSet.getInt("max_z");
                    final byte[] flagsBinary = resultSet.getBytes("flags");
                    final Location min = new Location(world, minX, minY, minZ);
                    final Location max = new Location(world, maxX, maxY, maxZ);
                    final BinaryFlags binaryFlags = new BinaryFlags();
                    binaryFlags.loadValue(flagsBinary);
                    final Region region = new Region(uuid, name, min, max, binaryFlags, new ArrayList<>());
                    this.plugin.getRegionManager().loadRegion(region);
                }
            }
        });
    }

    public void insertRegion(Region region) {
        this.prepareClosingStatement(INSERT_REGION_STATEMENT, statement -> {
            statement.setString(1, region.uuid().toString());
            statement.setString(2, region.name());
            statement.setString(3, region.minLocation().getWorld().getName());
            statement.setInt(4, region.minLocation().getBlockX());
            statement.setInt(5, region.minLocation().getBlockY());
            statement.setInt(6, region.minLocation().getBlockZ());
            statement.setInt(7, region.maxLocation().getBlockX());
            statement.setInt(8, region.maxLocation().getBlockY());
            statement.setInt(9, region.maxLocation().getBlockZ());
            statement.setBytes(10, region.binaryFlags().asBinary());
            statement.executeUpdate();
        });
    }

    public void deleteRegion(Region region) {
        this.prepareClosingStatement(DELETE_REGION_STATEMENT, statement -> {
            statement.setString(1, region.uuid().toString());
            statement.executeUpdate();
        });
    }

    public PlayerData loadUser(UUID uuid) {
        return this.resultPreparedStatement(LOAD_USER_STATEMENT, statement -> {
            statement.setString(1, uuid.toString());
            final PlayerData playerData = new PlayerData(uuid, new BinaryWhitelist());
            try (ResultSet resultSet = statement.executeQuery(LOAD_USER_STATEMENT)) {
                if (resultSet.next()) {
                    byte[] whitelisted_regions = resultSet.getBytes("whitelisted_regions");
                    playerData.getBinaryWhitelistedRegions().loadValue(whitelisted_regions);
                }
            }

            return playerData;
        });
    }

    public void saveUsers() {
        final Queue<PlayerData> queue = this.plugin.getPlayerDataManager().getSaveQueue();
        if (queue.isEmpty()) return;
        this.prepareClosingStatement(SAVE_USER_STATEMENT, statement -> {
            PlayerData playerData;
            while ((playerData = queue.poll()) != null) {
                statement.setString(1, playerData.toString());
                statement.setBytes(2, playerData.getBinaryWhitelistedRegions().asBinary());
                statement.addBatch();
            }

            statement.executeBatch();
        });
    }

    public void updateRegionFlag(Region region) {
        this.prepareClosingStatement(UPDATE_REGION_FLAGS_STATEMENT, statement -> {
            statement.setBytes(1, region.binaryFlags().asBinary());
            statement.setString(2, region.uuid().toString());
            statement.executeUpdate();
        });
    }
}
