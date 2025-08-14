package com.iafenvoy.rollable.config.entry.dialog;

import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.render.screen.WidgetBuilderManager;
import com.iafenvoy.jupiter.render.screen.dialog.Dialog;
import com.iafenvoy.jupiter.render.widget.WidgetBuilder;
import com.iafenvoy.rollable.Rollable;
import com.iafenvoy.rollable.config.Sensitivity;
import com.iafenvoy.rollable.config.entry.SensitivityEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SensitivityDialog extends Dialog<Sensitivity> {
    public SensitivityDialog(Screen parent, SensitivityEntry entry) {
        super(parent, entry);
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.of("<"), (button) -> this.close()).dimensions(10, 5, 20, 15).build());
        Sensitivity value = this.entry.getValue(), defaultValue = this.entry.getDefaultValue();
        this.createEntry("pitch", value.pitch, defaultValue.pitch, 0);
        this.createEntry("yaw", value.yaw, defaultValue.yaw, 1);
        this.createEntry("roll", value.roll, defaultValue.roll, 2);
    }

    private void createEntry(String key, double value, double defaultValue, int index) {
        String t = "config.%s.sensitivity.%s".formatted(Rollable.MOD_ID, key);
        DoubleEntry entry = new DoubleEntry(t, defaultValue);
        entry.setValue(value);
        WidgetBuilder<Double> builder = WidgetBuilderManager.get(entry);
        builder.addDialogElements(this::addDrawableChild, I18n.translate(t) + ":", 80, 30 + index * 25, Math.max(10, this.width - 110), 20);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, partialTicks);
    }
}
