package kts.dev.ktsbk.minecraft.screens;

import kts.dev.ktsbk.minecraft.widget.RollbackBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class RollbackScreen extends Screen {
    public RollbackScreen(Text title) {
        super(title);
    }
    @Override
    protected void init() {
        RollbackBlock block = new RollbackBlock(0, 0, 100, 16);
        this.addDrawableChild(block);
    }

}
