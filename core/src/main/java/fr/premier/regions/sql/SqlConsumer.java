package fr.premier.regions.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<V> {

    void accept(V value) throws SQLException;
}
