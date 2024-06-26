package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface KtsBkCommand {
    LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral();
    default LiteralCommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("k").then(getCommandLiteral()));
        return dispatcher.register(ClientCommandManager.literal("ktsbk").then(getCommandLiteral()));
    }
}
