package com.iafenvoy.rollable.config;

import com.iafenvoy.jupiter.interfaces.IConfigEnumEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum ActivationBehaviour implements IConfigEnumEntry {
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    @Override
    public Text getDisplayText() {
        return Text.translatable("config.do_a_barrel_roll.controls.activation_behaviour." + this.name().toLowerCase());
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
