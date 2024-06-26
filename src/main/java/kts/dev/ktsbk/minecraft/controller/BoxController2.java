package kts.dev.ktsbk.minecraft.controller;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kts.dev.ktsbk.client.KtsBkApiProvider;
import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.db.box.KtsBoxType;
import kts.dev.ktsbk.common.db.currencies.KtsCurrency;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.KbErr;
import kts.dev.ktsbk.common.utils.WithKbErr;
import kts.dev.ktsbk.minecraft.mixin.HandledScreenAccessor;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

public class BoxController2 {
    private final GenericContainerScreen screen;
    private final HandledScreenAccessor accessor;
    private final KtsBox box;
    private final ItemStack item;
    private final List<ClickableWidget> widgets;
    private int selectedAccountInd = 0;
    private final List<Integer> selectedSlots;
    private final TextRenderer tr;
    private KtsAccount selectedAccount;
    private List<KtsAccount> accounts;
    private final Identifier BOARDER = new Identifier("ktsbk", "textures/o.png");
    private boolean blocked;
    // widgets
    private final MultilineTextWidget viewError = new MultilineTextWidget(Text.literal(""), MinecraftClient.getInstance().textRenderer);
    private final ButtonWidget buyButton = ButtonWidget.builder(Text.literal("buy"), this::onBuy).size(50, 20).build();
    private final ButtonWidget sellButton = ButtonWidget.builder(Text.literal("sell"), this::onBuy).size(50, 20).build();
    private final ButtonWidget lockButton = ButtonWidget.builder(Text.literal("🔒"), this::changeBlockStatus).size(20, 20).build();
    private final ButtonWidget updateButton = ButtonWidget.builder(Text.literal("🗘"), this::onUpdateCount).size(20, 20).build();
    private final ButtonWidget blockBox;
    public EditBoxWidget countEntry = new EditBoxWidget(
            MinecraftClient.getInstance().textRenderer,
            0, 0,
            60, 16,
            Text.literal("Count"),
            Text.literal("")
    );
    public EditBoxWidget costEntry = new EditBoxWidget(
            MinecraftClient.getInstance().textRenderer,
            0, 0,
            60, 16,
            Text.literal("Cost"),
            Text.literal("")
    );

    private Text blockBoxButton(boolean blocked) {
        return blocked ? Text.literal("⚠").formatted(Formatting.RED) : Text.literal("🛒").formatted(Formatting.GREEN);
    }

    public BoxController2(GenericContainerScreen screen, @NotNull KtsBox box) {
        this.screen = screen;
        this.accessor = (HandledScreenAccessor) screen;
        this.widgets = new ArrayList<>();
        this.box = box;
        tr = MinecraftClient.getInstance().textRenderer;
        selectedSlots = new ArrayList<>();
        blocked = true;
        // Item deserialization
        ItemStack item1 = null;
        try {
            StringNbtReader r = new StringNbtReader(new StringReader(this.box.getMinecraftSerializedItem()));
            item1 = ItemStack.fromNbt(r.parseCompound());
            item1.setCount((int) box.getCountPerTransaction());
        } catch (CommandSyntaxException ignore) {}

        if(item1 == null || item1.isEmpty()) {
            item = new ItemStack(Items.BARRIER, 1);
        } else {
            item = item1;
        }

        loadAccounts(KtsBkApiProvider.INSTANCE.getCacheService());
        selectAccount();


        blockBox  = ButtonWidget.builder(blockBoxButton(box.isBlocked()), this::onChangeBuyStatus).size(20, 20).build();
    }

    public void init() {
        widgets.clear();
        initCustomerPanel(accessor.getX() + accessor.getBackgroundWidth() + 1, accessor.getY());
        initProducerPanel(accessor.getX() - 60, accessor.getY());
        Screens.getButtons(screen).addAll(widgets);
    }

    public void initCustomerPanel(int x, int y) {
        initInfoPanel(new int[] {x}, new int[] {y});
        y += accessor.getBackgroundHeight() / 2;
        initAccountPanel(new int[] {x}, new int[] {y});
    }

    private void addText(int[] x, int[] y, Text text) {
        widgets.add(new TextWidget(x[0], y[0], 1000, tr.fontHeight, text, tr).alignLeft());
        y[0] += tr.fontHeight;
    }

    public void initInfoPanel(int[] x, int[] y) {
        addText(x, y, Text.literal("Продавец: " + box.getAccount().getName() + ", boxId: " + this.box.getId()));
        addText(x, y, Text.literal("Товар: " + box.getMinecraftIdentifier()));
        addText(x, y, Text.literal("Цена/шт: " + box.getBuyCostPerTransaction() + box.getAccount().getCurrency().getShortName() + "/" + box.getCountPerTransaction() + "шт"));
        addText(x, y, Text.literal("Кол-во лотов: " + box.getCountNow()));
        addText(x, y, Text.literal("Мир: " + box.getWorld().getKtsbkName()));
        addText(x, y, Text.literal("Координаты:" + "[" + box.getX() + ", " + box.getY() + ", " + box.getZ() + "]"));
    }

