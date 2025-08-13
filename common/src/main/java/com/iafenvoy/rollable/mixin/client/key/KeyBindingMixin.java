package com.iafenvoy.rollable.mixin.client.key;

import com.iafenvoy.rollable.api.key.InputContext;
import com.iafenvoy.rollable.impl.key.InputContextImpl;
import com.iafenvoy.rollable.util.key.ContextualKeyBinding;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements ContextualKeyBinding {
    @Unique
    private final ArrayList<InputContext> contexts = new ArrayList<>();

    @Override
    public List<InputContext> doABarrelRoll$getContexts() {
        return this.contexts;
    }

    @Override
    public void doABarrelRoll$addToContext(InputContext context) {
        this.contexts.add(context);
    }

    private static KeyBinding getContextKeyBinding(InputUtil.Key key) {
        for (InputContextImpl context : InputContextImpl.getContexts()) {
            KeyBinding binding = context.getKeyBinding(key);
            if (binding != null)
                if (context.isActive()) return binding;
                else binding.setPressed(false);
        }
        return null;
    }

    @WrapOperation(method = "onKeyPressed", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static Object doABarrelRoll$applyKeybindContext(Map<InputUtil.Key, KeyBinding> map, Object key, Operation<KeyBinding> original) {
        KeyBinding binding = getContextKeyBinding((InputUtil.Key) key);
        if (binding != null) return binding;
        return original.call(map, key);
    }

    @WrapOperation(method = "setKeyPressed", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static Object doABarrelRoll$applyKeybindContext2(Map<InputUtil.Key, KeyBinding> map, Object key, Operation<KeyBinding> original) {
        KeyBinding binding = getContextKeyBinding((InputUtil.Key) key);
        KeyBinding originalBinding = original.call(map, key);
        if (binding != null) {
            if (originalBinding != null)
                originalBinding.setPressed(false);
            return binding;
        }
        return originalBinding;
    }

    @Inject(method = "updateKeysByCode", at = @At("HEAD"), require = 0)
    private static void doABarrelRoll$updateContextualKeys(CallbackInfo ci) {
        for (InputContextImpl context : InputContextImpl.getContexts())
            context.updateKeysByCode();
    }

    @WrapWithCondition(method = "updateKeysByCode", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), require = 0)
    private static boolean doABarrelRoll$skipAddingContextualKeys(Map<InputUtil.Key, KeyBinding> map, Object key, Object keyBinding) {
        return !InputContextImpl.contextsContain((KeyBinding) keyBinding);
    }
}
