package com.iafenvoy.rollable.fabric;

import com.iafenvoy.rollable.DoABarrelRoll;
import net.fabricmc.api.ModInitializer;

public class DoABarrelRollFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DoABarrelRoll.init();
    }
}
