package com.aetherteam.aether.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.InclusiveRange;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombinedPackResourcesTest {
    @Test
    void classicPackReadsReleaseSpecificAndSharedAssets() throws Exception {
        Path releasePath = Path.of("src/main/resources/packs/classic_125");
        Path basePath = Path.of("src/main/resources/packs/classic_base");
        PackLocationInfo location = new PackLocationInfo("builtin/aether_125_art", Component.literal("test"),
                PackSource.BUILT_IN, Optional.empty());
        PackFormat format = PackFormat.of(1);
        PackMetadataSection metadata = new PackMetadataSection(Component.literal("test"),
                new InclusiveRange<>(format, format));

        try (CombinedPackResources resources = new CombinedPackResources(location, metadata,
                List.of(new PathPackResources(location, releasePath), new PathPackResources(location, basePath)),
                releasePath)) {
            assertTrue(resources.getNamespaces(PackType.CLIENT_RESOURCES).contains("aether"));
            assertResourceEquals(resources, releasePath,
                    "textures/item/armor/phoenix_boots.png");
            assertResourceEquals(resources, basePath,
                    "textures/models/armor/gravitite_layer_1.png");
        }
    }

    private static void assertResourceEquals(CombinedPackResources resources, Path source, String resourcePath)
            throws Exception {
        var resource = resources.getResource(PackType.CLIENT_RESOURCES,
                Identifier.fromNamespaceAndPath("aether", resourcePath));
        assertNotNull(resource, resourcePath);
        try (var input = resource.get()) {
            assertArrayEquals(Files.readAllBytes(source.resolve("assets/aether").resolve(resourcePath)),
                    input.readAllBytes());
        }
    }
}
