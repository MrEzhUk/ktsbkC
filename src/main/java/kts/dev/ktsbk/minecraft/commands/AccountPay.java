package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.AccountIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AccountPay implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("pay").then(ClientCommandManager.argument("account_from", new AccountIdArgumentType())
                        .then(ClientCommandManager.argument("account_to", new AccountIdArgumentType())
                        .then(ClientCommandManager.argument("count", LongArgumentType.longArg())
                        .then(ClientCommandManager.argument("msg", StringArgumentType.greedyString()).executes(ctx -> {
                            KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
                            KbErr e = service.pay(
                                    KtsBkApiProvider.INSTANCE.auth(),
                                    AccountIdArgumentType.getId(ctx, "account_from"),
                                    AccountIdArgumentType.getId(ctx, "account_to"),
                                    LongArgumentType.getLong(ctx, "count"),
                                    StringArgumentType.getString(ctx, "msg")
                            );
                            ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
                            return 1;
                        }))))
        );
    }
}
