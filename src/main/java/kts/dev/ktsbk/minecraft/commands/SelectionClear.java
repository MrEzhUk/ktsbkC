package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class SelectionClear implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("clear").executes(ctx -> {
            KtsBkRenderer.INSTANCE.selected.clear();
            return 1;
        });
    }
}
