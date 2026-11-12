package net.zylo.balance;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZyloBalanceClient implements ClientModInitializer {
    private static final Pattern BALANCE_LINE = Pattern.compile(
        "(?i)(?:balance|bal|money|cash|purse|wallet)\\D{0,35}([$€£]?\\s*[0-9][0-9,._]*(?:\\.[0-9]+)?\\s*(?:k|m|b|t|thousand|million|billion|trillion)?)"
    );

    private static KeyMapping openScreenKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
            ResourceLocation.fromNamespaceAndPath("zylo_balance_tracker", "main")
        );
        openScreenKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.zylo_balance_tracker.open",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_RIGHT_SHIFT,
            category
        ));

        BalanceStore.load();

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            String plain = message.getString();
            Matcher matcher = BALANCE_LINE.matcher(plain);
            if (matcher.find()) {
                try {
                    BigDecimal parsed = parseAmount(matcher.group(1));
                    if (parsed != null) BalanceStore.update(parsed);
                } catch (NumberFormatException ignored) {
                    // Ignore chat lines that resemble a balance but are not numeric.
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openScreenKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new BalanceScreen());
                }
            }
        });
    }

    private static BigDecimal parseAmount(String input) {
        String s = input.toLowerCase(Locale.ROOT)
            .replace("$", "").replace("€", "").replace("£", "")
            .replace(",", "").replace("_", "").replace(" ", "");
        BigDecimal multiplier = BigDecimal.ONE;
        if (s.endsWith("trillion") || s.endsWith("t")) {
            multiplier = new BigDecimal("1000000000000");
            s = s.replaceAll("(trillion|t)$", "");
        } else if (s.endsWith("billion") || s.endsWith("b")) {
            multiplier = new BigDecimal("1000000000");
            s = s.replaceAll("(billion|b)$", "");
        } else if (s.endsWith("million") || s.endsWith("m")) {
            multiplier = new BigDecimal("1000000");
            s = s.replaceAll("(million|m)$", "");
        } else if (s.endsWith("thousand") || s.endsWith("k")) {
            multiplier = new BigDecimal("1000");
            s = s.replaceAll("(thousand|k)$", "");
        }
        if (s.isBlank()) return null;
        return new BigDecimal(s).multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
    }
}
