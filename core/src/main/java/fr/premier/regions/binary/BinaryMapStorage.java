package fr.premier.regions.binary;

import java.util.Map;

public abstract class BinaryMapStorage<E extends Map<?, ?>> extends BinaryStorage<E> {

    public BinaryMapStorage(E value) {
        super(value);
    }
}
