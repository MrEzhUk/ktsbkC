package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsbkConfig;
import kts.dev.ktsbk.minecraft.controller.BoxController;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AllSettings implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("setting").then(
                ClientCommandManager.literal("boxcontroller").executes(ctx -> {
                    BoxController.INSTANCE.disabled = !BoxController.INSTANCE.disabled;
                    ctx.getSource().getPlayer().sendMessage(Text.literal("BoxController ").append(BoxController.INSTANCE.disabled ? Text.translatable("ktsbk.enable") : Text.translatable("ktsbk.disable")));
                    return 1;
                })
        ).then(
                ClientCommandManager.literal("changerendermod").executes(ctx -> {
                    KtsbkConfig.config.setRenderMode(KtsbkConfig.config.getRenderMode() == 0 ? 1 : 0);
                    KtsbkConfig.config.save();
                    KtsBkRenderer.INSTANCE.mode = KtsBkRenderer.INSTANCE.getModeById(KtsbkConfig.config.getRenderMode());
                    ctx.getSource().getPlayer().sendMessage(Text.literal("Mode " + KtsBkRenderer.INSTANCE.mode.name()));
                    return 1;
                })
        );
    }
}
