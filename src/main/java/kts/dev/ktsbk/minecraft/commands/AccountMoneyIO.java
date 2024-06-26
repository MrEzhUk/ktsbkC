package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.AccountIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AccountMoneyIO implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("money_io")
                .then(ClientCommandManager.argument("account", new AccountIdArgumentType())
                        .then(ClientCommandManager.argument("count", LongArgumentType.longArg()).executes(ctx -> {
                            KtsBkServiceC2S serviceC2S = KtsBkApiProvider.INSTANCE.getCacheService();
                            KbErr e = serviceC2S.io_money(null, AccountIdArgumentType.getId(ctx, "account"), LongArgumentType.getLong(ctx, "count"));
                            ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
                            return 1;
                        }))
                );
    }
}
