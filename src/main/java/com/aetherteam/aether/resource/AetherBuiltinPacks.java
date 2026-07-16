package com.aetherteam.aether.resource;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.flag.FeatureFlagSet;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class AetherBuiltinPacks {
    static final List<BuiltinPackDefinition> DEFINITIONS = List.of(
            new BuiltinPackDefinition("builtin/aether_125_art", PackType.CLIENT_RESOURCES,
                    List.of("classic_125", "classic_base"), "pack.aether.125.title", "pack.aether.125.description",
                    Availability.ALWAYS, Activation.NORMAL),
            new BuiltinPackDefinition("builtin/aether_b173_art", PackType.CLIENT_RESOURCES,
                    List.of("classic_b173", "classic_base"), "pack.aether.b173.title", "pack.aether.b173.description",
                    Availability.ALWAYS, Activation.NORMAL),
            new BuiltinPackDefinition("builtin/aether_ctm_fix", PackType.CLIENT_RESOURCES,
                    List.of("ctm_fix"), "pack.aether.ctm.title", "pack.aether.ctm.description",
                    Availability.CTM, Activation.REQUIRED),
            new BuiltinPackDefinition("builtin/aether_tips", PackType.CLIENT_RESOURCES,
                    List.of("tips"), "pack.aether.tips.title", "pack.aether.tips.description",
                    Availability.TIPS, Activation.NORMAL),
            new BuiltinPackDefinition("builtin/aether_colorblind", PackType.CLIENT_RESOURCES,
                    List.of("colorblind"), "pack.aether.colorblind.title", "pack.aether.colorblind.description",
                    Availability.ALWAYS, Activation.NORMAL),
            new BuiltinPackDefinition("builtin/aether_tooltips", PackType.CLIENT_RESOURCES,
                    List.of("tooltips"), "pack.aether.tooltips.title", "pack.aether.tooltips.description",
                    Availability.ALWAYS, Activation.NORMAL),
            new BuiltinPackDefinition("builtin/aether_imm_ptl_compat", PackType.SERVER_DATA,
                    List.of("imm_ptl_compat"), "pack.aether.imm_ptl_compat.title", "pack.aether.imm_ptl_compat.description",
                    Availability.IMMERSIVE_PORTALS, Activation.REQUIRED),
            new BuiltinPackDefinition("builtin/aether_temporary_freezing", PackType.SERVER_DATA,
                    List.of("temporary_freezing"), "pack.aether.freezing.title", "pack.aether.freezing.description",
                    Availability.ALWAYS, Activation.TEMPORARY_FREEZING_CONFIG),
            new BuiltinPackDefinition("builtin/aether_ruined_portal", PackType.SERVER_DATA,
                    List.of("ruined_portal"), "pack.aether.ruined_portal.title", "pack.aether.ruined_portal.description",
                    Availability.ALWAYS, Activation.RUINED_PORTAL_CONFIG)
    );

    private AetherBuiltinPacks() {
    }

    public static RepositorySource repositorySource(PackType packType) {
        return consumer -> {
            PackContext context = currentContext();
            ModContainer mod = FabricLoader.getInstance().getModContainer(Aether.MODID)
                    .orElseThrow(() -> new IllegalStateException("Missing Aether mod container"));
            for (BuiltinPackDefinition definition : availableDefinitions(packType, context)) {
                consumer.accept(createPack(mod, definition, context));
            }
        };
    }

    static List<BuiltinPackDefinition> definitions() {
        return DEFINITIONS;
    }

    static List<BuiltinPackDefinition> availableDefinitions(PackType packType, PackContext context) {
        return DEFINITIONS.stream()
                .filter(definition -> definition.packType() == packType && definition.isAvailable(context))
                .toList();
    }

    private static PackContext currentContext() {
        FabricLoader loader = FabricLoader.getInstance();
        return new PackContext(loader::isModLoaded,
                AetherConfig.STARTUP.enable_trivia.get(),
                AetherConfig.COMMON.enable_immersive_portals_compatibility.get(),
                AetherConfig.COMMON.add_temporary_freezing_automatically.get(),
                AetherConfig.COMMON.add_ruined_portal_automatically.get());
    }

    private static Pack createPack(ModContainer mod, BuiltinPackDefinition definition, PackContext context) {
        PackSource source = PackSource.create(PackSource.BUILT_IN::decorate,
                definition.shouldAddAutomatically(context));
        PackLocationInfo location = new PackLocationInfo(definition.id(),
                Component.translatable(definition.titleKey()), source, Optional.empty());
        List<Path> paths = definition.directories().stream()
                .map(directory -> findPackPath(mod, directory))
                .toList();

        Pack.ResourcesSupplier resources;
        if (paths.size() == 1) {
            resources = new PathPackResources.PathResourcesSupplier(paths.getFirst());
        } else {
            PackFormat format = SharedConstants.getCurrentVersion().packVersion(definition.packType());
            PackMetadataSection metadata = new PackMetadataSection(Component.translatable(definition.descriptionKey()),
                    new InclusiveRange<>(format, format));
            resources = new CombinedPackResources.CombinedResourcesSupplier(metadata, paths, paths.getFirst());
        }

        Pack.Metadata metadata = new Pack.Metadata(Component.translatable(definition.descriptionKey()),
                PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
        PackSelectionConfig selection = new PackSelectionConfig(definition.isRequired(), Pack.Position.TOP, false);
        return new Pack(location, resources, metadata, selection);
    }

    private static Path findPackPath(ModContainer mod, String directory) {
        return mod.findPath("packs/" + directory)
                .orElseThrow(() -> new IllegalStateException("Missing Aether built-in pack directory: " + directory));
    }

    enum Availability {
        ALWAYS,
        CTM,
        TIPS,
        IMMERSIVE_PORTALS
    }

    enum Activation {
        NORMAL,
        REQUIRED,
        TEMPORARY_FREEZING_CONFIG,
        RUINED_PORTAL_CONFIG
    }

    record PackContext(Predicate<String> isModLoaded, boolean triviaEnabled,
                       boolean immersivePortalsCompatibilityEnabled, boolean temporaryFreezingAutoEnabled,
                       boolean ruinedPortalAutoEnabled) {
        boolean isModLoaded(String modId) {
            return this.isModLoaded.test(modId);
        }
    }

    record BuiltinPackDefinition(String id, PackType packType, List<String> directories, String titleKey,
                                 String descriptionKey, Availability availability, Activation activation) {
        boolean isAvailable(PackContext context) {
            return switch (this.availability) {
                case ALWAYS -> true;
                case CTM -> context.isModLoaded("ctm");
                case TIPS -> context.isModLoaded("tipsmod") && context.triviaEnabled();
                case IMMERSIVE_PORTALS -> context.isModLoaded("immersive_portals_core")
                        && context.immersivePortalsCompatibilityEnabled();
            };
        }

        boolean isRequired() {
            return this.activation == Activation.REQUIRED;
        }

        boolean shouldAddAutomatically(PackContext context) {
            return switch (this.activation) {
                case TEMPORARY_FREEZING_CONFIG -> context.temporaryFreezingAutoEnabled();
                case RUINED_PORTAL_CONFIG -> context.ruinedPortalAutoEnabled();
                default -> true;
            };
        }
    }
}
