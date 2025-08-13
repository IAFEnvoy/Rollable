package com.iafenvoy.rollable.forge;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import com.iafenvoy.rollable.DoABarrelRoll;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ModPermissions {
    public static final PermissionDynamicContextKey<Integer> DEFAULT_PERMISSION_LEVEL_CONTEXT = new PermissionDynamicContextKey<>(
            Integer.class, "default_permission_level", Objects::toString);

    public static final PermissionNode<Boolean> IGNORE_CONFIG_NODE = new PermissionNode<>(
            DoABarrelRoll.MOD_ID, "ignore_config", PermissionTypes.BOOLEAN,
            ModPermissions::defaultResolve, DEFAULT_PERMISSION_LEVEL_CONTEXT);
    public static final PermissionNode<Boolean> CONFIGURE_NODE = new PermissionNode<>(
            DoABarrelRoll.MOD_ID, "configure", PermissionTypes.BOOLEAN,
            ModPermissions::defaultResolve, DEFAULT_PERMISSION_LEVEL_CONTEXT);

    public static final List<PermissionNode<Boolean>> NODES = List.of(IGNORE_CONFIG_NODE, CONFIGURE_NODE);

    public static boolean resolve(ServerPlayerEntity player, String permission, int defaultPermissionLevel) {
        for (var node : NODES) {
            if (node.getNodeName().equals(permission)) {
                PermissionAPI.getPermission(player, node, DEFAULT_PERMISSION_LEVEL_CONTEXT.createContext(defaultPermissionLevel));
            }
        }
        return player.hasPermissionLevel(defaultPermissionLevel);
    }

    private static boolean defaultResolve(@Nullable ServerPlayerEntity player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        if (player != null) {
            for (var key : context) {
                if (key.getDynamic() == DEFAULT_PERMISSION_LEVEL_CONTEXT) {
                    return player.hasPermissionLevel((int) key.getValue());
                }
            }
        }
        return false;
    }

//    private static PermissionNode.PermissionResolver<Boolean> defaultResolve(int defaultPermissionLevel) {
//        return (player, playerUUID, context) -> {
//            if (player != null) {
//                return player.hasPermissionLevel(defaultPermissionLevel);
//            }
//            return false;
//        };
//    }
}
