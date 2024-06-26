package kts.dev.ktsbk.minecraft.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ComboBoxWidget extends ClickableWidget {
    private final List<String> options;
    int x, y;
    TextRenderer tr = MinecraftClient.getInstance().textRenderer;
    private static final Identifier COMBO_BOX_RESULT = new Identifier("ktsbk", "textures/combobox_result_field.png");
    private static final Identifier COMBO_BOX_UP = new Identifier("ktsbk", "textures/combobox_up.png");
    private static final Identifier COMBO_BOX_ACTIVE_SELECTION = new Identifier("ktsbk", "textures/combobox_active_selection.png");
    private static final Identifier COMBO_BOX_SELECTION = new Identifier("ktsbk", "textures/combobox_simple_selection.png");
    private static final Identifier COMBO_BOX_DOWN = new Identifier("ktsbk", "textures/combobox_down.png");
    public ComboBoxWidget(int x, int y, int width, int height, List<String> options) {
        super(x, y, width, height, Text.literal(options.get(0)));
        this.options = options;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().scale(0.5f, 0.5f, 0.0f);
        int local_y = y;
        context.drawTexture(COMBO_BOX_RESULT, x, local_y, 0, 0, 160, 38, 160, 38);
        local_y += 39;
        for(int i = 0; i < options.size(); i++) {
            if(i % 2 == 0) {
                context.drawTexture(COMBO_BOX_SELECTION, x, local_y + i * 32, 0, 0, 160, 32, 160, 32);
            } else {
                context.drawTexture(COMBO_BOX_ACTIVE_SELECTION, x, local_y + i * 32, 0, 0, 160, 32, 160, 32);
            }

            context.drawText(tr, options.get(i), this.x + (160 - tr.getWidth(options.get(i))) / 2, local_y + i * 32 + (32 - tr.fontHeight) / 2, 0xffffff, true);
        }
        context.getMatrices().pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
