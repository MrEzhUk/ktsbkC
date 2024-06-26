package kts.dev.ktsbk.minecraft.renderer;

import kts.dev.ktsbk.minecraft.mixin.WorldRendererInvoker;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class KtsBkBlockOutline {
    private static final VoxelShape shape = VoxelShapes.cuboid(0,0,0,1,1,1);
    private final BlockPos pos;
    private final Box box;
    private final float r, g, b, alpha;
    public KtsBkBlockOutline(BlockPos pos, float r, float g, float b, float alpha) {
        this.pos = pos;
        this.box = new Box(pos);
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
    }

    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, Camera camera) {
        WorldRendererInvoker.drawCuboidShapeOutlineInvoke(
                matrices, vertexConsumer, shape,
                this.pos.getX() - camera.getPos().x,
                this.pos.getY() - camera.getPos().y,
                this.pos.getZ() - camera.getPos().z,
                r, g, b, alpha
        );
    }

    public BlockPos getPos() {
        return pos;
    }
}
