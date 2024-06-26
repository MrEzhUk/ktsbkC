package kts.dev.ktsbk.minecraft.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import kts.dev.ktsbk.client.KtsbkConfig;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
//import red.jackf.whereisit.client.api.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KtsBkRenderer {
    // renderBlockBounding
    public static KtsBkRenderer INSTANCE = new KtsBkRenderer();
    public KtsBkBlockOutline lastClicked = null;
    public final List<KtsBkBlockOutline> selected = new ArrayList<>();
    public VertexFormat.DrawMode mode; //VertexFormat.DrawMode.TRIANGLE_FAN;
    public boolean renderSelected = false;
    private KtsBkRenderer() {
        KtsbkConfig.config.load();
        mode = getModeById(KtsbkConfig.config.getRenderMode());
    }
    public VertexFormat.DrawMode getModeById(int id) {
        if (id == 1) {
            return VertexFormat.DrawMode.TRIANGLE_FAN;
        }
        return VertexFormat.DrawMode.DEBUG_LINES;
    }

    public void renderBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Camera camera) {
        MinecraftClient minecraft = MinecraftClient.getInstance();

        if(renderSelected) for(KtsBkBlockOutline o : selected) o.render(matrices, vertexConsumer, camera);
        if(minecraft.player != null && lastClicked != null && minecraft.player.getMainHandStack().getItem() == Items.RED_DYE) {
            lastClicked.render(matrices, vertexConsumer, camera);
        }

    }

}
