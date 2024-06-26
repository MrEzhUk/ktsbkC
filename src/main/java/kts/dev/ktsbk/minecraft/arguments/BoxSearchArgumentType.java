package kts.dev.ktsbk.minecraft.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.multiworld.KtsServer;
import kts.dev.ktsbk.common.db.multiworld.KtsWorld;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.Pair;
import kts.dev.ktsbk.common.utils.SearchProduct;
import kts.dev.ktsbk.common.utils.WithKbErr;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BoxSearchArgumentType implements ArgumentType<Void> {
    SearchProduct product;
    boolean first;

    public BoxSearchArgumentType(SearchProduct product) {
        this(product, false);
    }
    public BoxSearchArgumentType(SearchProduct product, boolean first) {
        this.first = first;
        this.product = product;
    }

    public static List<String> FIRST_ARGUMENT = new ArrayList<>() {{
        add("srv:"); add("server:");
        add("w:"); add("world:");
        add("cur:"); add("currency");
        add("r:"); add("radius:");
        add("s:"); add("sort:");
        add("i:"); add("id:");
        add("cn:"); add("count:");
        add("cs:"); add("cost:");

    }};
    String rawString;

    @Override
    public Void parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder sb = new StringBuilder();
        while(reader.canRead() && reader.peek() != ' ') {
            sb.append(reader.read());
        }
        rawString = sb.toString();

        if(!rawString.matches("[^:]*:[^:]*")) {
            if(this.first) product.clear();
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
        }

        String[] a = rawString.split(":");
        try {
            KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
            if (a[0].startsWith("w")) {
                WithKbErr<KtsWorld> p = service.getWorldByKtsBkName(KtsBkApiProvider.INSTANCE.auth(), a[1]);
                if (p.t != KbErr.SUCCESS) return null;
                product.setWorldId(p.u.getId());
            } else if (a[0].startsWith("i")) {
                product.setMinecraftId(a[1]);
            } else if (a[0].startsWith("r")) {
                product.setRadius(Long.parseLong(a[1]));
            } else if (a[0].startsWith("cn") || a[0].startsWith("count")) {
                product.setMinCount(Long.parseLong(a[1]));
            } else if (a[0].startsWith("cs") || a[0].startsWith("cost")) {
                product.setMaxCostPerCount(Double.parseDouble(a[1]));
            } else if (a[0].startsWith("s")) {
                if (a[1].equals("by_cost")) {
                    product.setSortedByCost(true);
                } else if (a[1].equals("by_radius")) {
                    product.setSortedByNear(true);
                }
            }
        } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {}
        return null;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        System.out.println(rawString);
        if(rawString == null || !rawString.contains(":")) {
            for(String sug : FIRST_ARGUMENT) {
                if(rawString == null || sug.startsWith(rawString)) {
                    builder.suggest(sug);
                }
            }
        } else {
            String after = "";
            String before;
            if(!rawString.endsWith(":")) {
                String[] a = rawString.split(":");
                before = a[0] + ":";
                after = a[1];
            } else {
                before = rawString;
            }
            try {
                KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
                if(rawString.startsWith("srv") || rawString.startsWith("server")) {
                    WithKbErr<List<KtsServer>> srv = service.autoCompleteServer(null, after);
                    if(srv.t != KbErr.SUCCESS) {
                        builder.suggest("(" + srv.t.name() + ")");
                    } else {
                        for (KtsServer si : srv.u) {
                            builder.suggest(before + si.getName());
                        }
                    }
                } else if (rawString.startsWith("w")) {
                    WithKbErr<List<KtsWorld>> r = service.autoCompleteWorld(KtsBkApiProvider.INSTANCE.auth(), after);
                    if(r.t == KbErr.SUCCESS) {
                        for (KtsWorld w : r.u) {
                            builder.suggest(before + w.getKtsbkName());
                        }
                    } else {
                        builder.suggest(r.t.translatable());
                    }
                } else if(rawString.startsWith("s")) {
                    builder.suggest(before + "by_cost");
                    builder.suggest(before + "by_radius");
                } else if(rawString.startsWith("i")) {
                    int a = 0;
                    for (Item i : Registries.ITEM) {
                        Optional<RegistryKey<Item>> k = Registries.ITEM.getEntry(i).getKey();
                        if (k.isPresent() && k.get().getValue().getPath().startsWith(after)) {
                            builder.suggest(before + k.get().getValue().getPath());
                            a++;
                            if (a > 9) break;
                        }
                    }
                }

            } catch (UndeclaredThrowableException | WebsocketNotConnectedException e) {
                builder.suggest("(no connection)");
            }
        }
        rawString = null;
        return builder.buildFuture();
    }
}
