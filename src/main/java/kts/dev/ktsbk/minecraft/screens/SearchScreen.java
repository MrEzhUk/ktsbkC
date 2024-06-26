package kts.dev.ktsbk.minecraft.screens;

import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.minecraft.renderer.KtsBkBlockOutline;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import kts.dev.ktsbk.minecraft.widget.ComboBoxWidget;
import kts.dev.ktsbk.minecraft.widget.SearchWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class SearchScreen extends Screen {
    public SearchWidget widget;
    private final Inventory inv;
    private final List<KtsBox> boxes;
    private final Text title;
    public SearchScreen(Text title, List<KtsBox> boxes, Inventory inv) {
        super(title);
        this.title = title;
        this.inv = inv;
        this.boxes = boxes;
        KtsBkRenderer.INSTANCE.selected.clear();
    }

    private Void onClickCallback(int ind) {
        KtsBox box = boxes.get(ind);
        KtsBkBlockOutline outline = new KtsBkBlockOutline(
                new BlockPos((int)box.getX(), (int)box.getY(), (int)box.getZ()),
                1, 0, 0, 1
        );

        KtsBkRenderer.INSTANCE.selected.add(outline);
        this.close();
        KtsBkRenderer.INSTANCE.renderSelected = true;
        return null;
    }

    @Override
    protected void init() {
        widget = new SearchWidget(this.width / 2,  this.height / 2, 9, 3, inv, title);
        widget.onClickCallback = this::onClickCallback;
        addDrawableChild(widget);
        //ComboBoxWidget c = new ComboBoxWidget(0, 0, 50, 50, List.of("testing1", "test", "tst", "hahha"));
        //addDrawableChild(c);
        //OptionListWidget s = new OptionListWidget(MinecraftClient.getInstance(), 0, 0, 100, 100);
        //s.
        //addDrawableChild(s);
    }

    @Override
    public void close() {
        super.close();
    }
}
