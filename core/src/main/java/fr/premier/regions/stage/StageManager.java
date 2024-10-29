package fr.premier.regions.stage;

import fr.premier.regions.stage.impl.ChatStage;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.function.BiConsumer;

public class StageManager {

    private final Map<UUID, Stage<?, ?>> stageMap = new HashMap<>();

    public <V extends Event> void addStage(Player player, Stage<V, ?> stage) {
        this.stageMap.put(player.getUniqueId(), stage);
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(stage.chatMessage()));
    }

    public void createChatMessageStage(InventoryClickEvent event, BiConsumer<Player, String> consumer) {
        final Player player = (Player) event.getWhoClicked();
        this.addStage(player, (ChatStage) (chatEvent, message) -> {
            if (message.split(" ", 2).length > 1) {
                chatEvent.getPlayer().sendMessage(Component.text("Please type a correct name").color(NamedTextColor.RED));
                return StageResult.CANCELLED;
            }

            consumer.accept(player, message);
            return StageResult.DONE;
        });
    }

    @SuppressWarnings("unchecked")
    protected <V extends Event, K> void executeStage(UUID uuid, V event) {
        Stage<?, ?> currentStage = this.stageMap.get(uuid);
        if (currentStage == null) return;
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

