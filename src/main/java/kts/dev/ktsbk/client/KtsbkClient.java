package kts.dev.ktsbk.client;

import kts.dev.ktsbk.minecraft.commands.*;
import kts.dev.ktsbk.minecraft.renderer.KtsBkBlockOutline;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.text.Text;

public class KtsbkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            new Login().register(dispatcher);
            new AccountInfo().register(dispatcher);

            new AccountPay().register(dispatcher);
            new AccountCreate().register(dispatcher);
            new AccountInvite().register(dispatcher);
            new AccountKick().register(dispatcher);
            new AccountDelete().register(dispatcher);

            new ServerSelect().register(dispatcher);
            new WorldSelect().register(dispatcher);

            new BoxSearch().register(dispatcher);
            new BoxInfo().register(dispatcher);

            new BoxCreate().register(dispatcher);
            new BoxCreateConfirm().register(dispatcher);
            new BoxCreateDeny().register(dispatcher);

            new BoxUnblock().register(dispatcher);
            new BoxBlock().register(dispatcher);
            new BoxDelete().register(dispatcher);

            new UserCreate().register(dispatcher);
            new UserTokenReset().register(dispatcher);
            new AccountMoneyIO().register(dispatcher);

            new AllSettings().register(dispatcher);
            new SelectionClear().register(dispatcher);
            new Testing().register(dispatcher);
        });

        KtsbkConfig.config.load();

        //WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
        //    KtsBkRenderer.INSTANCE.renderBlockOutline(worldRenderContext);
        //    return true;
        //});

        HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {
            if(!KtsBkRenderer.INSTANCE.selected.isEmpty()) {
                for(int i = 0; i < KtsBkRenderer.INSTANCE.selected.size(); i++) {
                    KtsBkBlockOutline outline = KtsBkRenderer.INSTANCE.selected.get(i);
                    drawContext.drawCenteredTextWithShadow(
                            MinecraftClient.getInstance().textRenderer,
                            Text.literal(outline.getPos().toShortString()),
                            drawContext.getScaledWindowWidth() / 2,
                            1 + MinecraftClient.getInstance().textRenderer.fontHeight * i,
                            0x00ff00
                    );
                }
            }

            //MinecraftClient MC = MinecraftClient.getInstance();
            //if(MC.player == null) return;
            //Vector3f camera_eye = new Vector3f(MC.gameRenderer.getCamera().getHorizontalPlane());
            //Vector3f orig_camera_eye = new Vector3f(camera_eye);
            //drawContext.getMatrices().push();
            //drawContext.getMatrices().translate(MC.getWindow().getFramebufferWidth() / 2.0, MC.getWindow().getFramebufferHeight() / 2.0, 0.0);

            //Vector3f originalCordM = new Vector3f(1, 1, 1);
            //Vector3f cordM = new Vector3f(originalCordM);
            //cordM.add(MC.player.getEyePos().toVector3f().negate());
            //camera_eye.mul(cordM.dot(camera_eye));
            //cordM.add(camera_eye.negate());
            //System.out.println(cordM);
            //drawContext.drawTexture(new Identifier("ktsbk", "textures/x.png"), (int) (cordM.x + MC.getWindow().getWidth() / 2), (int) (cordM.z + MC.getWindow().getHeight() / 2), 0, 0, 0, 3, 3, 3, 3);
            // distance: Math.sqrt(cordM.dot(cordM))
            //drawContext.getMatrices().pop();
        }));
    }
}
