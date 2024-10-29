package fr.premier.regions.api.region;

import fr.premier.regions.api.PreRegionsAPI;
import fr.premier.regions.api.flag.PreFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreRegionUtils {


    public static <V extends Event & Cancellable> void handleCancelableEvent(V event, Location location, PreFlag preFlag) {
        handleCancelableEvent(event, null, location, preFlag);
    }

    public static <V extends Event & Cancellable> void handleCancelablePlayerEvent(V event, Player player, Location location, PreFlag preFlag) {
        handleCancelableEvent(event, player.getUniqueId(), location, preFlag);
    }

    public static <V extends Event & Cancellable> void handleCancelablePlayerEvent(V event, Player player, PreFlag preFlag) {
        handleCancelableEvent(event, player.getUniqueId(), player.getLocation(), preFlag);
    }

    public static <V extends Event & Cancellable> void handleCancelableEvent(V event, UUID uuid, Location location, PreFlag preFlag) {
        handleEvent(event, uuid, location, preFlag, Cancellable::isCancelled, v -> v.setCancelled(true), v -> v.setCancelled(false));
    }

    public static <V extends Event> void handlePlayerEvent(V event, Player player, Location location, PreFlag preFlag, Predicate<V> alreadyCancelled, Consumer<V> cancelled, Consumer<V> allowed) {
        handleEvent(event, player.getUniqueId(), location, preFlag, alreadyCancelled, cancelled, allowed);
    }

    public static <V extends Event> void handlePlayerEvent(V event, Player player, PreFlag preFlag, Predicate<V> alreadyCancelled, Consumer<V> cancelled, Consumer<V> allowed) {
        handleEvent(event, player.getUniqueId(), player.getLocation(), preFlag, alreadyCancelled, cancelled, allowed);
    }


    public static <V extends Event> void handleEvent(V event, Location location, PreFlag preFlag, Predicate<V> alreadyCancelled, Consumer<V> cancelled, Consumer<V> allowed) {
        handleEvent(event, null, location, preFlag, alreadyCancelled, cancelled, allowed);
    }

    public static <V extends Event> void handleEvent(V event, @Nullable UUID uuid, Location location, PreFlag preFlag, Predicate<V> alreadyCancelled, Consumer<V> cancelled, Consumer<V> allowed) {
        final PreRegionEventResult result = PreRegionsAPI.getInstance().getEventResult(uuid, preFlag, location, alreadyCancelled.test(event));
        if (result == PreRegionEventResult.NONE) return;
        if (result == PreRegionEventResult.CANCEL) {
            cancelled.accept(event);
        } else if (result == PreRegionEventResult.ALLOW) {
            allowed.accept(event);
        }
    }
}
