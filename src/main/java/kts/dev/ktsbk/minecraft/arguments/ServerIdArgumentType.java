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
import kts.dev.ktsbk.common.db.multiworld.KtsServer;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.Pair;
import kts.dev.ktsbk.common.utils.WithKbErr;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerIdArgumentType implements ArgumentType<Long> {
    String rawArgument;
    BiMap<Long, String> loaded;

    public ServerIdArgumentType() {
        rawArgument = "";
        loaded = HashBiMap.create();
    }

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        rawArgument = reader.readString();

        Long id = loaded.inverse().getOrDefault(rawArgument, null);
        if(id == null) return 0L;
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
        WithKbErr<List<KtsServer>> p = null;
        try {
            p = service.autoCompleteServer(KtsBkApiProvider.INSTANCE.auth(), getRawArgument());
        } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {}

        if(p == null) {
            builder.suggest("(no connection)");
            return builder.buildFuture();
        }

        if(p.t != KbErr.SUCCESS) {
            builder.suggest(p.t.translatable());
            return builder.buildFuture();
        }

        for(KtsServer server : p.u) {
            builder.suggest(server.getName());
            //builder.suggest(server.getShortName());
            loaded.forcePut(server.getId(), server.getName());
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
