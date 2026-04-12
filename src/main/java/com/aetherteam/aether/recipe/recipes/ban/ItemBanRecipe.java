package com.aetherteam.aether.recipe.recipes.ban;

import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.PlacementBanRecipeSerializer;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ItemBanRecipe extends AbstractPlacementBanRecipe<ItemStack, Ingredient, SingleRecipeInput> {
    public ItemBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock, Ingredient ingredient) {
        super(AetherRecipeTypes.ITEM_PLACEMENT_BAN.get(), biome, bypassBlock, ingredient);
    }

    public ItemBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock) {
        this(biome, bypassBlock, Ingredient.of());
    }

    /**
     * Checks if the recipe matches the given parameters using {@link AbstractPlacementBanRecipe#matches(Level, BlockPos, Object)}.<br><br>
     * Then checks an event hook through {@link AetherEventDispatch#isItemPlacementBanned(LevelAccessor, BlockPos, ItemStack)}.<br><br>
     * Before calling {@link AetherEventDispatch#onPlacementSpawnParticles(LevelAccessor, BlockPos, Direction, ItemStack, BlockState)} to spawn particles on item ban.
     *
     * @param level          The {@link Level} the recipe is performed in.
     * @param pos            The {@link BlockPos} the recipe is performed at.
     * @param direction      The {@link Direction} face that is interacted with.
     * @param stack          The {@link ItemStack} being used that is being checked.
     * @param spawnParticles A {@link Boolean} for whether to spawn particles.
     * @return Whether the given {@link ItemStack} is banned from placement.
     */
    public boolean banItem(Level level, BlockPos pos, Direction direction, ItemStack stack, boolean spawnParticles) {
        if (this.matches(level, pos, stack)) {
            if (AetherEventDispatch.isItemPlacementBanned(level, pos, stack)) {
                if (spawnParticles) {
                    AetherEventDispatch.onPlacementSpawnParticles(level, pos, direction, stack, null);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public RecipeSerializer<ItemBanRecipe> getSerializer() {
        return AetherRecipeSerializers.ITEM_PLACEMENT_BAN.get();
    }

    public static final class Serializer {
        private static final MapCodec<ItemBanRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockStateRecipeUtil.KEY_CODEC.fieldOf("biome").forGetter(ItemBanRecipe::getBiome),
                BlockStateIngredient.CODEC.optionalFieldOf("bypass").forGetter(ItemBanRecipe::getBypassBlock),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemBanRecipe::getIngredient)
        ).apply(inst, ItemBanRecipe::new));

        private Serializer() {
        }

        public static RecipeSerializer<ItemBanRecipe> create() {
            return PlacementBanRecipeSerializer.create(CODEC, StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork));
        }

        private static ItemBanRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Either<ResourceKey<Biome>, TagKey<Biome>> biome = PlacementBanRecipeSerializer.readBiome(buffer);
            Optional<BlockStateIngredient> bypassBlock = PlacementBanRecipeSerializer.readBypass(buffer);
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new ItemBanRecipe(biome, bypassBlock, ingredient);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ItemBanRecipe recipe) {
            PlacementBanRecipeSerializer.writeBase(buffer, recipe);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        }
    }
}
