package net.eliotex.rhodium.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.PlayerWorldManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerWorldManager.class)
public abstract class PlayerWorldManagerMixin {
    @Final @Shadow private net.minecraft.server.world.ServerWorld world;
    @ModifyVariable(method = "applyViewDistance", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int forceViewDistance(int original) {
        PlayerManager pm = world.getServer().getPlayerManager();
        return ((PlayerManagerAccessor) pm).getViewDistanceRaw();
    }
}