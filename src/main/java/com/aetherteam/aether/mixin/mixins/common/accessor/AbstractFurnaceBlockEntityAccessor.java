package com.aetherteam.aether.mixin.mixins.common.accessor;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Accessor("quickCheck")
    RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> aether$getQuickCheck();

    @Accessor("litTimeRemaining")
    int aether$getLitTimeRemaining();

    @Accessor("litTimeRemaining")
    void aether$setLitTimeRemaining(int litTime);

    @Accessor("litTotalTime")
    void aether$setLitTotalTime(int litDuration);

    @Accessor("cookingTimer")
    int aether$getCookingTimer();

    @Accessor("cookingTimer")
    void aether$setCookingTimer(int cookingProgress);

    @Accessor("cookingTotalTime")
    int aether$getCookingTotalTime();

    @Accessor("cookingTotalTime")
    void aether$setCookingTotalTime(int cookingTotalTime);
}
