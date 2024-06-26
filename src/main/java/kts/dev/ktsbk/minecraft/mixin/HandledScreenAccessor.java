package kts.dev.ktsbk.minecraft.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor { //HandledScreenAccessor<T extends ScreenHandler> extends ScreenHandlerProvider<T> {
    @Accessor("x")
    int getX();
    @Accessor("y")
    int getY();
    @Accessor("backgroundWidth")
    int getBackgroundWidth();
    @Accessor("backgroundHeight")
    int getBackgroundHeight();
}
