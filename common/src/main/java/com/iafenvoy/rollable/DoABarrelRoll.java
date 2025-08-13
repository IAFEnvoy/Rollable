package com.iafenvoy.rollable;

import com.mojang.logging.LogUtils;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class DoABarrelRoll {
    public static final String MOD_ID = "rollable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void init() {
    }
}
