package fr.premier.regions.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlBiConsumer<K, V> {

    void accept(K key, V value) throws SQLException;
}
