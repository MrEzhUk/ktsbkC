package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.minecraft.arguments.UserIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class UserTokenReset implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("reset")
                .then(ClientCommandManager.literal("password")
                        .then(ClientCommandManager.argument("user", new UserIdArgumentType("user")).executes(ctx -> {
                            KtsBkServiceC2S serviceC2S = KtsBkApiProvider.INSTANCE.getCacheService();
                            serviceC2S.resetPassword(null, UserIdArgumentType.getId(ctx, "user"));
                            return 1;
                        }))
                );
    }
}