    public void initAccountPanel(int[] x, int[] y) {
        long s = this.selectedSlots.size() * this.box.getBuyCostPerTransaction(); // кол-во товаров
        long tax = (long)Math.ceil(s * this.box.getAccount().getCurrency().getTransactionPercent() / 100.0);
        addText(x, y, Text.literal("Активный счет: " + selectedAccount.getName()));
        addText(x, y, Text.literal("Баланс: " + selectedAccount.getBalance() + " ").append(selectedAccount.getCurrency().getShortName()));
        addText(x, y, Text.literal("Сумма: ").append(Text.literal(s + "+" + tax + " ").setStyle(Style.EMPTY.withColor(0xff0000))).append(box.getAccount().getCurrency().getShortName()));
        addText(x, y, Text.literal("Остаток: ").append(Text.literal((selectedAccount.getBalance() - (s + tax)) + " ").setStyle(Style.EMPTY.withColor(0x34c924))).append(selectedAccount.getCurrency().getShortName()));
        widgets.add(ButtonWidget.builder(Text.literal("message"), this::onChangeAccount).dimensions(x[0], y[0], 80, 20).build());
        widgets.add(ButtonWidget.builder(Text.literal("⟳"), this::onRefreshBalance).dimensions(x[0] + 81, y[0], 20, 20).build());
        y[0] += 20;
        buyButton.setPosition(x[0], y[0]);
        sellButton.setPosition(x[0] + 51, y[0]);

        if(
                box.getBoxType() != KtsBoxType.BUY_ONLY &&
                box.getBoxType() != KtsBoxType.BUY_SELL &&
                box.getBoxType() != KtsBoxType.BUY_SELL_CONFIRM
        ) buyButton.active = false;

        if(
                box.getBoxType() != KtsBoxType.BUY_SELL &&
                box.getBoxType() != KtsBoxType.BUY_SELL_CONFIRM &&
                box.getBoxType() != KtsBoxType.SELL_CONFIRM_ONLY &&
                box.getBoxType() != KtsBoxType.SELL_ONLY
        ) sellButton.active = false;

        widgets.add(buyButton);
        widgets.add(sellButton);
        y[0] += 20;
        viewError.setX(x[0]);
        viewError.setY(y[0]);
        widgets.add(viewError);
    }

    public void initProducerPanel(int x, int y) {
        blockBox.setPosition(x, y);
        updateButton.setPosition(x + 20, y);
        lockButton.setPosition(x + 40, y);
        y += 20 + 1;
        costEntry.setPosition(x - 8, y);
        y += 16 + 1;
        countEntry.setPosition(x - 8, y);
        widgets.addAll(List.of(blockBox, updateButton, lockButton, costEntry, countEntry));
    }

    public void close() {

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
    public boolean clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player) {
        if(!blocked) return false;
        if(slotId < 0 || slotId > 63) return false;
        if(slotId > 26) return true;


        Slot s = this.screen.getScreenHandler().getSlot(slotId);

        s.getStack().removeSubNbt(ItemStack.DISPLAY_KEY);
        if(ItemStack.areEqual(s.getStack(), item)) {
            if (this.selectedSlots.contains(slotId)) {
                this.selectedSlots.remove(slotId);
            } else {
                this.selectedSlots.add(slotId);
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

    public void onBuy(ButtonWidget b) {
        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
        KbErr e = service.buyInBox(KtsBkApiProvider.INSTANCE.auth(), this.selectedAccount.getId(), this.box.getId(), this.selectedSlots.size());
        if(e != KbErr.SUCCESS) {
            viewError.setMessage(Text.translatable(e.translatable()));
            return;
        } else {
            viewError.setMessage(Text.translatable(e.translatable()));
        }

        this.blocked = false;
        if(MinecraftClient.getInstance().interactionManager == null) return;

        this.box.setCountNow(this.box.getCountNow() - this.selectedSlots.size());

        for(Integer i : this.selectedSlots) {
            MinecraftClient.getInstance().interactionManager.clickSlot(
                    this.screen.getScreenHandler().syncId,
                    i,
                    1,
                    SlotActionType.QUICK_MOVE,
                    MinecraftClient.getInstance().player
            );
        }
        this.blocked = true;
        this.selectedSlots.clear();
    }

    public void onUpdateCount(ButtonWidget b) {
        long c = 0, t = 0;
        for(Slot s : screen.getScreenHandler().slots) {
            if(t > 26) break;
            ItemStack st = s.getStack();
            st.removeSubNbt(ItemStack.DISPLAY_KEY);
            if(ItemStack.areEqual(st, item)) c++;
            t++;
        }

        KtsBkServiceC2S service = KtsBkApiProvider.INSTANCE.getCacheService();
        Long count;
        Long cost;
        try {
            count = countEntry.getText().isEmpty() ? null : Long.parseLong(countEntry.getText());
            countEntry.setText("");
        } catch (NumberFormatException e) {
            viewError.setMessage(Text.translatable(KbErr.ILLEGAL_COUNT.translatable()));
            return;
        }

        try {
            cost = costEntry.getText().isEmpty() ? null : Long.parseLong(costEntry.getText());
            costEntry.setText("");
        } catch (NumberFormatException e) {
            viewError.setMessage(Text.translatable(KbErr.ILLEGAL_COST.translatable()));
            return;
        }

        KbErr lastError = null;//service.updateBoxData(null, box.getId(), c, count, cost);
        if(lastError == KbErr.SUCCESS) {
            box.setCountNow(c);
            if(cost != null) box.setBuyCostPerTransaction(cost);
            if(count != null) {
                box.setCountPerTransaction(count);
                item.setCount(count.intValue());
                onUpdateCount(b);
            }
        }
        viewError.setMessage(Text.translatable(lastError.translatable()));
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
                    this.buyButton.active = true;
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
                }
            } catch (WebsocketNotConnectedException | UndeclaredThrowableException e) {
                r = KbErr.CONNECTION_ERROR;
            }
        }
        viewError.setMessage(Text.translatable(r.translatable()));
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

}
