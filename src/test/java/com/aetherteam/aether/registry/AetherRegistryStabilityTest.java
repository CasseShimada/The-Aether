package com.aetherteam.aether.registry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Guards the stable identifiers declared by the built-in registry classes. The
 * snapshots were generated from the user's released {@code 26.1.2-fabric}
 * baseline before replacing the local DeferredRegister compatibility layer.
 */
class AetherRegistryStabilityTest {
    private static final Path SOURCE_ROOT = Path.of("src/main/java/com/aetherteam/aether");
    private static final Pattern REGISTER_CALL = Pattern.compile(
            "(?s)(?:\\bregister\\w*|\\.register)\\s*\\(\\s*\"([A-Za-z0-9_./-]+)\"");
    private static final Pattern IDENTIFIER_CALL = Pattern.compile(
            "Identifier\\.fromNamespaceAndPath\\(Aether\\.MODID,\\s*\"([A-Za-z0-9_./-]+)\"");

    private static final Map<String, Snapshot> RELEASED_26_1_2 = Map.ofEntries(
            Map.entry("AetherGameEvents.java", new Snapshot(1, "9a7591f366ad7d81500a86219aa63c33bfb13d55967631902c8f323fbd175ab5")),
            Map.entry("block/AetherBlocks.java", new Snapshot(131, "8afa75316032b0815a4c2477f4f7a3b44dc2f34cc3d0eb32d9de680088f188d5")),
            Map.entry("item/AetherItems.java", new Snapshot(168, "2dab3eaaca6f3192040ef3cbb83fbcd0f643ef28b05a4d45bc4940d577a327ef")),
            Map.entry("entity/AetherEntityTypes.java", new Snapshot(38, "1fb78676a3ca4343c9dcf9f57801d10d056d091e661d1534df6df98e7a65c609")),
            Map.entry("blockentity/AetherBlockEntityTypes.java", new Snapshot(11, "5f5a9898c6bccdba8b1035833943280787b54318b8eeb89673c936dc2ad632ef")),
            Map.entry("inventory/menu/AetherMenuTypes.java", new Snapshot(5, "78923cba909272c608be76b68ae4b2e5e5fd9b9b8f12360330c025948fc581d3")),
            Map.entry("recipe/AetherRecipeTypes.java", new Snapshot(10, "965e073e1a637d4d1623660041d827c3db820bf7fcc8cd39df2e8556b80b5749")),
            Map.entry("recipe/AetherRecipeSerializers.java", new Snapshot(11, "a664fa2501b3f4850bc84f09f66a27fc55b2ab130b1bc699ca40e9cbb9701af9")),
            Map.entry("recipe/book/AetherRecipeBookCategories.java", new Snapshot(7, "ed8be5b2837286bd1ce778dad08f173c775dc0c9d6b2c967d2ea26ef9408bb5f")),
            Map.entry("client/particle/AetherParticleTypes.java", new Snapshot(9, "0be3b82524231640d9f4fc298b1475a0258f0b088bb759cf4d979c7bca92bf14")),
            Map.entry("client/AetherSoundEvents.java", new Snapshot(129, "aad30a18575088526b086882f17dbcb2edff9df22b9be02c0ffec0e54469c78e")),
            Map.entry("effect/AetherEffects.java", new Snapshot(2, "2e29898b9f707c319adc17277fde0999f11ba0212f4fd2b07ad2f210c2e85325")),
            Map.entry("item/AetherCreativeTabs.java", new Snapshot(10, "95bdfc5aac2c8abd61243149a3e31c053b13df530d438bbc8935906469fafd6e")),
            Map.entry("entity/ai/attribute/AetherAttributes.java", new Snapshot(1, "b3dd7f5581f93c7589b4913cce1bc18844bb081c02602c3cf0d40fdf9f57ae7a")),
            Map.entry("item/components/AetherDataComponents.java", new Snapshot(2, "c18944d5a37e81103f79d5818824386dc33cffd419aa60b5b076f7fc53de1a2b")),
            Map.entry("loot/conditions/AetherLootConditions.java", new Snapshot(1, "a8618d646c4af40f35e3028c25df6c4852f2c9c61bc51669936c29b570879788")),
            Map.entry("loot/functions/AetherLootFunctions.java", new Snapshot(4, "481630a57984e8a7e56daf698bc6419ef0799efba7b5953cbd0d979f5267bd44")),
            Map.entry("advancement/AetherAdvancementTriggers.java", new Snapshot(2, "57d5e268bad236014e0e60af9025aff965b47397619d84a6d73aefc773c5402d")),
            Map.entry("world/AetherPoi.java", new Snapshot(1, "a157152fe56346b99b060f523093f3bb01976825135cdaf8a73e9b7c533228ef")),
            Map.entry("world/feature/AetherFeatures.java", new Snapshot(4, "e46f8e73f31277538869a88092e6d8a8ece3769da2d127d1cf164cec88808f9d")),
            Map.entry("world/foliageplacer/AetherFoliagePlacerTypes.java", new Snapshot(3, "1409260c4ddd889f38dc5e8ea0e1d3c4d7eef0c782d5248e92d2e7ef12aa3bbe")),
            Map.entry("world/placementmodifier/AetherPlacementModifiers.java", new Snapshot(4, "5b5f1f44bf880aee4f46bfdece3d12b768ce24cf90aa59bc02982ba7e561dfb8")),
            Map.entry("world/processor/AetherPosRuleTests.java", new Snapshot(1, "5ed0cafdb1d54d639de1b4e0f8a0064e4e0ba5cd509ed801127add66e3abb0e0")),
            Map.entry("world/processor/AetherStructureProcessors.java", new Snapshot(7, "a0c4189a2c713c321b81416b5fb15f60bda2ba9351041787047bc00409579cb2")),
            Map.entry("world/structure/AetherStructureTypes.java", new Snapshot(5, "e98948044272349670d6139435bb0442b3e40aa499a937db80a7a09a040a2b37")),
            Map.entry("world/structurepiece/AetherStructurePieceTypes.java", new Snapshot(15, "c9cb1e1f4f337a3afd523d4ecd262b9da959e3515421eb555872c5431e0cf064")),
            Map.entry("world/treedecorator/AetherTreeDecoratorTypes.java", new Snapshot(1, "a932cfaf7182f94afb62a7a2ab3d4a70424b200240cf7ceda6a667c355e407ef")),
            Map.entry("world/trunkplacer/AetherTrunkPlacerTypes.java", new Snapshot(2, "04a5318c43ea06c8105891c354161b6da5fc02716fe2d03c434fab75f21fe3ab"))
    );

