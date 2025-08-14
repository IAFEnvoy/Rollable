package com.iafenvoy.rollable.config.entry;

import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.iafenvoy.jupiter.interfaces.ITextFieldConfig;
import com.iafenvoy.jupiter.render.widget.TextFieldWithErrorWidget;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ExtendedTextFieldWidgetBuilder<T> extends WidgetBuilder<T> {
    private final ITextFieldConfig textFieldConfig;
    private @Nullable TextFieldWithErrorWidget widget;

    public ExtendedTextFieldWidgetBuilder(IConfigEntry<T> config) {
        super(config);
        if (config instanceof ITextFieldConfig t) this.textFieldConfig = t;
        else throw new IllegalArgumentException("ExtendedTextFieldWidgetBuilder only accept ITextFieldConfig");
    }

    @Override
    public void addCustomElements(Consumer<ClickableWidget> appender, int x, int y, int width, int height) {
        this.widget = new TextFieldWithErrorWidget(CLIENT.get().textRenderer, x, y, width, height);
        this.widget.setMaxLength(512);
        this.widget.setText(this.textFieldConfig.valueAsString());
        this.widget.setChangedListener((s) -> {
            try {
                this.textFieldConfig.setValueFromString(s);
                this.canSave = true;
                this.widget.setHasError(false);
            } catch (Exception var3) {
                this.canSave = false;
                this.widget.setHasError(true);
                this.setCanReset(true);
            }

        });
        appender.accept(this.widget);
    }

    @Override
    public void updateCustom(boolean visible, int y) {
        if (this.widget != null) {
            this.widget.visible = visible;
            this.widget.setY(y);
        }
    }

    @Override
    public void refresh() {
        if (this.widget != null) {
            this.widget.setText(this.textFieldConfig.valueAsString());
        }
    }
}
