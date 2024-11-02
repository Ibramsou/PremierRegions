package fr.premier.regions.database;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.binary.impl.BinaryFlags;
import fr.premier.regions.data.PlayerData;
import fr.premier.regions.region.Region;
import fr.premier.regions.sql.SqlDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            "max_z INTEGER NOT NULL, " +
            "flags varbinary(10000))";
    private static final String CREATE_WHITELIST_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS whitelist (" +
            "uuid VARCHAR(36) NOT NULL, " +
            "region VARCHAR(36) NOT NULL)";
    private static final String LOAD_WHITELIST_STATEMENT = "SELECT * FROM whitelist WHERE uuid = ?";
    private static final String WHITELIST_STATEMENT = "INSERT INTO whitelist (uuid, region) VALUES (?, ?)";
    private static final String UN_WHITELIST_STATEMENT = "DELETE FROM whitelist WHERE uuid = ? AND region = ?";
    private static final String LOAD_REGIONS_STATEMENT = "SELECT * FROM regions";
    private static final String INSERT_REGION_STATEMENT = "INSERT INTO regions (uuid, name, world, min_x, min_y, min_z, max_x, max_y, max_z, flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_REGION_STATEMENT = "DELETE FROM regions WHERE uuid = ?";
    private static final String UPDATE_REGION_FLAGS_STATEMENT = "UPDATE regions SET flags = ? WHERE uuid = ?";
    private static final String UPDATE_REGION_POSITIONS_STATEMENT = "UPDATE regions SET min_x = ?, min_y = ?, min_z, max_x = ?, max_y = ?, max_z = ? WHERE uuid = ?";
    private static final String UPDATE_REGION_NAME_STATEMENT = "UPDATE regions SET name = ? WHERE uuid = ?";
    private static final String LIST_WHITELIST_STATEMENT = "SELECT * FROM whitelist WHERE region = ?";
    private static final String CLEAR_WHITELISTED_REGION_STATEMENT = "DELETE FROM whitelist where region = ?";

    private final RegionsPlugin plugin;

    public RegionsDatabase(RegionsPlugin plugin) {
        super(new RegionsCredential(plugin));
        this.plugin = plugin;
        this.loadTables();
        this.loadRegions();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveWhitelists, 20L * 60L, 20L * 60L);
    }

    public void disable() {
        this.saveWhitelists();
    }

    private void loadTables() {
        this.createClosingStatement(statement -> {
            statement.executeUpdate(CREATE_REGIONS_TABLE_STATEMENT);
            statement.executeUpdate(CREATE_WHITELIST_TABLE_STATEMENT);
        });
    }

    private void loadRegions() {
        this.createClosingStatement(statement -> {
            try (ResultSet resultSet = statement.executeQuery(LOAD_REGIONS_STATEMENT)) {
                while (resultSet.next()) {
                    final UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    final String name = resultSet.getString("name");
                    final String worldName = resultSet.getString("world");
                    final World world = Bukkit.getWorld(worldName);
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
                    this.plugin.getRegionManager().loadRegionNameAndPosition(region);
                }
            }
        });
    }

    public void insertRegion(Region region) {
        this.prepareClosingStatement(INSERT_REGION_STATEMENT, statement -> {
            statement.setString(1, region.getUUID().toString());
            statement.setString(2, region.getName());
            statement.setString(3, region.getFirstLocation().getWorld().getName());
            statement.setInt(4, region.getFirstLocation().getBlockX());
            statement.setInt(5, region.getFirstLocation().getBlockY());
            statement.setInt(6, region.getFirstLocation().getBlockZ());
            statement.setInt(7, region.getSecondLocation().getBlockX());
            statement.setInt(8, region.getSecondLocation().getBlockY());
            statement.setInt(9, region.getSecondLocation().getBlockZ());
            statement.setBytes(10, region.getBinaryFlags().asBinary());
            statement.executeUpdate();
        });
    }

    public void deleteRegion(Region region) {
        this.prepareClosingStatement(DELETE_REGION_STATEMENT, statement -> {
            statement.setString(1, region.getUUID().toString());
            statement.executeUpdate();
        });
        this.prepareClosingStatement(CLEAR_WHITELISTED_REGION_STATEMENT, statement -> {
            statement.setString(1, region.getUUID().toString());
            statement.executeUpdate();
        });
    }

    public PlayerData loadUser(UUID uuid) {
        return this.resultPreparedStatement(LOAD_WHITELIST_STATEMENT, statement -> {
            statement.setString(1, uuid.toString());
            final PlayerData playerData = new PlayerData(uuid, ConcurrentHashMap.newKeySet());
            try (ResultSet resultSet = statement.executeQuery()) {
                Set<Region> regions = new HashSet<>();
                while (resultSet.next()) {
                    final UUID regionUUID = UUID.fromString(resultSet.getString("region"));
                    final Region region = this.plugin.getRegionManager().getRegion(regionUUID);
                    if (region == null) continue;
                    regions.add(region);
                }
                if (!regions.isEmpty()) playerData.getWhitelistedRegions().addAll(regions);
            }

            return playerData;
        });
    }

    public void saveWhitelists() {
        final Queue<PlayerData> queue = this.plugin.getPlayerDataManager().getSaveQueue();
        if (queue.isEmpty()) return;
        final Queue<WhitelistElement> whitelistQueue = new LinkedList<>();
        final Queue<WhitelistElement> unWhitelistQueue = new LinkedList<>();
        PlayerData playerData;
        while ((playerData = queue.poll()) != null) {
            playerData.setWaitingSave(false);
            for (Region unWhitelistedRegion : playerData.getWhitelistedRegions()) {
                if (!playerData.getCopyRegions().contains(unWhitelistedRegion)) {
                    whitelistQueue.add(new WhitelistElement(playerData.getUuid(), unWhitelistedRegion.getUUID()));
                }
            }
            for (Region whitelistedRegion : playerData.getCopyRegions()) {
                if (!playerData.getWhitelistedRegions().contains(whitelistedRegion)) {
                    unWhitelistQueue.add(new WhitelistElement(playerData.getUuid(), whitelistedRegion.getUUID()));
                }
            }
        }

        if (!whitelistQueue.isEmpty()) {
            this.prepareClosingStatement(WHITELIST_STATEMENT, statement -> {
                WhitelistElement whitelistElement;
                while ((whitelistElement = whitelistQueue.poll()) != null) {
                    statement.setString(1, whitelistElement.uuid.toString());
                    statement.setString(2, whitelistElement.regionUUID.toString());
                    statement.addBatch();
                }

                statement.executeBatch();
            });
        }

        if (!unWhitelistQueue.isEmpty()) {
            this.prepareClosingStatement(UN_WHITELIST_STATEMENT, statement -> {
                WhitelistElement whitelistElement;
                while ((whitelistElement = unWhitelistQueue.poll()) != null) {
                    statement.setString(1, whitelistElement.uuid.toString());
                    statement.setString(2, whitelistElement.regionUUID.toString());
                    statement.addBatch();
                }

                statement.executeBatch();
            });
        }
    }

    public void updateRegionName(Region region) {
        this.prepareClosingStatement(UPDATE_REGION_NAME_STATEMENT, statement -> {
            statement.setString(1, region.getName());
            statement.setString(2, region.getUUID().toString());
            statement.executeUpdate();
        });
    }

    public void updateRegionPositions(Region region) {
        this.prepareClosingStatement(UPDATE_REGION_POSITIONS_STATEMENT, statement -> {
            final Location first = region.getFirstLocation();
            final Location second = region.getSecondLocation();
            statement.setInt(1, first.getBlockX());
            statement.setInt(2, first.getBlockY());
            statement.setInt(3, first.getBlockZ());
            statement.setInt(4, second.getBlockX());
            statement.setInt(5, second.getBlockY());
            statement.setInt(6, second.getBlockZ());
            statement.setString(7, region.getUUID().toString());
            statement.executeUpdate();
        });
    }

    public List<OfflinePlayer> getWhitelistedPlayers(Region region) {
        final int hashcode = this.plugin.getRegionManager().hashRegion(region);
        final List<OfflinePlayer> players = new ArrayList<>();
        this.prepareClosingStatement(LIST_WHITELIST_STATEMENT, statement -> {
            statement.setInt(1, hashcode);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("uuid")));
                    if (!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) continue;
                    players.add(offlinePlayer);
                }
            }
        });

        return players;
    }

    public void updateRegionFlag(Region region) {
        this.prepareClosingStatement(UPDATE_REGION_FLAGS_STATEMENT, statement -> {
            statement.setBytes(1, region.getBinaryFlags().asBinary());
            statement.setString(2, region.getUUID().toString());
            statement.executeUpdate();
        });
    }

    private record WhitelistElement(UUID uuid, UUID regionUUID) {}
}
