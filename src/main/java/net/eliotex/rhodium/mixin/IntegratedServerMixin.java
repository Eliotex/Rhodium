package net.eliotex.rhodium.mixin;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.eliotex.rhodium.config.ConfigManager;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Unique private long lastHeadTime = -1L;
    @Unique private double recentTPS = 20.0;
    @Unique private int newRD = -1;
    @Shadow private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method = "setupWorld()V", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        long now = System.currentTimeMillis();
        if (lastHeadTime != -1L) {
            long diff = now - lastHeadTime;
            double seconds = diff / 1.0E3;
            if (seconds > 0) {
                recentTPS = 1.0 / seconds;
            }
        }
        lastHeadTime = now;
    }

    @Redirect(method = "setupWorld()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;setViewDistance(I)V"))
    private void redirectSetViewDistance(PlayerManager pm, int originalValue) {
        int current = pm.getViewDistance();
        int minTPSforIncreaseLocal = ConfigManager.minTPSforIncrease;
        if (current < 12 && originalValue > 11) {
            newRD = 12;
        } else if (current > originalValue || originalValue < 13) {
            newRD = originalValue;
        } else if (current < originalValue && recentTPS > minTPSforIncreaseLocal && recentTPS < 21) {
            newRD = current + 1;
        }
        if (newRD != current) {
            pm.setViewDistance(newRD);
            LOGGER.info("Changing view distance to {}, from {}", newRD, current);
        }
    }

    @Redirect(method = "setupWorld()V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private void redirectViewDistanceLogger(org.apache.logging.log4j.Logger logger, String msg, Object[] args) {
        if (!"Changing view distance to {}, from {}".equals(msg)) {
            logger.info(msg, args);
        }
    }
}
