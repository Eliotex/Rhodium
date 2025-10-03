package net.eliotex.rhodium.config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class ConfigManager {
    private static final Logger LOGGER = LogManager.getLogger();
    public static volatile int minTPSforIncrease = 14;
    public static void loadFromOptions() {
        Path options = Paths.get(System.getProperty("user.dir"), "options.txt");
        boolean found = false;
        try (Stream<String> lines = Files.lines(options, StandardCharsets.UTF_8)) {
            for (String raw : (Iterable<String>) lines::iterator) {
                if (raw == null) continue;
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();

                if ("minTPSforIncrease".equalsIgnoreCase(key)) {
                    found = true;
                    try {
                        int parsed = Integer.parseInt(value);
                        if (parsed > 0 && parsed < 21) {
                            minTPSforIncrease = parsed;
                            LOGGER.info("Rhodium: using minTPSforIncrease={}", minTPSforIncrease);
                        } else {
                            LOGGER.warn("Rhodium: minTPSforIncrease invalid ({}) - using default {}", value, minTPSforIncrease);
                        }
                    } catch (NumberFormatException nfe) {
                        LOGGER.warn("Rhodium: Couldn't parse minTPSforIncrease {} — using default {}", value, minTPSforIncrease);
                    }
                }
            }
        } catch (IOException ioe) {
            LOGGER.warn("Rhodium: Error reading options.txt: {} — using default {}", ioe.getMessage(), minTPSforIncrease);
        }
        if (!found) {
            LOGGER.warn("Rhodium: No minTPSforIncrease in options.txt — using default {}", minTPSforIncrease);
        }
    }
}
