package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.client.KtsbkConfig;
import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.db.box.KtsBoxType;
import kts.dev.ktsbk.common.db.multiworld.KtsWorld;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import kts.dev.ktsbk.minecraft.arguments.AccountIdArgumentType;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BoxCreate implements KtsBkCommand {
    public static KtsBox box = new KtsBox();
    static {
        box.setId(-1);
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("create").then(
                ClientCommandManager.literal("box")
                .then(ClientCommandManager.argument("box_type", StringArgumentType.word()).suggests((ctx, builder) -> {
                            builder.suggest(KtsBoxType.BUY_ONLY.toString());
                            builder.suggest(KtsBoxType.SELL_ONLY.toString());
                            builder.suggest(KtsBoxType.BUY_SELL.toString());
                            return builder.buildFuture();
                        })
                .then(ClientCommandManager.argument("account", new AccountIdArgumentType())
                .then(ClientCommandManager.argument("buy_cost", LongArgumentType.longArg(0))
                .then(ClientCommandManager.argument("sell_cost", LongArgumentType.longArg(0)).executes(ctx -> {
                    KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
                    if(KtsBkRenderer.INSTANCE.lastClicked == null) {
                        ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.box.no_box_select"));
                        return 1;
                    }

                    ItemStack item = ctx.getSource().getPlayer().getMainHandStack();
                    if(item.isEmpty()) {
                        ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.box.no_item_in_hand"));
                        return 1;
                    }
                    // clear lore
                    item.removeSubNbt(ItemStack.DISPLAY_KEY);
                    NbtCompound n = new NbtCompound();
                    item.writeNbt(n);
                    StringNbtWriter snw = new StringNbtWriter();
                    String stringItem = snw.apply(n);

                    if(stringItem.length() > 8192) {
                        ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.box.very_long_item"));
                        return 1;
                    }

                    BlockPos pos = KtsBkRenderer.INSTANCE.lastClicked.getPos();

                    WithKbErr<KtsAccount> acc = service.getAccountById(KtsBkApiProvider.INSTANCE.auth(), AccountIdArgumentType.getId(ctx, "account"));
                    WithKbErr<KtsWorld> world = service.getWorldById(KtsBkApiProvider.INSTANCE.auth(), KtsbkConfig.config.getWorldSelected());
                    if(acc.t != KbErr.SUCCESS) {
                        ctx.getSource().getPlayer().sendMessage(Text.translatable(acc.t.translatable()));
                        return 1;
                    }
                    if(world.t != KbErr.SUCCESS) {
                        ctx.getSource().getPlayer().sendMessage(Text.translatable(world.t.translatable()));
                        return 1;
                    }

                    box.setId(0);
                    box.setX(pos.getX());
                    box.setY(pos.getY());
                    box.setZ(pos.getZ());
                    box.setWorld(world.u);
                    box.setAccount(acc.u);
                    box.setCountPerTransaction(item.getCount());
                    box.setBuyCostPerTransaction(LongArgumentType.getLong(ctx, "buy_cost"));
                    box.setSellCostPerTransaction(LongArgumentType.getLong(ctx, "sell_cost"));
                    box.setMinecraftIdentifier(item.getItem().toString());
                    box.setMinecraftSerializedItem(stringItem);
                    try {
                        box.setBoxType(KtsBoxType.valueOf(StringArgumentType.getString(ctx, "box_type")));
                    } catch (IllegalArgumentException e) {
                        ctx.getSource().getPlayer().sendMessage(Text.literal("Box type error."));
                        return 1;
                    }

                    ctx.getSource().getPlayer().sendMessage(
                            Text.literal("")
                            .append(Text.translatable("ktsbk.create_box.account").getString() + acc.u.getName() + " " + acc.u.getCurrency().getShortName() + "(" + acc.u.getCurrency().getServer().getShortName() + ")\n")
                            .append(Text.translatable("ktsbk.create_box.world").getString() + world.u.getKtsbkName() + "\n")
                            .append(Text.translatable("ktsbk.create_box.coordinates").getString() + "[" + pos.toShortString() + "]\n")
                            .append(Text.translatable("ktsbk.create_box.id").getString() + item.getItem() + "\n")
                            .append(Text.literal("Box type: " + box.getBoxType() + "\n"))
                            .append(Text.translatable("ktsbk.create_box.count").getString() + item.getCount() + "\n")
                            .append(Text.translatable("ktsbk.create_box.cost").getString() + LongArgumentType.getLong(ctx, "buy_cost") + "/" + LongArgumentType.getLong(ctx, "sell_cost"))
                    );
                    ctx.getSource().getPlayer().sendMessage(
                            Text.literal("[")
                                    .append(Text.literal("✔")
                                            .setStyle(Style.EMPTY
                                                    .withColor(0x00ff00)
                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/k confirm box"))
                                            )
                                    )
                                    .append("] [")
                                    .append(Text.literal("✘")
                                            .setStyle(Style.EMPTY
                                                    .withColor(0xff0000)
                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/k deny box"))
                                            )
                                    )
                                    .append("]")
                    );
                    return 1;
                })))))
        );
    }
}
