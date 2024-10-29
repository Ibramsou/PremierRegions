package fr.premier.regions.stage;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.*;

public class StageManager {

    private final Map<UUID, Stage<?, ?>> stageMap = new HashMap<>();

    public <V extends Event> void addStage(Player player, Stage<V, ?> stage) {
        this.stageMap.put(player.getUniqueId(), stage);
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(stage.chatMessage()));
    }

    @SuppressWarnings("unchecked")
    public <V extends Event, K> void executeStage(UUID uuid, V event) {
        Stage<?, ?> currentStage = this.stageMap.get(uuid);
        if (currentStage.eventType() == event.getClass()) return;
        Stage<V, K> castStage = (Stage<V, K>) currentStage;
        if (event instanceof AsyncChatEvent chatEvent) {
            if (LegacyComponentSerializer.legacySection().serialize(chatEvent.message()).equals("cancel")) {
                chatEvent.setCancelled(true);
                chatEvent.getPlayer().sendMessage(Component.text("§cOperation cancelled."));
                this.stageMap.remove(uuid);
                return;
            }
        }

        StageResult result = castStage.execute(event, castStage.eventToObjectFunction().apply(event));
        if (result == StageResult.NONE) return;
        if (result == StageResult.CANCELLED) {
            if (event instanceof Cancellable cancellable) cancellable.setCancelled(true);
            return;
        }
        if (result == StageResult.DONE) {
            if (event instanceof Cancellable cancellable) cancellable.setCancelled(true);
            this.stageMap.remove(uuid);
        }
    }
}

