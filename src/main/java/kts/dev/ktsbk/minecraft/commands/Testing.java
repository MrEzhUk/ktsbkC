package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.minecraft.screens.RollbackScreen;
import kts.dev.ktsbk.minecraft.screens.SearchScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class Testing implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("testing").executes(ctx -> {
            //MinecraftClient.getInstance().player.networkHandler.sendChatCommand("gamemode survival");
            /*
            Inventory inv = new SimpleInventory(128);
            for(int i = 0; i < 128; i++) {
                inv.setStack(i, new ItemStack(Items.BARRIER,i % 65));
            }
            MinecraftClient.getInstance().setScreen(new SearchScreen(Text.literal("Testing"), null, inv));
            MinecraftClient.getInstance().run();
            */
            MinecraftClient.getInstance().setScreen(new RollbackScreen(Text.literal("testing")));
            MinecraftClient.getInstance().run();
            return 1;
        });
    }
}
