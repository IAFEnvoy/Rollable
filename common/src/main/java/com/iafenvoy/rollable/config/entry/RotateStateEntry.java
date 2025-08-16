package com.iafenvoy.rollable.config.entry;

import com.iafenvoy.jupiter.config.entry.BaseEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.SingleConfigType;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.iafenvoy.rollable.flight.RotateState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RotateStateEntry extends BaseEntry<RotateState> {
    public static final ConfigType<RotateState> TYPE = new SingleConfigType<>();

    public RotateStateEntry(String nameKey, double pitch, double yaw, double roll) {
        super(nameKey, new RotateState(pitch, yaw, roll));
    }

    public RotateStateEntry(String nameKey, RotateState defaultValue) {
        super(nameKey, defaultValue);
    }

    @Override
    public ConfigType<RotateState> getType() {
        return TYPE;
    }

    @Override
    public IConfigEntry<RotateState> newInstance() {
        return new RotateStateEntry(this.nameKey, this.copyDefaultData()).visible(this.visible).json(this.jsonKey);
    }

    @Override
    public Codec<RotateState> getCodec() {
        return RecordCodecBuilder.create(i -> i.group(
                Codec.DOUBLE.fieldOf("pitch").forGetter(RotateState::pitch),
                Codec.DOUBLE.fieldOf("yaw").forGetter(RotateState::yaw),
                Codec.DOUBLE.fieldOf("roll").forGetter(RotateState::roll)
        ).apply(i, RotateState::new));
    }

    @Override
    protected RotateState copyDefaultData() {
        return new RotateState(this.defaultValue.pitch(), this.defaultValue.yaw(), this.defaultValue.roll());
    }
}
