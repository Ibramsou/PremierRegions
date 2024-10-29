package fr.premier.regions.stage;

import org.bukkit.event.Event;

import java.util.function.Function;

public interface Stage<K extends Event, V> {

    StageResult execute(K event, V object);

    Function<K, V> eventToObjectFunction();

    Class<K> eventType();

    String chatMessage();
}
