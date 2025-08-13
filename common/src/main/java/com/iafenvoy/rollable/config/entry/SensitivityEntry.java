package com.iafenvoy.rollable.config.entry;

import com.iafenvoy.jupiter.config.entry.BaseEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.SingleConfigType;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.iafenvoy.rollable.config.Sensitivity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SensitivityEntry extends BaseEntry<Sensitivity> {
    public static final ConfigType<Sensitivity> TYPE = new SingleConfigType<>();

    public SensitivityEntry(String nameKey, double pitch, double yaw, double roll) {
        super(nameKey, new Sensitivity(pitch, yaw, roll));
    }

    public SensitivityEntry(String nameKey) {
        super(nameKey, new Sensitivity());
    }

    public SensitivityEntry(String nameKey, Sensitivity defaultValue) {
        super(nameKey, defaultValue);
    }

    @Override
    public ConfigType<Sensitivity> getType() {
        return TYPE;
    }

    @Override
    public IConfigEntry<Sensitivity> newInstance() {
        return new SensitivityEntry(this.nameKey, this.copyDefaultData()).visible(this.visible).json(this.jsonKey);
    }

    @Override
    public Codec<Sensitivity> getCodec() {
        return RecordCodecBuilder.create(i -> i.group(
                Codec.DOUBLE.fieldOf("pitch").forGetter(s -> s.pitch),
                Codec.DOUBLE.fieldOf("yaw").forGetter(s -> s.yaw),
                Codec.DOUBLE.fieldOf("roll").forGetter(s -> s.roll)
        ).apply(i, Sensitivity::new));
    }

    @Override
    protected Sensitivity copyDefaultData() {
        return new Sensitivity(this.defaultValue.pitch, this.defaultValue.yaw, this.defaultValue.roll);
    }
}
