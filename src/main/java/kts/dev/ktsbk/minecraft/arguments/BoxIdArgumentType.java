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
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BoxIdArgumentType implements ArgumentType<Long> {
    String rawArgument;
    BiMap<Long, String> loaded;

    public BoxIdArgumentType() {
        rawArgument = "";
        loaded = HashBiMap.create();
    }

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        rawArgument = reader.readString();
        try {
            return Long.parseLong(rawArgument);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();

        WithKbErr<List<KtsBox>> t;
        try {
            t = service.getMyBoxes(KtsBkApiProvider.INSTANCE.auth());
            if(t.t != KbErr.SUCCESS) {
                builder.suggest(t.t.translatable());
                return builder.buildFuture();
            }
            for(KtsBox box : t.u) {
                builder.suggest((int)box.getId());
            }
            return builder.buildFuture();
        } catch (UndeclaredThrowableException | WebsocketNotConnectedException e) {
            builder.suggest("(no connection)");
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