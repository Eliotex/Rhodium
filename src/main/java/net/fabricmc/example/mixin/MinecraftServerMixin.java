package net.fabricmc.example.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.example.GradualViewDistance;

@Mixin(value = MinecraftServer.class, priority = 2000)
public abstract class MinecraftServerMixin {
    @Shadow public abstract PlayerManager getPlayerManager();
    @Shadow public long[] lastTickLengths;

    @Inject(method = "tick", at = @At("TAIL"))
    private void stepViewDistanceGradually(CallbackInfo ci) {
        PlayerManager pm = this.getPlayerManager();
        GradualViewDistance g = (GradualViewDistance) (Object) pm;
        if (!g.isGradualActive()) return;

        int needed = 3;
        int checked = 0;
        for (int i = this.lastTickLengths.length - 1; i >= 0 && checked < needed; i--) {
            long v = this.lastTickLengths[i];
            if (v > 0L) {
                double ms = v * 1.0E-6;
                if (ms > 100.0) {
                    return;
                }
                checked++;
            }
        }

        if (checked < needed) return;
        int current = ((PlayerManagerAccessor) (Object) pm).getViewDistanceRaw();
        int target  = g.getGradualTargetDistance();
        if (target > current) {
            pm.setViewDistance(current + 1);
            if (current + 1 >= target) {
                g.clearGradual();
            }
        } else {
            g.clearGradual();
        }
    }
}



