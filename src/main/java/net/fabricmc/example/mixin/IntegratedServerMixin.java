package net.fabricmc.example.mixin;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.fabricmc.example.GradualViewDistance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Redirect( method = "setupWorld()V", at = @At( value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;setViewDistance(I)V" ) )
    private void redirectSetViewDistance(PlayerManager pm, int target) {
        int current = pm.getViewDistance();
        if (target > current + 1) {
            ((GradualViewDistance)(Object) pm).setGradualTargetDistance(target);
        } else {
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