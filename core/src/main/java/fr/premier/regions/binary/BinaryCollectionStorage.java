package fr.premier.regions.binary;

import java.util.Collection;

public abstract class BinaryCollectionStorage<E extends Collection<?>> extends BinaryStorage<E> {
    public BinaryCollectionStorage(E value) {
        super(value);
    }
}
