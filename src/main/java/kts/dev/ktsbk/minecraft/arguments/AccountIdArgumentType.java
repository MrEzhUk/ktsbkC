package kts.dev.ktsbk.minecraft.arguments;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AccountIdArgumentType implements ArgumentType<Long> {
    String rawArgument;
    BiMap<Long, String> loaded;

    public AccountIdArgumentType() {
        rawArgument = "";
        loaded = HashBiMap.create();
    }

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        rawArgument = reader.readString();
        if(rawArgument.startsWith(".")) {
            return Long.parseLong(rawArgument.substring(1));
        } else {
            Long id = loaded.inverse().getOrDefault(rawArgument, null);
            if(id == null) return 0L;
            return id;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
        if(rawArgument.startsWith(".")) {
            try {
                long a = Long.parseLong(rawArgument.substring(1));
                String sug = loaded.getOrDefault(a, null);
                if(sug == null) {
                    WithKbErr<KtsAccount> A = null;
                    try {
                        A = service.getAccountById(KtsBkApiProvider.INSTANCE.auth(), a);
                    } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {}

                    if(A == null) {
                        builder.suggest("(no connection)");
                        return builder.buildFuture();
                    }

                    if(A.t != KbErr.SUCCESS) {
                        sug = "(not found)";
                    } else {
                        sug = A.u.getName();
                    }
                }
                builder.suggest(sug);
            } catch (NumberFormatException | WebsocketNotConnectedException ignore) {}
            return builder.buildFuture();
        }
        WithKbErr<List<KtsAccount>> accounts = null;
        try {
            accounts = service.autoCompleteAccount(KtsBkApiProvider.INSTANCE.auth(), getRawArgument());
        } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {}
        if(accounts == null) return builder.buildFuture();
        if(accounts.t != KbErr.SUCCESS) return builder.buildFuture();

        for(KtsAccount acc : accounts.u) {
            loaded.forcePut(acc.getId(), acc.getName());
            builder.suggest(acc.getName());
        }
        return builder.buildFuture();
    }

    public static Long getId(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Long.class);
    }

    public String getRawArgument() {
        return rawArgument;
    }


}
