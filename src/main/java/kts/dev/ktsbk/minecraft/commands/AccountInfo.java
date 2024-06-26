package kts.dev.ktsbk.minecraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.db.users.KtsUser;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.Pair;
import kts.dev.ktsbk.common.utils.WithKbErr;
import kts.dev.ktsbk.minecraft.arguments.AccountIdArgumentType;
import kts.dev.ktsbk.minecraft.text.StdFormatter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class AccountInfo implements KtsBkCommand {

    private void printAccountsList(ClientPlayerEntity e, List<KtsAccount> accounts) {
        boolean disabled;
        for(KtsAccount acc : accounts) {
            disabled = acc.isDisabled() || acc.getCurrency().isDisabled() || acc.getCurrency().getServer().isDisabled();
            if(disabled) continue;
            e.sendMessage(StdFormatter.create().acc(acc).build());
        }
    }
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getCommandLiteral() {
        return ClientCommandManager.literal("info").then(ClientCommandManager.literal("account").executes(ctx -> {
            KtsBkServiceC2S s = KtsBkApiProvider.INSTANCE.getCacheService();

            WithKbErr<List<KtsAccount>> r = s.getMyAccounts(KtsBkApiProvider.INSTANCE.auth());
            //System.out.println("!!!!! " + r);
            //System.out.println(r.t.ordinal());
            //System.out.println(r.t == KbErr.SUCCESS);
            //System.out.println(r.u);
            if(r.t == KbErr.SUCCESS) {
                ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.info.accounts"));
                printAccountsList(ctx.getSource().getPlayer(), r.u);
            } else {
                ctx.getSource().getPlayer().sendMessage(Text.translatable(r.t.translatable()));
                return 1;
            }

            r = s.getMyMembership(KtsBkApiProvider.INSTANCE.auth());
            if(r.t == KbErr.SUCCESS) {
                ctx.getSource().getPlayer().sendMessage(Text.translatable("ktsbk.info.membership"));
                printAccountsList(ctx.getSource().getPlayer(), r.u);
            } else {
                ctx.getSource().getPlayer().sendMessage(Text.translatable(r.t.translatable()));
            }
            return 1;
        }).then(ClientCommandManager.argument("account", new AccountIdArgumentType()).executes(ctx -> {
            KtsBkServiceC2S s = KtsBkApiProvider.INSTANCE.getCacheService();

            WithKbErr<KtsAccount> p = s.getAccountById(KtsBkApiProvider.INSTANCE.auth(), AccountIdArgumentType.getId(ctx, "account"));
            if(p.t == KbErr.SUCCESS) {
                WithKbErr<List<KtsUser>> m = s.getAccountMembership(KtsBkApiProvider.INSTANCE.auth(), p.u.getId());
                //ctx.getSource().getPlayer().sendMessage(Text.literal("Account: " + p.u.getName() + "\nOwner: " + p.u.getUser().getNickname() + "\nMembership:"));
                ctx.getSource().getPlayer().sendMessage(
                        Text.translatable("ktsbk.create_box.account").append(p.u.getName() + "\n")
                        .append(Text.translatable("ktsbk.base.owner")).append(p.u.getUser().getNickname() + "\n")
                        .append(Text.translatable("ktsbk.base.membership"))
                );
                for(KtsUser usr : m.u) {
                    ctx.getSource().getPlayer().sendMessage(Text.literal("* " + usr.getNickname()));
                }
            } else {
                ctx.getSource().getPlayer().sendMessage(Text.translatable(p.t.translatable()));
            }
            return 1;
        })));
    }
}
