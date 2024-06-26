package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.minecraft.arguments.BoxIdArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;

public class BoxDelete implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("delete")
                .then(ClientCommandManager.literal("box")
                        .then(ClientCommandManager.argument("boxid", new BoxIdArgumentType()).executes(ctx -> {
                            try {
                                KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
                                KbErr e = service.deleteBox(null, BoxIdArgumentType.getId(ctx, "boxid"));

                                ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
                            } catch (UndeclaredThrowableException | WebsocketNotConnectedException e) {
                                ctx.getSource().getPlayer().sendMessage(Text.translatable(KbErr.CONNECTION_ERROR.translatable()));
                            }
                            return 1;
                        })));
    }
}
