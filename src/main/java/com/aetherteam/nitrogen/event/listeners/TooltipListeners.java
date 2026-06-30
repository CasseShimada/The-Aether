package com.aetherteam.nitrogen.event.listeners;

import com.aetherteam.aether.client.ClientCompat;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TooltipListeners {
    private static final String NITROGEN_MOD_ID = "nitrogen_internals";
    private static boolean callbackRegistered;

    public static final Map<Holder.Reference<Item>, TooltipPredicate> PREDICATES = new HashMap<>();

    public static void onTooltipCreationLowPriority() {
        if (callbackRegistered) {
            return;
        }
        callbackRegistered = true;

        ItemTooltipCallback.EVENT.register((itemStack, context, tooltipType, itemTooltips) ->
                addAbilityTooltips(Minecraft.getInstance().player, itemStack, itemTooltips, context));
    }

    public static void addAbilityTooltips(Player player, ItemStack stack, List<Component> components, Item.TooltipContext context) {
        for (int i = 1; i <= 5; i++) {
            String key = stack.getItem().getDescriptionId() + "." + NITROGEN_MOD_ID + ".ability.tooltip." + i;
            if (ClientCompat.hasTranslation(key)) {
                Component component = Component.translatable(key);
                TooltipPredicate predicate = PREDICATES.get(stack.getItem().builtInRegistryHolder());
                if (predicate != null) {
                    component = predicate.override(player, stack, components, context, component);
                }
                components.add(i, component);
            }
        }
    }

    @FunctionalInterface
    public interface TooltipPredicate {
        Component override(Player player, ItemStack stack, List<Component> components, Item.TooltipContext context, Component component);
    }
}
