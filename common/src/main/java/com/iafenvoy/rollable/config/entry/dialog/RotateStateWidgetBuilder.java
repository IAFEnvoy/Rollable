package com.iafenvoy.rollable.config.entry.dialog;

import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.rollable.config.entry.RotateStateEntry;
import com.iafenvoy.rollable.flight.RotateState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RotateStateWidgetBuilder extends WidgetBuilder<RotateState> {
    protected final RotateStateEntry config;
    private @Nullable ButtonWidget button;

    public RotateStateWidgetBuilder(RotateStateEntry config) {
        super(config);
        this.config = config;
    }

    @Override
    public void addCustomElements(Consumer<ClickableWidget> appender, int x, int y, int width, int height) {
        MinecraftClient client = CLIENT.get();
        this.button = ButtonWidget.builder(Text.of(String.valueOf(this.config.getValue())), (button) -> client.setScreen(new RotateStateDialog(client.currentScreen, this.config))).dimensions(x, y, width, height).build();
        appender.accept(this.button);
    }

    @Override
    public void updateCustom(boolean visible, int y) {
        if (this.button != null) {
            this.button.visible = visible;
            this.button.setY(y);
        }
    }

    @Override
    public void refresh() {
        if (this.button != null) this.button.setMessage(Text.of(String.valueOf(this.config.getValue())));
    }
}
