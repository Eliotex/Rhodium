package net.eliotex.rhodium.mixin;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.apache.logging.log4j.Logger;
@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Unique private long lastHeadTime = -1L;
    @Unique private double recentTPS = 20.0;
    @Shadow private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method = "setupWorld()V", at = @At("HEAD"))     //calculate current TPS
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

    @Redirect( method = "setupWorld()V", at = @At( value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;setViewDistance(I)V" ) )
    private void redirectSetViewDistance(PlayerManager pm, int originalValue) {
        int current = pm.getViewDistance();
        if(current < 12 && originalValue > 11) {
            pm.setViewDistance(12);                 //Instantly go to 12rd, bc the spawn chunks are loaded anyway
            LOGGER.info("Changing view distance to {}, from {}", current, 12);
            return;
        }
        if (current > originalValue || originalValue < 13) {
            pm.setViewDistance(originalValue);      //if rd decrease or player wants to increase to a rd smaller than 13
            LOGGER.info("Changing view distance to {}, from {}", current, originalValue);
            return;
        }
        if (current < originalValue && recentTPS > 14 && recentTPS < 21) {
            pm.setViewDistance(current + 1);        //increase by 1, if there is no lag and no ticks get skipped
            LOGGER.info("Changing view distance to {}, from {}", current, current + 1);
        }
    }
    
    //avoid spam in log
    @Redirect(method = "setupWorld()V", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private void redirectViewDistanceLogger(org.apache.logging.log4j.Logger logger, String msg, Object[] args) {
        if (!"Changing view distance to {}, from {}".equals(msg)) {
            logger.info(msg, args);
        }
    }
}
