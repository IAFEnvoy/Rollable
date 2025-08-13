package com.iafenvoy.rollable.fabric.compat.modmenu;

import com.iafenvoy.jupiter.render.screen.ClientConfigScreen;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ClientConfigScreen(parent, RollableClientConfig.INSTANCE);
    }
}
