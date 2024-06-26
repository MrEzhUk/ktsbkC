package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.AccountIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AccountDelete implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("delete").then(ClientCommandManager.literal("account").then(ClientCommandManager.argument("account", new AccountIdArgumentType()).executes(ctx -> {
            var service = KtsBkApiProvider.INSTANCE.getCacheService();
            KbErr e = service.disableAccount(null, AccountIdArgumentType.getId(ctx, "account"));
            ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
            return 1;
        })));
    }
}
