package com.iafenvoy.rollable.config;

import com.iafenvoy.jupiter.interfaces.IConfigEnumEntry;
import com.iafenvoy.rollable.Rollable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum ActivationBehaviour implements IConfigEnumEntry {
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    @Override
    public Text getDisplayText() {
        return Text.translatable("config.%s.controls.activation_behaviour.%s".formatted(Rollable.MOD_ID, this.name().toLowerCase()));
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public @NotNull IConfigEnumEntry getByName(String s) {
        return valueOf(s);
    }

    @Override
    public IConfigEnumEntry cycle(boolean b) {
        ActivationBehaviour[] types = values();
        return types[(this.ordinal() + (b ? 1 : -1)) % types.length];
    }
}
