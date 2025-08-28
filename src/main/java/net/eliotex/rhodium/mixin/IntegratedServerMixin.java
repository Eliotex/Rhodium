package net.eliotex.rhodium.mixin;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.eliotex.rhodium.GradualViewDistance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Redirect( method = "setupWorld()V", at = @At( value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;setViewDistance(I)V" ) )
    private void redirectSetViewDistance(PlayerManager pm, int target) {
        int current = pm.getViewDistance();
        if (target == current) {
            return;
        }
        ((GradualViewDistance) pm).setGradualTargetDistance(target);
        if (target < current) {
            pm.setViewDistance(target);
        }
    }
/*
    @Redirect(method = "setupWorld()V", at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private void redirectViewDistanceLogger(org.apache.logging.log4j.Logger logger, String msg, Object[] args) {
        if (!"Changing view distance to {}, from {}".equals(msg)) {
            logger.info(msg, args);
        }
    }
 */
}