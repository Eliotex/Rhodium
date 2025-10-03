package net.eliotex.rhodium.mixin;

import net.eliotex.rhodium.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "initializeGame()V", at = @At("TAIL"))
    private void onInitializeGameTail(CallbackInfo ci) {
        ConfigManager.loadFromOptions();
    }
}
