package com.aetherteam.aether.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AetherResourceIntegrityTest {
    private static final List<Path> RESOURCE_ROOTS = List.of(
            Path.of("src/main/resources"),
            Path.of("src/generated/resources"));
    private static final Pattern REGISTERED_SOUND = Pattern.compile("\\bregister\\(\"([a-z0-9_./-]+)\"");
    private static final Pattern REGISTERED_IDENTIFIER = Pattern.compile(
            "Identifier\\.fromNamespaceAndPath\\(Aether\\.MODID,\\s*\"([a-z0-9_./-]+)\"");

    @Test
    void allPackagedJsonIsWellFormed() throws IOException {
        List<String> failures = new ArrayList<>();
        int count = 0;
        for (Path root : RESOURCE_ROOTS) {
            try (Stream<Path> paths = Files.walk(root)) {
                for (Path json : paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .filter(path -> !path.toString().contains(".cache"))
                        .toList()) {
                    count++;
                    try {
                        readJson(json);
                    } catch (RuntimeException exception) {
                        failures.add(json + ": " + exception.getMessage());
                    }
                }
            }
        }
        assertTrue(count > 2_500, "unexpectedly small packaged JSON set: " + count);
        assertTrue(failures.isEmpty(), "malformed JSON resources: " + failures);
    }

    @Test
    void aetherModelsAndParticlesResolveLocalReferences() throws IOException {
        List<String> missing = new ArrayList<>();
        for (Path root : RESOURCE_ROOTS) {
            Path assets = root.resolve("assets/aether");
            if (!Files.isDirectory(assets)) {
                continue;
            }
            try (Stream<Path> paths = Files.walk(assets)) {
                for (Path json : paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .toList()) {
                    inspectReferences(readJson(json), json, false, missing);
                }
            }
        }
        assertTrue(missing.isEmpty(), "missing local Aether resource references: " + missing);
    }

    @Test
    void registeredSoundsAndParticlesHaveDefinitionsAndFiles() throws IOException {
        JsonObject sounds = readJson(Path.of("src/generated/resources/assets/aether/sounds.json")).getAsJsonObject();
        Set<String> registeredSounds = sourceIdentifiers(
                Path.of("src/main/java/com/aetherteam/aether/client/AetherSoundEvents.java"), REGISTERED_SOUND);
        assertEquals(registeredSounds, sounds.keySet(), "registered sound IDs must match sounds.json");

        List<String> missingSounds = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : sounds.entrySet()) {
            String event = entry.getKey();
            JsonElement definition = entry.getValue();
            for (JsonElement sound : definition.getAsJsonObject().getAsJsonArray("sounds")) {
                String name;
                String type = "file";
                if (sound.isJsonPrimitive()) {
                    name = sound.getAsString();
                } else {
                    JsonObject object = sound.getAsJsonObject();
                    name = object.get("name").getAsString();
                    if (object.has("type")) {
                        type = object.get("type").getAsString();
                    }
                }
                if (name.startsWith("aether:") && !type.equals("event")) {
                    requireResource("assets/aether/sounds/" + name.substring("aether:".length()) + ".ogg",
                            "sound event " + event, missingSounds);
                }
            }
        }
        assertTrue(missingSounds.isEmpty(), "missing Aether sound files: " + missingSounds);

        Set<String> registeredParticles = sourceIdentifiers(
                Path.of("src/main/java/com/aetherteam/aether/client/particle/AetherParticleTypes.java"),
                REGISTERED_IDENTIFIER);
        Set<String> particleDefinitions = new TreeSet<>();
        try (Stream<Path> paths = Files.list(Path.of("src/main/resources/assets/aether/particles"))) {
            paths.filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString().replaceFirst("\\.json$", ""))
                    .forEach(particleDefinitions::add);
        }
        assertEquals(registeredParticles, particleDefinitions,
                "registered particle IDs must match particle descriptions");
    }

    private static void inspectReferences(JsonElement element, Path source, boolean textures,
                                          List<String> missing) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement child : array) {
                if (textures && child.isJsonPrimitive()) {
                    requireTexture(child.getAsString(), source, true, missing);
                } else {
                    inspectReferences(child, source, textures, missing);
                }
            }
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String name = entry.getKey();
                JsonElement child = entry.getValue();
                if ((name.equals("parent") || name.equals("model") || name.equals("base"))
                        && child.isJsonPrimitive()) {
                    requireModel(child.getAsString(), source, missing);
                } else if (name.equals("textures")) {
                    inspectReferences(child, source, true, missing);
                } else if (textures && child.isJsonPrimitive()) {
                    requireTexture(child.getAsString(), source, false, missing);
                } else if (name.equals("file") && child.isJsonPrimitive()
                        && child.getAsString().startsWith("aether:")
                        && child.getAsString().endsWith(".png")) {
                    requireResource("assets/aether/textures/" + child.getAsString().substring("aether:".length()),
                            source.toString(), missing);
                } else {
                    inspectReferences(child, source, false, missing);
                }
            }
        }
    }

    private static void requireModel(String identifier, Path source, List<String> missing) {
        if (identifier.startsWith("aether:")) {
            requireResource("assets/aether/models/" + identifier.substring("aether:".length()) + ".json",
                    source.toString(), missing);
        }
    }

    private static void requireTexture(String identifier, Path source, boolean particle,
                                       List<String> missing) {
        if (identifier.startsWith("#") || !identifier.startsWith("aether:")) {
            return;
        }
        String directory = particle ? "particle/" : "";
        String relativePath = "assets/aether/textures/" + directory
                + identifier.substring("aether:".length()) + ".png";
        if (!resourceExists(relativePath) && (particle || !isGeneratedByAtlas(identifier))) {
            missing.add(source + " -> " + relativePath);
        }
    }

    private static void requireResource(String relativePath, String source, List<String> missing) {
        if (!resourceExists(relativePath)) {
            missing.add(source + " -> " + relativePath);
        }
    }

    private static boolean isGeneratedByAtlas(String identifier) {
        String spritePath = identifier.substring("aether:".length());
        Path atlasDirectory = Path.of("src/main/resources/assets/minecraft/atlases");
        try (Stream<Path> atlases = Files.list(atlasDirectory)) {
            for (Path atlas : atlases.filter(path -> path.toString().endsWith(".json")).toList()) {
                JsonArray sources = readJson(atlas).getAsJsonObject().getAsJsonArray("sources");
                for (JsonElement element : sources) {
                    JsonObject source = element.getAsJsonObject();
                    String type = source.get("type").getAsString();
                    if (type.endsWith("directory") && directorySourceExists(spritePath, source)) {
                        return true;
                    }
                    if (type.endsWith("paletted_permutations")
                            && paletteSourceExists(spritePath, source)) {
                        return true;
                    }
                }
            }
        } catch (IOException | RuntimeException ignored) {
            return false;
        }
        return false;
    }

    private static boolean directorySourceExists(String spritePath, JsonObject source) {
        String prefix = source.get("prefix").getAsString();
        if (!spritePath.startsWith(prefix)) {
            return false;
        }
        String sourcePath = source.get("source").getAsString() + "/"
                + spritePath.substring(prefix.length());
        return resourceExists("assets/aether/textures/" + sourcePath + ".png");
    }

    private static boolean paletteSourceExists(String spritePath, JsonObject source) {
        JsonObject permutations = source.getAsJsonObject("permutations");
        for (JsonElement texture : source.getAsJsonArray("textures")) {
            String identifier = texture.getAsString();
            if (!identifier.startsWith("aether:")) {
                continue;
            }
            String basePath = identifier.substring("aether:".length());
            for (String permutation : permutations.keySet()) {
                if (spritePath.equals(basePath + "_" + permutation)
                        && resourceExists("assets/aether/textures/" + basePath + ".png")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean resourceExists(String relativePath) {
        return RESOURCE_ROOTS.stream().anyMatch(root -> Files.isRegularFile(root.resolve(relativePath)));
    }

    private static Set<String> sourceIdentifiers(Path source, Pattern pattern) throws IOException {
        Matcher matcher = pattern.matcher(Files.readString(source));
        Set<String> identifiers = new TreeSet<>();
        while (matcher.find()) {
            identifiers.add(matcher.group(1));
        }
        return identifiers;
    }

    private static JsonElement readJson(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader);
        }
    }
}
