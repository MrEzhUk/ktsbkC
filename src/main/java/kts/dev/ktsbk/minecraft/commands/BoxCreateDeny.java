package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.common.utils.KbErr;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class BoxCreateDeny implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("deny").then(ClientCommandManager.literal("box").executes(ctx -> {
            BoxCreate.box.setId(-1);
            ctx.getSource().getPlayer().sendMessage(Text.translatable(KbErr.SUCCESS.translatable()));
            return 1;
        }));
    }
}
