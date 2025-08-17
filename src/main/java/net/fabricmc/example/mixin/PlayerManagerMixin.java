package net.fabricmc.example.mixin;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.example.GradualViewDistance;
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements GradualViewDistance {
    @Shadow private int viewDistance;
    private int gradualTargetDistance = -1;
    private boolean gradualActive = false;
    @Inject(method = "setViewDistance", at = @At("HEAD"), cancellable = true)
    private void onSetViewDistance(int newDistance, CallbackInfo ci) {
        int current = this.viewDistance;
        if (current == 0) return;
        if (newDistance > current + 1) {
            this.gradualTargetDistance = newDistance;
            this.gradualActive = true;
            ci.cancel();
            return;
        }
        if (this.gradualActive && newDistance > current + 1) {
            ci.cancel();
            return;
        }
        if (this.gradualActive && newDistance >= this.gradualTargetDistance) {
            clearGradual();
        }
    }
    @Override public int getGradualTargetDistance() { return this.gradualTargetDistance; }
    @Override public boolean isGradualActive() { return this.gradualActive; }
    @Override public void clearGradual() { this.gradualActive = false; this.gradualTargetDistance = -1; }
    @Override public void setGradualTargetDistance(int target) { this.gradualTargetDistance = target; }
    @Override public void setGradualActive(boolean active) { this.gradualActive = active; }
}