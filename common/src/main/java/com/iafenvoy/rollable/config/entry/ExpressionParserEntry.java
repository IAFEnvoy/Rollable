package com.iafenvoy.rollable.config.entry;

import com.iafenvoy.jupiter.config.entry.BaseEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.SingleConfigType;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.iafenvoy.jupiter.interfaces.ITextFieldConfig;
import com.iafenvoy.rollable.math.ExpressionParser;
import com.iafenvoy.rollable.math.Parser;
import com.mojang.serialization.Codec;

public class ExpressionParserEntry extends BaseEntry<ExpressionParser> implements ITextFieldConfig {
    public static final ConfigType<ExpressionParser> TYPE = new SingleConfigType<>();

    public ExpressionParserEntry(String nameKey, String defaultValue) {
        this(nameKey, new ExpressionParser(defaultValue));
    }

    public ExpressionParserEntry(String nameKey, ExpressionParser defaultValue) {
        super(nameKey, defaultValue);
    }

    @Override
    public ConfigType<ExpressionParser> getType() {
        return TYPE;
    }

    @Override
    public IConfigEntry<ExpressionParser> newInstance() {
        return new ExpressionParserEntry(this.nameKey, this.defaultValue).visible(this.visible).json(this.jsonKey);
    }

    @Override
    public Codec<ExpressionParser> getCodec() {
        return Codec.STRING.xmap(ExpressionParser::new, Parser::getString);
    }

    @Override
    public String valueAsString() {
        return this.value.getString();
    }

    @Override
    public void setValueFromString(String s) {
        this.value = new ExpressionParser(s);
    }
}
