package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.CurrencyIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AccountCreate implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("create").then(ClientCommandManager.literal("account").then(
                ClientCommandManager.argument("name", StringArgumentType.word())
                        .then(ClientCommandManager.argument("currency", new CurrencyIdArgumentType("currency")).executes(ctx -> {
                            KtsBkServiceC2S s = KtsBkApiProvider.INSTANCE.getCacheService();
                            String name = StringArgumentType.getString(ctx, "name");
                            long currency_id = CurrencyIdArgumentType.getId(ctx, "currency");
                            KbErr e = s.createAccount(KtsBkApiProvider.INSTANCE.auth(), name, currency_id);
                            ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
                            return 1;
                        }))));
    }
}
