package kts.dev.ktsbk.minecraft.mixin;

import kts.dev.ktsbk.minecraft.controller.BoxController;
import kts.dev.ktsbk.minecraft.controller.BoxScreenProvider;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public class GenericContainerScreenMixin {

    @Inject(at=@At("RETURN"), method = "<init>")
    private void ktsbk$init(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        if(
                MinecraftClient.getInstance().player != null &&
                MinecraftClient.getInstance().crosshairTarget != null &&
                MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.BLOCK
        ) {
            BlockHitResult blockHit = (BlockHitResult) MinecraftClient.getInstance().crosshairTarget;
            //MinecraftClient.getInstance().execute(() -> BoxController.INSTANCE.onOpen((GenericContainerScreen) (Object) this, blockHit.getBlockPos()));
            MinecraftClient.getInstance().execute(() -> BoxController.INSTANCE.onOpen((GenericContainerScreen) (Object) this, blockHit.getBlockPos()));
            //BoxScreenProvider.onOpen((GenericContainerScreen) (Object) this, blockHit.getBlockPos());
        }

    }

    @Inject(at=@At(value = "RETURN"), method = "render")
    public void ktsbk$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        BoxController.INSTANCE.boxInfoRender(context, mouseX, mouseY, delta);
    }
}
