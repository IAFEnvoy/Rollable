package com.iafenvoy.rollable;

import com.iafenvoy.rollable.api.RollEntity;
import com.iafenvoy.rollable.api.RollMouse;
import com.iafenvoy.rollable.config.RollableClientConfig;
import com.iafenvoy.rollable.impl.key.InputContextImpl;
import com.iafenvoy.rollable.render.HorizonLineWidget;
import com.iafenvoy.rollable.render.MomentumCrosshairWidget;
import com.iafenvoy.rollable.util.StarFoxUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Vector2d;

public class EventCallbacksClient {
    public static void clientTick(MinecraftClient client) {
        InputContextImpl.getContexts().forEach(InputContextImpl::tick);

        if (!DoABarrelRollClient.isFallFlying()) {
            DoABarrelRollClient.clearValues();
        }

        ModKeybindings.clientTick(client);

        StarFoxUtil.clientTick(client);
    }

    public static void onRenderCrosshair(DrawContext context, float tickDelta, int scaledWidth, int scaledHeight) {
        if (!DoABarrelRollClient.isFallFlying()) return;

        MatrixStack matrices = context.getMatrices();
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        RollEntity rollEntity = ((RollEntity) entity);
        if (entity != null) {
            if (RollableClientConfig.INSTANCE.generals.hudShowHorizon.getValue()) {
                HorizonLineWidget.render(matrices, scaledWidth, scaledHeight,
                        rollEntity.doABarrelRoll$getRoll(tickDelta), entity.getPitch(tickDelta));
            }

            if (RollableClientConfig.INSTANCE.generals.momentumBasedMouse.getValue() && RollableClientConfig.INSTANCE.generals.showMomentumWidget.getValue()) {
                RollMouse rollMouse = (RollMouse) MinecraftClient.getInstance().mouse;

                MomentumCrosshairWidget.render(matrices, scaledWidth, scaledHeight, new Vector2d(rollMouse.doABarrelRoll$getMouseTurnVec()));
            }
        }
    }
}
