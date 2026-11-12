package net.zylo.balance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BalanceStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static BigDecimal current = null;
    private static BigDecimal previous = null;
    private static BigDecimal gained = BigDecimal.ZERO;
    private static BigDecimal spent = BigDecimal.ZERO;

    private BalanceStore() {}

    public static synchronized void update(BigDecimal next) {
        if (current != null) {
            BigDecimal delta = next.subtract(current);
            if (delta.signum() > 0) gained = gained.add(delta);
            if (delta.signum() < 0) spent = spent.add(delta.abs());
            previous = current;
        }
        current = next;
        save();
    }

    public static synchronized BigDecimal current() { return current; }
    public static synchronized BigDecimal previous() { return previous; }
    public static synchronized BigDecimal gained() { return gained; }
    public static synchronized BigDecimal spent() { return spent; }

    public static synchronized void load() {
        Path path = configPath();
        if (!Files.exists(path)) return;
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json == null) return;
            if (json.has("current")) current = new BigDecimal(json.get("current").getAsString());
            if (json.has("previous")) previous = new BigDecimal(json.get("previous").getAsString());
            if (json.has("gained")) gained = new BigDecimal(json.get("gained").getAsString());
            if (json.has("spent")) spent = new BigDecimal(json.get("spent").getAsString());
        } catch (Exception ignored) {}
    }

    private static synchronized void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            JsonObject json = new JsonObject();
            if (current != null) json.addProperty("current", current.toPlainString());
            if (previous != null) json.addProperty("previous", previous.toPlainString());
            json.addProperty("gained", gained.toPlainString());
            json.addProperty("spent", spent.toPlainString());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception ignored) {}
    }

    private static Path configPath() {
        return Minecraft.getInstance().gameDirectory.toPath()
            .resolve("config").resolve("zylo-balance-tracker.json");
    }
}
