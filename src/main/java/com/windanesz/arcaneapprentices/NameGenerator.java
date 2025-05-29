package com.windanesz.arcaneapprentices;

import com.google.gson.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Name generator for wizard NPCs, handling both procedural name generation and configured names.
 * Names can be configured via JSON files or through code.
 *
 * @author windanesz
 */
public class NameGenerator {
    // Static instance of the name generator
    private static NameGenerator INSTANCE;
    
    // Map of name generators by type
    private final Map<String, NameSet> nameSets = new HashMap<>();
    
    // Config settings and names
    private float configNameChance = 0.5f;
    private String[] configNames = new String[0];
    
    /**
     * Private constructor - use getInstance() instead
     */
    private NameGenerator() {}

    /**
     * Gets the singleton instance of NameGenerator and ensures it's initialized
     *
     * @return The singleton instance
     */
    public static NameGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameGenerator();
            INSTANCE.initialize();
        }
        return INSTANCE;
    }

    /**
     * Initializes the name generator with name sets from JSON
     */
    public void initialize() {
        if (!nameSets.isEmpty()) return; // Already initialized
        loadNameSetsFromJson();
    }

    /**
     * Load name sets from JSON files (either config file or internal resource)
     */
    private void loadNameSetsFromJson() {
        try {
            // Check for external config file first
            File configFile = new File(Loader.instance().getConfigDir(), "arcaneapprentices/names.json");
            boolean loaded = false;
            
            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    parseJson(reader);
                    ArcaneApprentices.logger.info("Loaded name sets from config file");
                    loaded = true;
                }
            }
            
            // Fall back to internal resource if needed
            if (!loaded) {
                try (InputStream internalResource = getClass().getClassLoader().getResourceAsStream("assets/arcaneapprentices/names.json")) {
                    if (internalResource != null) {
                        try (InputStreamReader reader = new InputStreamReader(internalResource, StandardCharsets.UTF_8)) {
                            parseJson(reader);
                            ArcaneApprentices.logger.info("Loaded name sets from internal resource");
                        }
                    }
                }
                
                // Create default config for users
                createDefaultNameConfig();
            }
        } catch (Exception e) {
            ArcaneApprentices.logger.error("Failed to load name sets from JSON", e);
        }
    }

    /**
     * Parse JSON from reader into name sets
     */
    private void parseJson(Reader reader) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(reader).getAsJsonObject();

        // Load settings
        JsonObject settings = json.has("settings") ? json.getAsJsonObject("settings") : null;
        if (settings != null && settings.has("configNameChance")) {
            configNameChance = settings.get("configNameChance").getAsFloat();
        }

        // Load name sets
        if (json.has("nameSets")) {
            JsonObject nameSetsJson = json.getAsJsonObject("nameSets");
            for (Map.Entry<String, JsonElement> entry : nameSetsJson.entrySet()) {
                JsonObject nameSetJson = entry.getValue().getAsJsonObject();
                nameSets.put(entry.getKey(), new NameSet(
                    getJsonStringArray(nameSetJson, "prefixes"),
                    getJsonStringArray(nameSetJson, "middles"), 
                    getJsonStringArray(nameSetJson, "suffixes")
                ));
            }
        }
        
        // Load config names
        if (json.has("configNames")) {
            List<String> names = new ArrayList<>();
            json.getAsJsonArray("configNames").forEach(e -> names.add(e.getAsString()));
            configNames = names.toArray(new String[0]);
        }
    }

    /**
     * Helper method to extract a string array from JSON
     */
    private String[] getJsonStringArray(JsonObject json, String key) {
        if (!json.has(key)) return new String[0];
        
        JsonArray array = json.getAsJsonArray(key);
        String[] result = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).getAsString();
        }
        return result;
    }

    /**
     * Creates a default names.json file in the config directory
     */
    private void createDefaultNameConfig() {
        try {
            File configDir = new File(Loader.instance().getConfigDir(), "arcaneapprentices");
            if (!configDir.exists() && !configDir.mkdirs()) {
                ArcaneApprentices.logger.error("Failed to create config directory");
                return;
            }

            File namesFile = new File(configDir, "names.json");
            if (namesFile.exists()) return;

            // Create JSON structure
            JsonObject root = new JsonObject();
            
            // Settings
            JsonObject settings = new JsonObject();
            settings.addProperty("configNameChance", configNameChance);
            root.add("settings", settings);
            
            // Name sets
            JsonObject nameSetsJson = new JsonObject();
            for (Map.Entry<String, NameSet> entry : nameSets.entrySet()) {
                JsonObject nameSetJson = new JsonObject();
                addJsonArray(nameSetJson, "prefixes", entry.getValue().prefixes);
                addJsonArray(nameSetJson, "middles", entry.getValue().middles);
                addJsonArray(nameSetJson, "suffixes", entry.getValue().suffixes);
                nameSetsJson.add(entry.getKey(), nameSetJson);
            }
            root.add("nameSets", nameSetsJson);
            
            // Config names
            root.add("configNames", new JsonArray());

            // Write to file
            try (FileWriter writer = new FileWriter(namesFile)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
                ArcaneApprentices.logger.info("Created default name config at: " + namesFile.getAbsolutePath());
            }
        } catch (IOException e) {
            ArcaneApprentices.logger.error("Failed to create default name config", e);
        }
    }
    
    /**
     * Helper to add a string array to JSON
     */
    private void addJsonArray(JsonObject json, String key, String[] values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        json.add(key, array);
    }

    /**
     * Gets a random wizard name
     *
     * @param world The Minecraft world
     * @return A random wizard name
     */
    public static String getRandomWizardName(World world) {
        return getInstance().generateRandomName(world);
    }

    /**
     * Gets a random wizard name
     *
     * @param world The Minecraft world
     * @return A random wizard name
     */
    public String generateRandomName(World world) {
        if (world.rand.nextFloat() < configNameChance && configNames.length > 0) {
            return configNames[world.rand.nextInt(configNames.length)];
        } else {
            // Use procedural generation
            if (nameSets.isEmpty()) return "Wizard"; // Fallback name
            
            String[] types = nameSets.keySet().toArray(new String[0]);
            String type = types[world.rand.nextInt(types.length)];
            return nameSets.get(type).generateName(world.rand);
        }
    }

    /**
     * Inner class representing a set of name components
     */
    private static class NameSet {
        private final String[] prefixes;
        private final String[] middles;
        private final String[] suffixes;

        public NameSet(String[] prefixes, String[] middles, String[] suffixes) {
            this.prefixes = prefixes;
            this.middles = middles;
            this.suffixes = suffixes;
        }

        /**
         * Generate a random name using this name set
         */
        public String generateName(Random random) {
            if (prefixes.length == 0 || middles.length == 0 || suffixes.length == 0) {
                return "Dorian"; // Fallback name if any part is empty
            }
            
            String name = prefixes[random.nextInt(prefixes.length)] +
                         middles[random.nextInt(middles.length)] +
                         suffixes[random.nextInt(suffixes.length)];
                         
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }
}
