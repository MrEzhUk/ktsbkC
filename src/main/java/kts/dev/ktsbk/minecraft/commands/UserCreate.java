package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class UserCreate implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("create").then(ClientCommandManager.literal("user")
                .then(ClientCommandManager.argument("nickname", StringArgumentType.word())
                .then(ClientCommandManager.argument("disid", StringArgumentType.word()).executes(ctx -> {
                    KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
                    KbErr r = service.createUser(
                            KtsBkApiProvider.INSTANCE.auth(),
                            StringArgumentType.getString(ctx, "nickname"),
                            StringArgumentType.getString(ctx, "disid")
                    );
                    ctx.getSource().getPlayer().sendMessage(Text.translatable(r.translatable()));
                    return 1;
                })
        )));
    }
}
