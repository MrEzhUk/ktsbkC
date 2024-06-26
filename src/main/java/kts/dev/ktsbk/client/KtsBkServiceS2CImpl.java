package kts.dev.ktsbk.client;

import kts.dev.ktsbk.common.services.KtsBkServiceS2C;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class KtsBkServiceS2CImpl implements KtsBkServiceS2C {
    // void chat(String message);
    public void error(String message) {
        if(MinecraftClient.getInstance().player == null) {
            System.out.println(message);
            return;
        }
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Error from server: " + message).setStyle(Style.EMPTY.withColor(0xff0000)));
    }
    // void errorKbErr(KbErr kb);
    // void updateAccount(KtsAccount acc);
    // void updateUser(KtsUser usr);
    // void updateBox(KtsBSideBox box);
}
