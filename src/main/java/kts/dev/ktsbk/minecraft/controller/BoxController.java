package kts.dev.ktsbk.minecraft.controller;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.client.KtsbkConfig;
import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.db.currencies.KtsCurrency;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import kts.dev.ktsbk.minecraft.mixin.HandledScreenAccessor;
import kts.dev.ktsbk.minecraft.renderer.KtsBkRenderer;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BoxController {
    public static final BoxController INSTANCE = new BoxController();
    private KtsBox box = null;
    private List<KtsAccount> accounts = null;
    private KtsAccount selectedAccount = null;
    private int selectedAccountInd = 0;
    private boolean blocked = true;
    private GenericContainerScreen instance = null;
    private HandledScreenAccessor accessor = null;
    private final Set<Integer> selectedSlots = new HashSet<>();
    private final Set<Integer> invSelectedSlots = new HashSet<>();
    private final Identifier CELL = new Identifier("ktsbk", "textures/o.png");
    public ButtonWidget changeAccountButton = ButtonWidget.builder(Text.literal("change"), this::onChangeAccount).size(80, 20).build();
    public ButtonWidget refreshBalanceButton = ButtonWidget.builder(Text.literal("⟳"), this::onRefreshBalance).size(20, 20).build();
    public ButtonWidget buyButton = ButtonWidget.builder(Text.literal("buy"), this::onBuy).size(50, 20).build();
    public ButtonWidget sellButton = ButtonWidget.builder(Text.literal("sell"), this::onSell).size(50, 20).build();
    public ButtonWidget lockButton = ButtonWidget.builder(Text.literal("🔒"), this::changeBlockStatus).size(20, 20).build();
    public ButtonWidget updateButton = ButtonWidget.builder(Text.literal("🗘"), this::onUpdateCount).size(20, 20).build();
    public EditBoxWidget countEntry = new EditBoxWidget(
            MinecraftClient.getInstance().textRenderer,
            0, 0,
            60, 16,
            Text.literal("Count"),
            Text.literal("")
            );
    public EditBoxWidget buyCostEntry = new EditBoxWidget(
            MinecraftClient.getInstance().textRenderer,
            0, 0,
            60, 16,
            Text.literal("buy_cost"),
            Text.literal("")
            );
    public EditBoxWidget sellCostEntry = new EditBoxWidget(
            MinecraftClient.getInstance().textRenderer,
            0, 0,
            60, 16,
            Text.literal("sell_cost"),
            Text.literal("")
    );

    public MultilineTextWidget viewError = new MultilineTextWidget(Text.literal(""), MinecraftClient.getInstance().textRenderer);
    public ButtonWidget blockBox = ButtonWidget.builder(Text.literal(""), this::onChangeBuyStatus).size(20, 20).build();
    private KbErr lastError = null;
    private ItemStack item = new ItemStack(Items.BARRIER, 1);
    private boolean isOwner = false;
    public boolean disabled = false;

    private BoxController() {}
    public void onScreenInit() {
        if(this.box == null) {
            return;
        }
        changeAccountButton.setPosition(
                accessor.getX() + accessor.getBackgroundWidth() + 1,
                accessor.getY() + accessor.getBackgroundHeight() / 2 + 40
        );
        refreshBalanceButton.setPosition(
                accessor.getX() + accessor.getBackgroundWidth() + 1 + 81,
                accessor.getY() + accessor.getBackgroundHeight() / 2 + 40
        );
        buyButton.setPosition(
                accessor.getX() + accessor.getBackgroundWidth() + 1,
                accessor.getY() + accessor.getBackgroundHeight() / 2 + 61
        );
        sellButton.setPosition(
                accessor.getX() + accessor.getBackgroundWidth() + 52,
                accessor.getY() + accessor.getBackgroundHeight() / 2 + 61
        );
        Screens.getButtons(instance).add(changeAccountButton);
        Screens.getButtons(instance).add(refreshBalanceButton);
        Screens.getButtons(instance).add(buyButton);
        Screens.getButtons(instance).add(sellButton);

        viewError.setX(accessor.getX() + accessor.getBackgroundWidth() + 1);
        viewError.setY(accessor.getY() + accessor.getBackgroundHeight() / 2 + 82);
        viewError.setMaxWidth((instance.width - accessor.getBackgroundWidth()) / 2);

        Screens.getButtons(instance).add(viewError);


        if(!isOwner) return;
        lockButton.setPosition(
                accessor.getX() - 21,
                accessor.getY()
        );
        updateButton.setPosition(
                accessor.getX() - 42,
                accessor.getY()
        );
        blockBox.setPosition(
                accessor.getX() - 63,
                accessor.getY()
        );
        countEntry.setPosition(
                accessor.getX() - 69,
                accessor.getY() + 21
        );
        buyCostEntry.setPosition(
                accessor.getX() - 69,
                accessor.getY() + 38
        );
        sellCostEntry.setPosition(
                accessor.getX() - 69,
                accessor.getY() + 55
        );

        Screens.getButtons(instance).add(lockButton);
        Screens.getButtons(instance).add(updateButton);
        Screens.getButtons(instance).add(blockBox);
        Screens.getButtons(instance).add(countEntry);
        Screens.getButtons(instance).add(buyCostEntry);
        Screens.getButtons(instance).add(sellCostEntry);
    }

    public void onOpen(GenericContainerScreen instance, BlockPos pos) {
        if(disabled) return;
        if(!instance.getTitle().getString().startsWith("ktsbkbox")) return;
        try {
            KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
            WithKbErr<KtsBox> p = service.getBoxByXYZ(
                    KtsBkApiProvider.INSTANCE.auth(),
                    KtsbkConfig.config.getWorldSelected(),
                    pos.getX(), pos.getY(), pos.getZ()
            );
            if (p.t == KbErr.SUCCESS) {
                KtsBkRenderer.INSTANCE.selected.clear();
                this.box = p.u;
                this.instance = instance;
                accessor = (HandledScreenAccessor) instance;

                loadAccounts(service);
                selectAccount();

                try {
                    StringNbtReader r = new StringNbtReader(new StringReader(this.box.getMinecraftSerializedItem()));
                    item = ItemStack.fromNbt(r.parseCompound());
                    item.setCount((int) box.getCountPerTransaction());
                } catch (CommandSyntaxException ignore) {}

                buyButton.active = false;
                sellButton.active = false;

                if(box.getBoxType().isBuy()) {
                    buyButton.active = !this.box.isBlocked();
                }

                if(box.getBoxType().isSell()) {
                    sellButton.active = !this.box.isBlocked();
                }



                for (KtsAccount acc : this.accounts) {
                    if (acc.getId() == this.box.getAccount().getId()) {
                        this.isOwner = true;
                        lockButton.setMessage(Text.literal("🔒"));
                        countEntry.setMessage(Text.literal(""));
                        buyCostEntry.setMessage(Text.literal(""));
                        blockBox.setMessage(box.isBlocked() ? Text.literal("⚠").formatted(Formatting.RED) : Text.literal("🛒").formatted(Formatting.GREEN));
                        break;
                    }
                }
                this.onScreenInit();
            }

        } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {
            if (MinecraftClient.getInstance().player == null) return;
            MinecraftClient.getInstance().player.sendMessage(Text.translatable(KbErr.CONNECTION_ERROR.translatable()));
        }
        //    }
        //};
    }
    
    private void changeBlockStatus(ButtonWidget b) {
        if(this.blocked) {
            
            this.blocked = false;
            b.setMessage(Text.literal("🔓"));
        } else {
            this.blocked = true;
            b.setMessage(Text.literal("🔒"));
        }
    }
    
    
    public void onClose() {
        this.selectedSlots.clear();
        this.invSelectedSlots.clear();

        this.accounts = null;
        this.selectedAccountInd = 0;
        this.selectedAccount = null;

        this.box = null;
        this.instance = null;
        this.accessor = null;

        this.buyCostEntry.setFocused(false);
        this.buyCostEntry.setText("");
        this.sellCostEntry.setText("");
        this.countEntry.setFocused(false);
        this.countEntry.setText("");
        this.updateButton.setFocused(false);
        this.lockButton.setFocused(false);
        this.buyButton.setFocused(false);
        this.refreshBalanceButton.setFocused(false);
        this.changeAccountButton.setFocused(false);
        this.blockBox.setFocused(false);
        this.sellButton.setFocused(false);
        this.viewError.setMessage(Text.literal(""));

        this.lastError = null;
        this.blocked = true;
        this.isOwner = false;
        this.item = new ItemStack(Items.BARRIER, 1);
    }

    public void boxInfoRender(DrawContext context, int mouseX, int mouseY, float delta) {
        if(this.box == null) return;


        context.getMatrices().push();
        int xOffset = 1;
        int yOffset = 0;


        context.getMatrices().translate(
                accessor.getX(),
                accessor.getY(),
                0.0f
        );

        for(int slotId : selectedSlots) {
            Slot slot = instance.getScreenHandler().getSlot(slotId);
            context.drawTexture(CELL, slot.x, slot.y, 0, 0, 16, 16, 16, 16);
        }

        for(int slotId : invSelectedSlots) {
            Slot slot = instance.getScreenHandler().getSlot(slotId);
            context.drawTexture(CELL, slot.x, slot.y, 0, 0, 16, 16, 16, 16);
        }


        context.getMatrices().translate(
                accessor.getBackgroundWidth() + xOffset,
                yOffset,
                0.0f
        );

        mouseX -= accessor.getX() + accessor.getBackgroundWidth() + xOffset;
        mouseY -= accessor.getY() + yOffset;

        context.drawItem(item, 0, 50);
        if(Math.abs(mouseX - 9) <= 9 && Math.abs(mouseY - 59) <= 9) {
            context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, item, 9, 59);
        }

        long s_buy = this.selectedSlots.size() * this.box.getBuyCostPerTransaction(); // кол-во товаров
        long tax_buy = (long)Math.ceil(s_buy * this.box.getAccount().getCurrency().getTransactionPercent() / 100.0);
        long s_sell = this.invSelectedSlots.size() * this.box.getSellCostPerTransaction();

        context.drawText(MinecraftClient.getInstance().textRenderer, "Продавец: " + box.getAccount().getName(), 0, 0, 0xffffff, true);
        context.drawText(MinecraftClient.getInstance().textRenderer, "boxId: " + this.box.getId(), 0, 10, 0xffffff, true);
        //context.drawText(MinecraftClient.getInstance().textRenderer, "Цена/шт: " + box.getBuyCostPerTransaction() + box.getAccount().getCurrency().getShortName() + "/" + box.getCountPerTransaction() + "шт", 0, 30, 0xffffff, true);
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.empty()
                        .append(Text.literal("📦").setStyle(Style.EMPTY.withColor(0xffa500)))
                        .append(box.getCountNow() + "")
                        .append(" " + box.getCountPerTransaction())
                        .append(Text.literal("₆₄").setStyle(Style.EMPTY.withColor(0x00ff00)))
                        .append(
                                box.getBoxType().isBuy() ?
                                        Text.empty()
                                                .append(Text.literal(" ▼").setStyle(Style.EMPTY.withColor(0xff0000)))
                                                .append(box.getBuyCostPerTransaction() + "")
                                        :
                                        Text.empty())
                        .append(
                                box.getBoxType().isSell() ?
                                        Text.empty()
                                                .append(Text.literal(" ▲").setStyle(Style.EMPTY.withColor(0x0096ff)))
                                                .append(box.getSellCostPerTransaction() + "")
                                        :
                                        Text.empty()
                        ),
                0, 20, 0xffffff, true
        );
        context.drawText(MinecraftClient.getInstance().textRenderer, box.getWorld().getKtsbkName(), 0, 30, 0xffffff, true);
        context.drawText(MinecraftClient.getInstance().textRenderer, "[" + box.getX() + ", " + box.getY() + ", " + box.getZ() + "]", 0, 40, 0xffffff, true);

        context.getMatrices().translate(0, accessor.getBackgroundHeight() / 2.0f, 0);
        context.drawText(MinecraftClient.getInstance().textRenderer, "Активный счет: " + selectedAccount.getName(), 0, 0, 0xffffff, true);
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("Баланс: " + selectedAccount.getBalance() + " ").append(selectedAccount.getCurrency().getShortName()), 0, 10, 0xffffff, true);
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                Text.literal("Сумма: ")
                .append(Text.literal(s_buy + "").setStyle(Style.EMPTY.withColor(0xff0000)))
                .append(tax_buy != 0 ? "+" : "")
                .append(Text.literal(tax_buy != 0 ? tax_buy + "" : "").setStyle(Style.EMPTY.withColor(0xff0000)))
                .append(s_sell != 0 ? "+" : "")
                .append(Text.literal(s_sell != 0 ? s_sell + "" : "").setStyle(Style.EMPTY.withColor(0x0000ff)))
                .append(box.getAccount().getCurrency().getShortName()),
                0, 20, 0xffffff, true
        );
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("Остаток: ").append(Text.literal((selectedAccount.getBalance() - (s_buy + tax_buy) + s_sell) + " ").setStyle(Style.EMPTY.withColor(0x34c924))).append(selectedAccount.getCurrency().getShortName()), 0, 30, 0xffffff, true);

        if(lastError != null) {
            viewError.setMessage(Text.translatable(lastError.translatable()));
        }

        context.getMatrices().pop();
    }

    public boolean clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        //System.out.println(slotId + " " + instance.getScreenHandler().getRows() + " " + instance.getScreenHandler().getInventory().size());
        if(box == null || instance == null || !blocked) return false;
        if(slotId < 0) return false;

        Slot s = this.instance.getScreenHandler().getSlot(slotId);

        s.getStack().removeSubNbt(ItemStack.DISPLAY_KEY);
        if(ItemStack.areEqual(s.getStack(), item)) {
            if(slotId < instance.getScreenHandler().getInventory().size()) {
                if(box.getBoxType().isBuy()) {
                    if (this.selectedSlots.contains(slotId)) {
                        this.selectedSlots.remove(slotId);
                    } else {
                        this.selectedSlots.add(slotId);
                    }
                }
            } else {
                if(box.getBoxType().isSell()) {
                    if (this.invSelectedSlots.contains(slotId)) {
                        this.invSelectedSlots.remove(slotId);
                    } else {
                        this.invSelectedSlots.add(slotId);
                    }
                }
            }
        }
        return true;
    }

    public void onChangeAccount(ButtonWidget b) {
        this.selectedAccountInd++;
        this.selectedAccountInd %= this.accounts.size();
        this.selectedAccount = this.accounts.get(selectedAccountInd);
    }
    public void onRefreshBalance(ButtonWidget b) {
        try {
            loadAccounts(KtsBkApiProvider.INSTANCE.getCacheService());
            for(KtsAccount acc : this.accounts) {
                if(acc.getId() == this.selectedAccount.getId()) {
                    this.selectedAccount = acc;
                    return;
                }
            }
            this.selectedAccount.setName("No account.");
        } catch (UndeclaredThrowableException | WebsocketNotConnectedException ignore) {}
    }
    private void loadAccounts(KtsBkServiceC2S service) {
        WithKbErr<List<KtsAccount>> accounts = service.getMyAccounts(KtsBkApiProvider.INSTANCE.auth());
        if(accounts.t != KbErr.SUCCESS) {
            this.accounts = null;
            return;
        }
        this.accounts = new ArrayList<>();
        for(KtsAccount acc : accounts.u) {
            if(acc.getCurrency().getId() != this.box.getAccount().getCurrency().getId()) continue;
            this.accounts.add(acc);
        }

        WithKbErr<List<KtsAccount>> m_accounts = service.getMyMembership(null);
        if(m_accounts.t != KbErr.SUCCESS) return;
        for(KtsAccount acc : m_accounts.u) {
            if(acc.getCurrency().getId() != this.box.getAccount().getCurrency().getId()) continue;
            this.accounts.add(acc);
        }


        //this.accounts = accounts.u;
    }

    private void selectAccount() {
        if(this.accounts == null || this.accounts.isEmpty()) {
            this.selectedAccount = new KtsAccount();
            this.selectedAccount.setName("No connection.");
            this.selectedAccount.setId(0);

            KtsCurrency cur = new KtsCurrency();
            cur.setShortName("??");
            this.selectedAccount.setCurrency(cur);
        } else {
            this.selectedAccount = accounts.get(0);
        }
    }

    public void onBuy(ButtonWidget b) {
        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
        KbErr e = service.buyInBox(KtsBkApiProvider.INSTANCE.auth(), this.selectedAccount.getId(), this.box.getId(), this.selectedSlots.size());
        if(e != KbErr.SUCCESS) {
            this.lastError = e;
            return;
        } else {
            this.lastError = KbErr.SUCCESS;
        }

        this.blocked = false;
        if(MinecraftClient.getInstance().interactionManager == null) return;

        this.box.setCountNow(this.box.getCountNow() - this.selectedSlots.size());

        for(Integer i : this.selectedSlots) {
            MinecraftClient.getInstance().interactionManager.clickSlot(
                    this.instance.getScreenHandler().syncId,
                    i,
                    1,
                    SlotActionType.QUICK_MOVE,
                    MinecraftClient.getInstance().player
            );
        }
        this.blocked = true;
        this.selectedSlots.clear();
    }

    public void onSell(ButtonWidget b) {
        List<Integer> empty_slots = new ArrayList<>();
        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();

        for(int i = 0; i < 27; i++) {
            if(this.instance.getScreenHandler().getSlot(i).getStack().isEmpty()) empty_slots.add(i);
        }

        if(empty_slots.size() - this.invSelectedSlots.size() < 0) {
            this.lastError = KbErr.ILLEGAL_COUNT;
            return;
        }


        KbErr e = service.sellInBox(KtsBkApiProvider.INSTANCE.auth(), this.selectedAccount.getId(), this.box.getId(), this.invSelectedSlots.size());
        if(e != KbErr.SUCCESS) {
            this.lastError = e;
            return;
        } else {
            this.lastError = KbErr.SUCCESS;
        }

        this.blocked = false;
        if(MinecraftClient.getInstance().interactionManager == null) return;



        this.box.setCountNow(this.box.getCountNow() + this.invSelectedSlots.size());

        for(Integer i : this.invSelectedSlots) {
            MinecraftClient.getInstance().interactionManager.clickSlot(
                    this.instance.getScreenHandler().syncId,
                    i,
                    1,
                    SlotActionType.SWAP,
                    MinecraftClient.getInstance().player
            );
            MinecraftClient.getInstance().interactionManager.clickSlot(
                    this.instance.getScreenHandler().syncId,
                    empty_slots.remove(0),
                    1,
                    SlotActionType.SWAP,
                    MinecraftClient.getInstance().player
            );

            //this.instance.getScreenHandler().getInventory().setStack(0, this.instance.getScreenHandler().getCursorStack());
            //ItemStack item = this.instance.getScreenHandler().slots.get(i).takeStack(1);
            //this.instance.getScreenHandler().getInventory().setStack(0, item);

        }
        this.blocked = true;
        this.invSelectedSlots.clear();
    }

    public void onUpdateCount(ButtonWidget b) {
        long c = 0, t = 0;
        for(Slot s : instance.getScreenHandler().slots) {
            if(t > 26) break;
            ItemStack st = s.getStack();
            st.removeSubNbt(ItemStack.DISPLAY_KEY);
            if(ItemStack.areEqual(st, item)) c++;
            t++;
        }

        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
        Long count;
        Long buy_cost;
        Long sell_cost;
        try {
            count = countEntry.getText().isEmpty() ? null : Long.parseLong(countEntry.getText());
            countEntry.setText("");
        } catch (NumberFormatException e) {
            this.lastError = KbErr.ILLEGAL_COUNT;
            return;
        }

        try {
            buy_cost = buyCostEntry.getText().isEmpty() ? null : Long.parseLong(buyCostEntry.getText());
            buyCostEntry.setText("");
        } catch (NumberFormatException e) {
            this.lastError = KbErr.ILLEGAL_COST;
            return;
        }

        try {
            sell_cost = sellCostEntry.getText().isEmpty() ? null : Long.parseLong(sellCostEntry.getText());
            sellCostEntry.setText("");
        } catch (NumberFormatException e) {
            this.lastError = KbErr.ILLEGAL_COST;
            return;
        }

        lastError = service.updateBoxData(null, box.getId(), c, count, buy_cost, sell_cost);
        if(lastError == KbErr.SUCCESS) {
            box.setCountNow(c);
            if(buy_cost != null) box.setBuyCostPerTransaction(buy_cost);
            if(sell_cost != null) box.setSellCostPerTransaction(sell_cost);
            if(count != null) {
                box.setCountPerTransaction(count);
                item.setCount(count.intValue());
                onUpdateCount(b);
            }
        }
    }

    public void onChangeBuyStatus(ButtonWidget b) {
        // block/unblock buying from box
        KbErr r;
        if(this.box.isBlocked()) {
            try {
                r = KtsBkApiProvider.INSTANCE.getCacheService().unblockBox(null, box.getId());
                if (r == KbErr.SUCCESS) {
                    b.setMessage(Text.literal("🛒").formatted(Formatting.GREEN));
                    this.box.setBlocked(false);
                    if(this.box.getBoxType().isBuy()) this.buyButton.active = true;
                    if(this.box.getBoxType().isSell()) this.sellButton.active = true;
                }
            } catch (WebsocketNotConnectedException | UndeclaredThrowableException e) {
                r = KbErr.CONNECTION_ERROR;
            }
        } else {
            try {
                r = KtsBkApiProvider.INSTANCE.getCacheService().blockBox(null, box.getId());
                if (r == KbErr.SUCCESS) {
                    b.setMessage(Text.literal("⚠").formatted(Formatting.RED));
                    this.box.setBlocked(true);
                    this.buyButton.active = false;
                    this.sellButton.active = false;
                }
            } catch (WebsocketNotConnectedException | UndeclaredThrowableException e) {
                r = KbErr.CONNECTION_ERROR;
            }
        }
        this.lastError = r;
    }
}
