package kts.dev.ktsbk.minecraft.controller;


import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.client.KtsbkConfig;
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;

public class BoxScreenProvider {
    public static boolean disabled = false;
    private static BoxController2 actualBoxController = null;

    public static BoxController2 getInstance() {
        return actualBoxController;
    }

    public static void onOpen(GenericContainerScreen screen, BlockPos pos) {
        if(disabled) return;
        if(!screen.getTitle().getString().startsWith("ktsbkbox")) return;

        if(actualBoxController != null) {
            actualBoxController.close();
        }
        try {
            KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
            WithKbErr<KtsBox> p = service.getBoxByXYZ(
                    KtsBkApiProvider.INSTANCE.auth(),
                    KtsbkConfig.config.getWorldSelected(),
                    pos.getX(), pos.getY(), pos.getZ()
            );
            if(p.t != KbErr.SUCCESS) return;
            actualBoxController = new BoxController2(screen, p.u);
            KtsBkRenderer.INSTANCE.selected.clear();
        } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {
            if (MinecraftClient.getInstance().player == null) return;
            MinecraftClient.getInstance().player.sendMessage(Text.translatable(KbErr.CONNECTION_ERROR.translatable()));
        }
    }

    public static void onClose() {
        actualBoxController.close();
    }

}

