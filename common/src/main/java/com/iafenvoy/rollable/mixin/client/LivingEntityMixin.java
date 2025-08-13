package com.iafenvoy.rollable.mixin.client;

import com.iafenvoy.rollable.DoABarrelRollClient;
import com.iafenvoy.rollable.ModKeybindings;
import com.iafenvoy.rollable.api.event.ThrustEvents;
import com.iafenvoy.rollable.config.RollableClientConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// High priority to ensure compat with mods like Elytra Aeronautics.
@Environment(EnvType.CLIENT)
@Mixin(value = LivingEntity.class, priority = 1200)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 6
            )
    )
    private Vec3d doABarrelRoll$wrapElytraVelocity(Vec3d original) {
        if (!(((LivingEntity) (Object) this) instanceof ClientPlayerEntity) || !RollableClientConfig.INSTANCE.thrust.enabled.getValue())
            return original;

        Vec3d rotation = this.getRotationVector();
        Vec3d velocity = this.getVelocity();

        if (RollableClientConfig.INSTANCE.thrust.particles.getValue()) {
            int particleDensity = (int) MathHelper.clamp(DoABarrelRollClient.throttle * 10, 0, 10);
            if (DoABarrelRollClient.throttle > 0.1 && this.getWorld().getTime() % (11 - particleDensity) == 0) {
                Vec3d pPos = this.getPos().add(velocity.multiply(0.5).negate());
                this.getWorld().addParticle(
                        ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        pPos.getX(), pPos.getY(), pPos.getZ(),
                        0, 0, 0
                );
            }
        }

        double throttleSign = ModKeybindings.THRUST_FORWARD.isPressed() ? 1 : ModKeybindings.THRUST_BACKWARD.isPressed() ? -1 : 0;
        throttleSign = ThrustEvents.modifyThrustInput(throttleSign);
        double maxSpeed = RollableClientConfig.INSTANCE.thrust.max.getValue();
        double speedIncrease = Math.max(maxSpeed - velocity.length(), 0) / maxSpeed * throttleSign;
        double acceleration = RollableClientConfig.INSTANCE.thrust.acceleration.getValue() * speedIncrease;

        return original.add(
                rotation.x * acceleration,
                rotation.y * acceleration,
                rotation.z * acceleration
        );
    }
}
