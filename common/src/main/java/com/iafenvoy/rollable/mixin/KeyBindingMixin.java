package com.iafenvoy.rollable.mixin;

import com.iafenvoy.rollable.RollableKeybindings;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Unique
    private static KeyBinding rollable$getContextKeyBinding(InputUtil.Key key) {
        KeyBinding binding = RollableKeybindings.CONTEXT.getKeyBinding(key);
        if (binding != null)
            if (RollableKeybindings.CONTEXT.isActive()) return binding;
            else binding.setPressed(false);
        return null;
    }

    @WrapOperation(method = "onKeyPressed", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static Object rollable$applyKeybindContext(Map<InputUtil.Key, KeyBinding> map, Object key, Operation<KeyBinding> original) {
        KeyBinding binding = rollable$getContextKeyBinding((InputUtil.Key) key);
        if (binding != null) return binding;
        return original.call(map, key);
    }

    @WrapOperation(method = "setKeyPressed", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static Object rollable$applyKeybindContext2(Map<InputUtil.Key, KeyBinding> map, Object key, Operation<KeyBinding> original) {
        KeyBinding binding = rollable$getContextKeyBinding((InputUtil.Key) key);
        KeyBinding originalBinding = original.call(map, key);
        if (binding != null) {
            if (originalBinding != null)
                originalBinding.setPressed(false);
            return binding;
        }
        return originalBinding;
    }

    @Inject(method = "updateKeysByCode", at = @At("HEAD"), require = 0)
    private static void rollable$updateContextualKeys(CallbackInfo ci) {
        RollableKeybindings.CONTEXT.updateKeysByCode();
    }

    @WrapWithCondition(method = "updateKeysByCode", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static boolean rollable$skipAddingContextualKeys(Map<InputUtil.Key, KeyBinding> map, Object key, Object keyBinding) {
        return !RollableKeybindings.CONTEXT.getKeyBindings().contains((KeyBinding) keyBinding);
    }
}