    @Test
    void nativeRegistryDeclarationsKeepReleasedIdentifiers() {
        List<Executable> assertions = new ArrayList<>();
        RELEASED_26_1_2.forEach((relativePath, expected) -> assertions.add(() -> {
            TreeSet<String> identifiers = identifiersIn(SOURCE_ROOT.resolve(relativePath), relativePath);
            assertEquals(expected.count(), identifiers.size(), relativePath + " identifiers: " + identifiers);
            assertEquals(expected.sha256(), sha256(String.join("\n", identifiers)),
                    relativePath + " identifiers: " + identifiers);
        }));
        assertAll(assertions);
    }

    private static TreeSet<String> identifiersIn(Path source, String relativePath) throws IOException {
        String text = Files.readString(source);
        TreeSet<String> identifiers = new TreeSet<>();
        collect(REGISTER_CALL.matcher(text), identifiers, relativePath);
        collect(IDENTIFIER_CALL.matcher(text), identifiers, relativePath);
        return identifiers;
    }

    private static void collect(Matcher matcher, TreeSet<String> identifiers, String relativePath) {
        while (matcher.find()) {
            String identifier = matcher.group(1);
            if (relativePath.endsWith("AetherStructurePieceTypes.java")) {
                identifier = identifier.toLowerCase(Locale.ROOT);
            }
            identifiers.add(identifier);
        }
    }

    private static String sha256(String value) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }

    private record Snapshot(int count, String sha256) {
    }
}
