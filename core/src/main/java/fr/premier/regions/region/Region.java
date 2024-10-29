package fr.premier.regions.region;

import fr.premier.regions.binary.impl.BinaryFlags;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Region {
    private final UUID uuid;
    private String name;
    private Location firstLocation;
    private Location secondLocation;
    private final BinaryFlags binaryFlags;
    private final List<Chunk> chunks;

    public Region(UUID uuid, String name, Location firstLocation, Location secondLocation, BinaryFlags binaryFlags, List<Chunk> chunks) {
        this.uuid = uuid;
        this.name = name;
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
        this.binaryFlags = binaryFlags;
        this.chunks = chunks;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public BinaryFlags getBinaryFlags() {
        return binaryFlags;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstLocation(Location minLocation) {
        this.firstLocation = minLocation;
    }

    public void setSecondLocation(Location maxLocation) {
        this.secondLocation = maxLocation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Region) obj;
        return Objects.equals(this.uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Region[" +
                "uuid=" + uuid + ", " +
                "name=" + name + ", " +
                "minLocation=" + firstLocation + ", " +
                "maxLocation=" + secondLocation + ", " +
                "binaryFlags=" + binaryFlags + ", " +
                "chunks=" + chunks + ']';
    }
}
