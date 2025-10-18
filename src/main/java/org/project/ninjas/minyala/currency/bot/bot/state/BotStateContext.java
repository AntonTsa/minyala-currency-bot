package org.project.ninjas.minyala.currency.bot.bot.state;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotStateContext {
    private final Map<BotState, BotStateHandler> handlers = new EnumMap<>(BotState.class);

    public BotStateContext(List<BotStateHandler> handlerList) {
        handlerList.forEach(handler -> handlers.put(handler.getHandledState(), handler));
    }

    public BotResponse process(BotState state, Update update) {
        BotStateHandler handler = handlers.get(state);
        if (handler == null) {
            throw new IllegalStateException("No handler found for state: " + state);
        }
        return handler.handle(update);
    }
}
