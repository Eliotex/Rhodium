package net.eliotex.rhodium.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.eliotex.rhodium.GradualViewDistance;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract PlayerManager getPlayerManager();
    @Unique private long lastHeadTime = -1L;
    @Unique private double recentTPS = 20.0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        long now = System.nanoTime();
        if (lastHeadTime != -1L) {
            long diff = now - lastHeadTime;
            double seconds = diff / 1.0E9;
            if (seconds > 0) {
                recentTPS = 1.0 / seconds;
            }
        }
        lastHeadTime = now;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void stepViewDistanceGradually(CallbackInfo ci) {
        if (recentTPS < 14 || recentTPS > 21) return;
        PlayerManager pm = this.getPlayerManager();
        GradualViewDistance g = (GradualViewDistance) (Object) pm;
        int current = ((PlayerManagerAccessor)(Object) pm).getViewDistanceRaw();
        int target = g.getGradualTargetDistance();
        if (target > current) {
            pm.setViewDistance(current + 1);
        }
    }
}