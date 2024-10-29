package fr.premier.regions.stage.impl;

import fr.premier.regions.stage.Stage;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Function;

public interface ChatStage extends Stage<AsyncChatEvent, String> {

    @Override
    default String chatMessage() {
        return "§aPlease type a name in the chat.";
    }

    @Override
    default Function<AsyncChatEvent, String> eventToObjectFunction() {
        return chatEvent -> LegacyComponentSerializer.legacySection().serialize(chatEvent.message());
    }

    @Override
    default Class<AsyncChatEvent> eventType() {
        return AsyncChatEvent.class;
    }
}
