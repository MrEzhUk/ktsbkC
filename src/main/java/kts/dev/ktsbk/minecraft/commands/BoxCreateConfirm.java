package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.box.KtsBoxType;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class BoxCreateConfirm implements KtsBkCommand {
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("confirm").then(ClientCommandManager.literal("box").executes(ctx -> {
            if(BoxCreate.box.getId() != 0) {
                ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.box.nothing_confirm"));
                return 1;
            }
            KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
            KbErr e = service.createBox(
                    KtsBkApiProvider.INSTANCE.auth(),
                    BoxCreate.box.getX(),
                    BoxCreate.box.getY(),
                    BoxCreate.box.getZ(),
                    BoxCreate.box.getWorld().getId(),
                    BoxCreate.box.getAccount().getId(),
                    BoxCreate.box.getMinecraftIdentifier(),
                    BoxCreate.box.getMinecraftSerializedItem(),
                    BoxCreate.box.getCountPerTransaction(),
                    BoxCreate.box.getBuyCostPerTransaction(),
                    BoxCreate.box.getSellCostPerTransaction(),
                    BoxCreate.box.getBoxType()
            );
            if(e == KbErr.SUCCESS) BoxCreate.box.setId(-1);
            ctx.getSource().getPlayer().sendMessage(Text.translatable(e.translatable()));
            return 1;
        }));
    }
}
