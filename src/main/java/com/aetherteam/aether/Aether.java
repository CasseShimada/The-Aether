package com.aetherteam.aether;

import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.api.AetherAdvancementSoundOverrides;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.AetherCauldronInteractions;
import com.aetherteam.aether.block.dispenser.AetherDispenseBehaviors;
import com.aetherteam.aether.block.dispenser.DispenseUsableItemBehavior;
import com.aetherteam.aether.block.dispenser.SkyrootBoatDispenseBehavior;
import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.TriviaGenerator;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.ai.attribute.AetherAttributes;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.inventory.AetherRecipeBookTypes;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.item.AetherCreativeTabs;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.combat.AetherArmorMaterials;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.loot.conditions.AetherLootConditions;
import com.aetherteam.aether.loot.functions.AetherLootFunctions;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.book.AetherRecipeBookCategories;
import com.aetherteam.aether.registry.DeferredRegister;
import com.aetherteam.aether.world.AetherPoi;
import com.aetherteam.aether.world.feature.AetherFeatures;
import com.aetherteam.aether.world.foliageplacer.AetherFoliagePlacerTypes;
import com.aetherteam.aether.world.placementmodifier.AetherPlacementModifiers;
import com.aetherteam.aether.world.processor.AetherStructureProcessors;
import com.aetherteam.aether.world.structure.AetherStructureTypes;
import com.aetherteam.aether.world.structurepiece.AetherStructurePieceTypes;
import com.aetherteam.aether.world.treedecorator.AetherTreeDecoratorTypes;
import com.aetherteam.aether.world.trunkplacer.AetherTrunkPlacerTypes;
import com.google.common.reflect.Reflection;
import com.mojang.logging.LogUtils;
import com.aetherteam.aether.accessories.api.slot.UniqueSlotHandling;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class Aether {
    public static final String MODID = "aether";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Path DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve(MODID);

    public static final TriviaGenerator TRIVIA_READER = new TriviaGenerator();

    private static boolean initialized;

    private Aether() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        DIRECTORY.toFile().mkdirs();

        Reflection.initialize(AetherDataAttachments.class);
        Reflection.initialize(AetherGameEvents.class);
        Reflection.initialize(AetherLootFunctions.class);
        Reflection.initialize(AetherLootConditions.class);
        Reflection.initialize(AetherDataComponents.class);
        Reflection.initialize(AetherRecipeBookCategories.class);
        Reflection.initialize(AetherRecipeTypes.class);
        Reflection.initialize(AetherRecipeSerializers.class);
        Reflection.initialize(AetherAttributes.class);
        Reflection.initialize(AetherEffects.class);
        Reflection.initialize(AetherParticleTypes.class);
        Reflection.initialize(AetherTreeDecoratorTypes.class);
        Reflection.initialize(AetherTrunkPlacerTypes.class);
        AetherBlocks.registerWoodTypes();
        registerContent();
        AetherCreativeTabs.registerVanillaTabEntries();
        AetherPoi.registerBlockStateMappings();
        AetherEntityTypes.registerEntityAttributes();
        AetherEntityTypes.registerSpawnPlacements();

        Reflection.initialize(AetherRecipeBookTypes.class);
        Reflection.initialize(AetherMobCategory.class);
        Reflection.initialize(AetherAdvancementTriggers.class);

        AetherBlocks.registerPots();
        AetherBlocks.registerFlammability();

        AetherItems.registerAccessories();
        AetherItems.setupBucketReplacements();

        registerDispenserBehaviors();
        registerCauldronInteractions();

        UniqueSlotHandling.EVENT.register(AetherAccessorySlots.INSTANCE);
    }

    private static void registerContent() {
        DeferredRegister<?>[] registers = {
                AetherBlocks.BLOCKS,
                AetherEntityTypes.ENTITY_TYPES,
                AetherSoundEvents.SOUNDS,
                AetherItems.ITEMS,
                AetherBlockEntityTypes.BLOCK_ENTITY_TYPES,
                AetherMenuTypes.MENU_TYPES,
                AetherFeatures.FEATURES,
                AetherFoliagePlacerTypes.FOLIAGE_PLACERS,
                AetherPlacementModifiers.PLACEMENT_MODIFIERS,
                AetherPoi.POI,
                AetherStructureTypes.STRUCTURE_TYPES,
                AetherStructurePieceTypes.STRUCTURE_PIECE_TYPES,
                AetherStructureProcessors.STRUCTURE_PROCESSOR_TYPES,
                AetherCreativeTabs.CREATIVE_MODE_TABS
        };

        for (DeferredRegister<?> register : registers) {
            register.register();
        }
    }

    private static void registerDispenserBehaviors() {
        SkyrootBoatDispenseBehavior.registerDispenserBehaviors();
        AetherDispenseBehaviors.registerDispenserBehaviors();
        DispenseUsableItemBehavior.registerDispenserBehaviors();
    }

    private static void registerCauldronInteractions() {
        AetherCauldronInteractions.registerCauldronInteractions();
    }
}
