package net.zylo.balance;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BalanceScreen extends Screen {
    private static final DecimalFormat NUMBER = new DecimalFormat("#,##0.##",
        DecimalFormatSymbols.getInstance(Locale.US));

    public BalanceScreen() {
        super(Component.literal("Zylo Balance Tracker"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);

        int panelW = 300;
        int panelH = 190;
        int left = (this.width - panelW) / 2;
        int top = (this.height - panelH) / 2;

        g.fill(left, top, left + panelW, top + panelH, 0xF0181D2B);
        g.fill(left, top, left + panelW, top + 3, 0xFF38BDF8);
        g.fill(left + 14, top + 16, left + panelW - 14, top + 17, 0xFF344256);

        g.drawCenteredString(this.font, Component.literal("ZYLO BALANCE"), this.width / 2, top + 12, 0xFFFFFFFF);
        g.drawCenteredString(this.font, Component.literal("YOUR CURRENT BALANCE"), this.width / 2, top + 34, 0xFF9CA3AF);

        BigDecimal current = BalanceStore.current();
        String balance = current == null ? "Not recorded yet" : "$" + NUMBER.format(current);
        g.drawCenteredString(this.font, Component.literal(balance), this.width / 2, top + 55,
            current == null ? 0xFFFFC857 : 0xFF4ADE80);

        String previous = BalanceStore.previous() == null ? "—" : "$" + NUMBER.format(BalanceStore.previous());
        g.drawString(this.font, Component.literal("Previous balance"), left + 20, top + 91, 0xFFB8C2D1);
        g.drawString(this.font, Component.literal(previous), left + 164, top + 91, 0xFFFFFFFF);

        g.drawString(this.font, Component.literal("Total gained"), left + 20, top + 111, 0xFFB8C2D1);
        g.drawString(this.font, Component.literal("$" + NUMBER.format(BalanceStore.gained())), left + 164, top + 111, 0xFF4ADE80);

        g.drawString(this.font, Component.literal("Total spent"), left + 20, top + 131, 0xFFB8C2D1);
        g.drawString(this.font, Component.literal("$" + NUMBER.format(BalanceStore.spent())), left + 164, top + 131, 0xFFFF7777);

        g.drawCenteredString(this.font, Component.literal("Updates when your balance appears in chat"),
            this.width / 2, top + 160, 0xFF9CA3AF);
        g.drawCenteredString(this.font, Component.literal("Press ESC to close  •  Right Shift to reopen"),
            this.width / 2, top + 174, 0xFF7C8A9D);

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
