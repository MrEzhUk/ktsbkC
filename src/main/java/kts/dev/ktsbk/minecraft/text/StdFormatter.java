package kts.dev.ktsbk.minecraft.text;

import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class StdFormatter {
    public static final int GRAY = 0x6a6a6a;
    public static final int LIGHT_ORANGE = 0xea8f25;
    public static final int ORANGE = 0xc97109;
    public static final int WHITE = 0xffffff;
    MutableText text;
    public StdFormatter() {
        text = Text.literal("");
    }
    public static StdFormatter create() {
        return new StdFormatter();
    }

    public Text build() {
        return this.text;
    }

    public StdFormatter gray(String str) {
        return gray(Text.literal(str));
    }
    public StdFormatter gray(Text str) {
        this.text.append(Text.literal("").setStyle(Style.EMPTY.withColor(GRAY)).append(str));
        return this;
    }

    public StdFormatter std(String str) {
        return std(Text.literal(str));
    }
    public StdFormatter std(Text str) {
        this.text.append(Text.literal("").append(str));
        return this;
    }
    public StdFormatter lorng(String str) {
        return lorng(Text.literal(str));
    }
    public StdFormatter lorng(Text str) {
        this.text.append(Text.literal("").setStyle(Style.EMPTY.withColor(LIGHT_ORANGE)).append(str));
        return this;
    }

    public StdFormatter orng(String str) {
        return orng(Text.literal(str));
    }
    public StdFormatter orng(Text str) {
        this.text.append(Text.literal("").setStyle(Style.EMPTY.withColor(ORANGE)).append(str));
        return this;
    }

    public StdFormatter acc(KtsAccount acc) {
        if(!acc.climbBlocked()) {
            return StdFormatter.create()
                    .std(acc.getId() + " ")
                    .lorng(acc.getName() + " ")
                    .std(acc.getCurrency().getShortName())
                    .std("(")
                    .lorng(acc.getCurrency().getServer().getShortName())
                    .std("): ")
                    .orng(String.valueOf(acc.getBalance()));
        } else {
            return StdFormatter.create()
                    .std(acc.getId() + " ")
                    .gray(acc.getName() + " ")
                    .std(acc.getCurrency().getShortName())
                    .std("(")
                    .gray(acc.getCurrency().getServer().getShortName())
                    .std("): ")
                    .gray(String.valueOf(acc.getBalance()));
        }
    }


}
