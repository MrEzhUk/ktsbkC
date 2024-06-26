package kts.dev.ktsbk.minecraft.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RollbackBlock extends ClickableWidget {
    public RollbackBlock(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("rollbackBlock"));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(MinecraftClient.getInstance().textRenderer, "testing", this.getX(), this.getY(), 0xffffff, true);
        context.drawTexture(new Identifier("ktsbk", "textures/rollback_block_orange.png"), this.getX(), this.getY(), 0, 0, 80, 1, 80, 1);
        context.drawTexture(new Identifier("ktsbk", "textures/rollback_block_gray.png"), this.getX(), this.getY() + 1, 0, 0, 80, 120, 80, 1);
    }
    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.setX(this.getX() + (int)deltaX);
        this.setY(this.getY() + (int)deltaY);
    }
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
