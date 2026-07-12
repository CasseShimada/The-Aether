package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.item.accessories.abilities.AccessoryAbilityHooks;
import com.aetherteam.aether.item.tools.abilities.ToolAbilityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockPlayerDestroyMixin {
    // Fabric's after-break event does not expose the original tool stack required by the Holystone ability.
    @Inject(method = "playerDestroy", at = @At("TAIL"))
    private void aether$onPlayerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        if (!level.isClientSide()) {
            AccessoryAbilityHooks.damageZaniteRing(player, level, state, pos);
            AccessoryAbilityHooks.damageZanitePendant(player, level, state, pos);
            ToolAbilityHooks.handleHolystoneToolAbility(player, level, pos, stack, state);
        }
    }
}
