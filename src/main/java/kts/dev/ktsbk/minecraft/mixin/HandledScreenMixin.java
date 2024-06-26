package kts.dev.ktsbk.minecraft.mixin;

import kts.dev.ktsbk.minecraft.controller.BoxController;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HandledScreen.class, priority = 800)
public class HandledScreenMixin {
    @Inject(at = @At("HEAD"), method = "close")
    void ktsbk$close(CallbackInfo ci) {
        BoxController.INSTANCE.onClose();
        //BoxScreenProvider.getInstance().close();
    }

    @Inject(at = @At("RETURN"), method = "init")
    void ktsbk$init(CallbackInfo ci) {
        BoxController.INSTANCE.onScreenInit();
        //BoxScreenProvider.getInstance().init();
    }
}
