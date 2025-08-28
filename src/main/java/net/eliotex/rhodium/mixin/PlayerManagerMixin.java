package net.eliotex.rhodium.mixin;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.eliotex.rhodium.GradualViewDistance;
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements GradualViewDistance {
    @Shadow private int viewDistance;
    @Unique private int gradualTargetDistance = -1;
    @Inject(method = "setViewDistance", at = @At("HEAD"), cancellable = true)
    private void onSetViewDistance(int newDistance, CallbackInfo ci) {
        int current = this.viewDistance;
        if (newDistance > current + 1) {
            if (current < 12) {
                return;
            }
            this.gradualTargetDistance = newDistance;
            ci.cancel();
        }
    }
    @Override public int getGradualTargetDistance() { return this.gradualTargetDistance; }
    @Override public void setGradualTargetDistance(int target) { this.gradualTargetDistance = target; }
}