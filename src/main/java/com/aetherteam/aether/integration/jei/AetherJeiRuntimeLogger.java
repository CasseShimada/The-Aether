package com.aetherteam.aether.integration.jei;

import com.aetherteam.aether.Aether;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

final class AetherJeiRuntimeLogger {
    private AetherJeiRuntimeLogger() {
    }

    static void logRuntimeAvailability(Logger logger, IJeiRuntime jeiRuntime) {
        Set<Identifier> jeiItems = jeiRuntime.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                .map(ItemStack::getItem)
                .map(BuiltInRegistries.ITEM::getKey)
                .filter(AetherJeiRuntimeLogger::isAetherItem)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
        Collection<ItemStack> filteredItems = jeiRuntime.getIngredientFilter().getFilteredIngredients(VanillaTypes.ITEM_STACK);
        List<ItemStack> visibleItems = jeiRuntime.getIngredientListOverlay().getVisibleIngredients(VanillaTypes.ITEM_STACK);
        long filteredAetherItems = countAetherItems(filteredItems);
        long visibleAetherItems = countAetherItems(visibleItems);
        List<Identifier> missingItems = BuiltInRegistries.ITEM.stream()
                .map(BuiltInRegistries.ITEM::getKey)
                .filter(AetherJeiRuntimeLogger::isAetherItem)
                .filter(id -> !jeiItems.contains(id))
                .toList();

        if (missingItems.isEmpty()) {
            logger.info("JEI runtime registered {} Aether items. Filter text='{}', filteredVisibleToSearch={}, visibleInOverlay={}.",
                    jeiItems.size(), jeiRuntime.getIngredientFilter().getFilterText(), filteredAetherItems, visibleAetherItems);
        } else {
            logger.warn("JEI runtime registered {} Aether items and is still missing {} entries: {}",
                    jeiItems.size(), missingItems.size(), missingItems.stream().limit(20).toList());
        }
    }

    static String logVisibleOverlayState(Logger logger, IJeiRuntime jeiRuntime, Screen screen, String previousState) {
        List<ItemStack> visibleItems = jeiRuntime.getIngredientListOverlay().getVisibleIngredients(VanillaTypes.ITEM_STACK);
        List<Identifier> sampleVisibleItems = visibleItems.stream()
                .map(ItemStack::getItem)
                .map(BuiltInRegistries.ITEM::getKey)
                .limit(8)
                .toList();
        long visibleAetherItems = countAetherItems(visibleItems);
        String filterText = jeiRuntime.getIngredientFilter().getFilterText();
        String state = screen.getClass().getName() + "|" + filterText + "|" + visibleItems.size() + "|" + visibleAetherItems + "|" + sampleVisibleItems;
        if (!state.equals(previousState)) {
            logger.info("JEI overlay state: screen={}, filter='{}', visibleCount={}, visibleAetherCount={}, sample={}",
                    screen.getClass().getSimpleName(), filterText, visibleItems.size(), visibleAetherItems, sampleVisibleItems);
        }
        return state;
    }

    private static long countAetherItems(Collection<ItemStack> itemStacks) {
        return itemStacks.stream()
                .map(ItemStack::getItem)
                .map(BuiltInRegistries.ITEM::getKey)
                .filter(AetherJeiRuntimeLogger::isAetherItem)
                .count();
    }

    private static boolean isAetherItem(Identifier id) {
        return Objects.equals(id.getNamespace(), Aether.MODID);
    }
}
