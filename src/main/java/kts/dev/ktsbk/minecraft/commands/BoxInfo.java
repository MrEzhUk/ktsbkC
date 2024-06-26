package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BoxInfo implements KtsBkCommand {
    private void printBoxesList(ClientPlayerEntity e, List<KtsBox> boxes) {
        MutableText base;
        for(KtsBox box : boxes) {
            if(box.isDisabled()) continue;
            if(box.isBlocked()) {
                base = Text.literal("").formatted(Formatting.DARK_GRAY);
            } else {
                base = Text.literal("");
            }

            e.sendMessage(base.append(Text.literal("[" + box.getId() + "] [" + box.getX() + "," + box.getY() + "," + box.getZ() + "]"))
                    .append(" ")
                    .append(box.getAccount().getName())
                    .append(" ")
                    .append(box.getMinecraftIdentifier())
            );
        }
    }
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("info").then(ClientCommandManager.literal("box").executes(ctx -> {
            KtsBkServiceC2S s = KtsBkApiProvider.INSTANCE.getCacheService();

            WithKbErr<List<KtsBox>> r = s.getMyBoxes(KtsBkApiProvider.INSTANCE.auth());
            if(r.t == KbErr.SUCCESS) {
                ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.info.boxes"));
                printBoxesList(ctx.getSource().getPlayer(), r.u);
            } else {
                ctx.getSource().getPlayer().sendMessage(Text.translatable(r.t.translatable()));
            }

            return 1;
        }));
    }
}
