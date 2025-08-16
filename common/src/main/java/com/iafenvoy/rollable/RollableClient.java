package com.iafenvoy.rollable;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.render.screen.WidgetBuilderManager;
import com.iafenvoy.rollable.compat.FlightAssistantIntegration;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.config.entry.ExpressionParserEntry;
import com.iafenvoy.rollable.config.entry.ExtendedTextFieldWidgetBuilder;
import com.iafenvoy.rollable.config.entry.RotateStateEntry;
import com.iafenvoy.rollable.config.entry.dialog.RotateStateWidgetBuilder;
import com.iafenvoy.rollable.event.ClientEvents;

public class RollableClient {
    public static boolean FLIGHT_ASSISTANT_LOADED = false;

    public static void init() {
        WidgetBuilderManager.register(ExpressionParserEntry.TYPE, ExtendedTextFieldWidgetBuilder::new);
        WidgetBuilderManager.register(RotateStateEntry.TYPE, config -> new RotateStateWidgetBuilder((RotateStateEntry) config));
        ConfigManager.getInstance().registerConfigHandler(RollableClientConfig.INSTANCE);

        ClientEvents.registerEvents();
        if (FLIGHT_ASSISTANT_LOADED) FlightAssistantIntegration.init();
    }
}
