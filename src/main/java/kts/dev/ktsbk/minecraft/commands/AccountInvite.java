package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.AccountIdArgumentType;
import kts.dev.ktsbk.minecraft.arguments.UserIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AccountInvite implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("invite")
                .then(ClientCommandManager.argument("account", new AccountIdArgumentType())
                        .then(ClientCommandManager.argument("user", new UserIdArgumentType("user")).executes(ctx -> {
                            KtsBkServiceC2S s = KtsBkApiProvider.INSTANCE.getCacheService();
                            KbErr e = s.invite(KtsBkApiProvider.INSTANCE.auth(), AccountIdArgumentType.getId(ctx, "account"), UserIdArgumentType.getId(ctx, "user"));
                            ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
                            return 1;
                        })));
    }
}
