package fr.premier.regions.binary.impl;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.binary.BinaryCollectionStorage;
import fr.premier.regions.region.Region;

import java.io.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BinaryWhitelist extends BinaryCollectionStorage<Set<Region>> {

    public BinaryWhitelist() {
        super(ConcurrentHashMap.newKeySet());
    }

    @Override
    protected byte[] serialize(Set<Region> value) {
        if (value.isEmpty()) return EMPTY_ARRAY;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (DataOutputStream output = new DataOutputStream(stream)) {
                output.writeInt(value.size());
                for (Region region : value) {
                    output.writeInt(RegionsPlugin.getInstance().getRegionManager().hashRegion(region));
                }
                return stream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void deserialize(byte[] array) {
        if (array == null || array.length == 0) return;
        try (DataInputStream stream = new DataInputStream(new ByteArrayInputStream(array))) {
            this.value.clear();
            final int size = stream.readInt();
            for (int i = 0; i < size; i++) {
                final Region region = RegionsPlugin.getInstance().getRegionManager().getRegion(stream.readInt());
                if (region == null) continue;
                this.value.add(region);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
