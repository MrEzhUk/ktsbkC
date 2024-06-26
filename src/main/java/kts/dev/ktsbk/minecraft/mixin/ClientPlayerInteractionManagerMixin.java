package kts.dev.ktsbk.minecraft.mixin;

import kts.dev.ktsbk.minecraft.controller.BoxController;
import kts.dev.ktsbk.minecraft.renderer.KtsBkBlockOutline;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerInteractionManager.class, priority = 800)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult);

    @Inject(method = "interactBlock", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"), cancellable = true)
    private void ktsbk$interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState bs = null;
        if(this.client.world != null) {
            bs = this.client.world.getBlockState(pos);
        }
        if(
                bs != null && bs.getBlock() instanceof BarrelBlock &&
                player.getMainHandStack().getItem() == Items.RED_DYE
        ) {
            KtsBkRenderer.INSTANCE.lastClicked = new KtsBkBlockOutline(pos, 1f, 0.75f, 0, 0.8f);
            cir.setReturnValue(ActionResult.FAIL);
            return;
        }
        MutableObject<ActionResult> mutableObject = new MutableObject<>();
        this.sendSequencedPacket(this.client.world, (sequence) -> {
            mutableObject.setValue(this.interactBlockInternal(player, hand, hitResult));
            return new PlayerInteractBlockC2SPacket(hand, hitResult, sequence);
        });
        cir.setReturnValue(mutableObject.getValue());
    }

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    void ktsbk$clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if(BoxController.INSTANCE.clickSlot(syncId, slotId, button, actionType, player)) {
            ci.cancel();
        }
    }

}
