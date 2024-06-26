package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsbkConfig;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.ServerIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ServerSelect implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("select").then(
                ClientCommandManager.literal("server")
                .then(ClientCommandManager.argument("server", new ServerIdArgumentType()).executes(ctx -> {
                    long id = ServerIdArgumentType.getId(ctx, "server");
                    KtsbkConfig.config.setServerSelected(id);
                    KtsbkConfig.config.save();
                    ctx.getSource().getPlayer().sendMessage(Text.translatable(KbErr.SUCCESS.translatable()));
                    return 1;
                }))
        );
    }
}
