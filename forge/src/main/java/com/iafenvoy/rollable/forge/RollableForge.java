package com.iafenvoy.rollable.forge;

import com.iafenvoy.rollable.Rollable;
import com.iafenvoy.rollable.RollableClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Rollable.MOD_ID)
public class RollableForge {
    public RollableForge() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> RollableClient::init);
    }
}
