package com.iafenvoy.rollable.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastUtil {
    public static void toasty(String key) {
        MinecraftClient.getInstance().getToastManager().add(SystemToast.create(
                MinecraftClient.getInstance(),
                SystemToast.Type.TUTORIAL_HINT,
                Text.translatable("toast.do_a_barrel_roll"),
                Text.translatable("toast.do_a_barrel_roll." + key)
        ));
    }
}
