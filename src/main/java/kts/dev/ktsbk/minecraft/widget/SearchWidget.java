package kts.dev.ktsbk.minecraft.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.IntFunction;

public class SearchWidget extends ClickableWidget {
    private final Identifier ITEM_BACK = new Identifier("ktsbk", "textures/slot.png");
    private static final int SLOT_SIZE = 18;
    private final int x, y, horizontalSlots, verticalSlots;
    private final int height, width;
    private final Text message;
    private final Inventory inv;
    private int itemInvOffset = 0;
    public IntFunction<Void> onClickCallback;
    public SearchWidget(int x, int y, int horizontalSlots, int verticalSlots, Inventory inv, Text message) {
        super(x  - horizontalSlots * SLOT_SIZE / 2, y - verticalSlots * SLOT_SIZE / 2, horizontalSlots * SLOT_SIZE, verticalSlots * SLOT_SIZE, message);
        this.x = x - horizontalSlots * SLOT_SIZE / 2;
        this.y = y - verticalSlots * SLOT_SIZE / 2;
        this.horizontalSlots = horizontalSlots;
        this.verticalSlots = verticalSlots;

        this.width = verticalSlots * SLOT_SIZE;
        this.height = horizontalSlots * SLOT_SIZE;
        this.message = message;
        this.inv = inv;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer r = MinecraftClient.getInstance().textRenderer;
        context.drawText(
                r,
                message,
                this.x,
                this.y - r.fontHeight,
                0xffffff,
                false
        );
        Text tOffset = Text.of(String.valueOf(itemInvOffset));
        context.drawText(
                r,
                tOffset,
                this.x + this.height - r.getWidth(tOffset),
                this.y - r.fontHeight,
                0xffffff,
                false
        );

        if(
                this.x <= mouseX && mouseX <= this.x + height &&
                this.y <= mouseY && mouseY <= this.y + width
        ) {
            int currentSlotIndex = calcSlot(mouseX, mouseY);
            ItemStack stack = inv.getStack(currentSlotIndex);
            if(!stack.isEmpty()) {
                context.drawTooltip(
                        r,
                        stack.getTooltip(MinecraftClient.getInstance().player, TooltipContext.ADVANCED),
                        mouseX,
                        mouseY
                );
            }
        }

        int ind;
        for(int i = 0; i < verticalSlots; i++) {
            for(int j = 0; j < horizontalSlots; j++) {
                ind = i * horizontalSlots + j + itemInvOffset * horizontalSlots;
                context.drawTexture(ITEM_BACK, x + j * SLOT_SIZE, y + i * SLOT_SIZE, 0, 0, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
                if(ind >= this.inv.size()) continue;
                context.drawItem(this.inv.getStack(ind), x + j * SLOT_SIZE + 1, y + i * SLOT_SIZE + 1);
                context.drawItemInSlot(
                        MinecraftClient.getInstance().textRenderer,
                        this.inv.getStack(ind),
                        x + j * SLOT_SIZE + 1,
                        y + i * SLOT_SIZE + 1
                );

            }
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }



    private int calcSlot(double mouseX, double mouseY) {
        int slotX = ((int)mouseX - this.x) / SLOT_SIZE;
        int slotY = ((int)mouseY - this.y) / SLOT_SIZE;

        return slotX + (slotY + this.itemInvOffset) * horizontalSlots;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if(onClickCallback != null) onClickCallback.apply(calcSlot(mouseX, mouseY));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(
                this.inv.size() > verticalSlots * horizontalSlots &&
                this.itemInvOffset - (int)verticalAmount >= 0 &&
                this.itemInvOffset - (int)verticalAmount < this.inv.size() / horizontalSlots - 1
        ) {
            this.itemInvOffset -= (int) verticalAmount;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
