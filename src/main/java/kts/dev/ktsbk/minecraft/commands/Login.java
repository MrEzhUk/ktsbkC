package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.utils.AuthContext;
import kts.dev.ktsbk.common.utils.KbErr;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Login implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("login").then(
                ClientCommandManager.argument("token", StringArgumentType.string()).executes(ctx -> {
                    KtsBkApiProvider.INSTANCE.setToken(
                            StringArgumentType.getString(ctx, "token")
                    );
                    ctx.getSource().getPlayer().sendMessage(Text.translatable(KbErr.SUCCESS.translatable()));
                    return 1;
                })
        );
    }
}
