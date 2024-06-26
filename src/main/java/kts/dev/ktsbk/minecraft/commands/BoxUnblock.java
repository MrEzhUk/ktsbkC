package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.BoxIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class BoxUnblock implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("unblock").then(
                ClientCommandManager.literal("box").then(ClientCommandManager.argument("box", new BoxIdArgumentType()).executes(ctx -> {
                    long id = BoxIdArgumentType.getId(ctx, "box");
                    KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
                    KbErr e = service.unblockBox(KtsBkApiProvider.INSTANCE.auth(), id);
                    ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
                    return 1;
                }))
        );
    }
}
