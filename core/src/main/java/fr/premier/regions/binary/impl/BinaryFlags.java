package fr.premier.regions.binary.impl;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.binary.BinaryMapStorage;
import fr.premier.regions.flag.Flag;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BinaryFlags extends BinaryMapStorage<Map<Flag, FlagState>> {

    public BinaryFlags() {
        super(new HashMap<>());
    }

    @Override
    protected byte[] serialize(Map<Flag, FlagState> value) {
        if (value.isEmpty()) return EMPTY_ARRAY;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (DataOutputStream output = new DataOutputStream(stream)) {
                output.writeInt(value.size());
                for (Map.Entry<Flag, FlagState> entry : value.entrySet()) {
                    final Flag flag = entry.getKey();
                    final FlagState state = entry.getValue();
                    output.writeUTF(flag.getName());
                    output.writeByte(state.ordinal());
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
                final String name = stream.readUTF();
                final byte ordinal = stream.readByte();
                final Flag flag = RegionsPlugin.getInstance().getFlagManager().getDefaultFlag(name);
                if (flag == null) continue;
                value.put(flag, FlagState.values()[ordinal]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
