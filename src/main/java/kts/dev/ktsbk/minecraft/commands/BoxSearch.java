package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.SearchProduct;
import kts.dev.ktsbk.common.utils.WithKbErr;
import kts.dev.ktsbk.minecraft.arguments.BoxSearchArgumentType;
import kts.dev.ktsbk.minecraft.screens.SearchScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

public class BoxSearch implements KtsBkCommand {
    private final SearchProduct product = new SearchProduct(0, 0, 0, 27);
    Inventory inv = new SimpleInventory(27);
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("search").executes(this::cleared_exec)
                .then(ClientCommandManager.argument("t1", new BoxSearchArgumentType(product, true)).executes(this::exec)
                .then(ClientCommandManager.argument("t2", new BoxSearchArgumentType(product)).executes(this::exec)
                .then(ClientCommandManager.argument("t3", new BoxSearchArgumentType(product)).executes(this::exec)
                .then(ClientCommandManager.argument("t4", new BoxSearchArgumentType(product)).executes(this::exec)
                .then(ClientCommandManager.argument("t5", new BoxSearchArgumentType(product)).executes(this::exec)
                .then(ClientCommandManager.argument("t6", new BoxSearchArgumentType(product)).executes(this::exec)
        ))))));
    }

    int cleared_exec(CommandContext<FabricClientCommandSource> ctx) {
        product.clear();
        return this.exec(ctx);
    }



    int exec(CommandContext<FabricClientCommandSource> ctx) {
        Vec3d e = ctx.getSource().getPlayer().getPos();
        product.setX((long) e.getX());
        product.setY((long) e.getY());
        product.setZ((long) e.getZ());
        System.out.println(product);
        WithKbErr<List<KtsBox>> t;
        try {
            KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
            t = service.searchBox(KtsBkApiProvider.INSTANCE.auth(), product);
        } catch (UndeclaredThrowableException u) {
            ctx.getSource().getPlayer().sendMessage(Text.translatable(KbErr.APPLICATION_EXCEPTION.translatable()));
            return 1;
        }
        inv.clear();
        if(t.t == KbErr.SUCCESS) {
            for(int i = 0; i < t.u.size(); i++) {
                KtsBox box = t.u.get(i);
                ItemStack item;
                String serializationItem = box.getMinecraftSerializedItem();
                StringNbtReader snr = new StringNbtReader(new StringReader(serializationItem));
                try {
                    NbtCompound nbt = snr.parseCompound();
                    item = ItemStack.fromNbt(nbt);//new ItemStack(Registries.ITEM.getEntry(o), (int)box.getCountPerTransaction());

                    NbtList lore = new NbtList();
                    lore.add(NbtString.of(Text.Serialization.toJsonString(Text.of("Count transaction: " + box.getCountNow()))));
                    lore.add(NbtString.of(Text.Serialization.toJsonString(Text.of("Cost/count: " + box.getBuyCostPerTransaction() + "/" + box.getCountPerTransaction()))));
                    lore.add(NbtString.of(Text.Serialization.toJsonString(Text.of(box.getWorld().getKtsbkName() + " [" + box.getX() + "," + box.getY() + "," + box.getZ() + "]"))));
                    item.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
                    item.setCount((int) box.getCountPerTransaction());

                    inv.setStack(i, item);
                } catch (CommandSyntaxException y) {
                    item = new ItemStack(Items.BARRIER);
                    item.setCount((int) box.getCountPerTransaction());
                    inv.setStack(i, item);
                }
            }
            //ctx.getSource().getClient().execute(() -> {
                SearchScreen screen = new SearchScreen(
                        Text.literal("ktsbk search"),
                        t.u,
                        inv
                );
                ctx.getSource().getClient().setScreen(
                    screen
                );

                try {
                    ctx.getSource().getClient().run();
                } catch (UnsupportedOperationException err) {
                    ctx.getSource().getPlayer().sendMessage(Text.literal("Mod gui render conflict."));
                    throw err;
                }


            //ctx.getSource().getClient().run();
            //});

            //ctx.getSource().getClient().run();
        } else {
            ctx.getSource().getPlayer().sendMessage(Text.translatable(t.t.translatable()));
        }
        product.clear();
        return 1;
    }
}
