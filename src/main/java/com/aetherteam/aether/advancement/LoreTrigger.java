package com.aetherteam.aether.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

/**
 * Criterion trigger used for checking an item placed by a player inside a Book of Lore.
 */
public class LoreTrigger extends SimpleCriterionTrigger<LoreTrigger.Instance> {
    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, (instance) -> instance.test(stack));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {
        public static final Codec<LoreTrigger.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(LoreTrigger.Instance::player),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(LoreTrigger.Instance::item))
                .apply(instance, LoreTrigger.Instance::new));

        public static Criterion<Instance> forItem(ItemPredicate item) {
            return AetherAdvancementTriggers.LORE_ENTRY.get().createCriterion(new LoreTrigger.Instance(Optional.empty(), Optional.of(item)));
        }

        public static Criterion<Instance> forItem(ItemLike item) {
            return forItem(new ItemPredicate(
                    Optional.of(HolderSet.direct(item.asItem().builtInRegistryHolder())),
                    MinMaxBounds.Ints.ANY,
                    DataComponentMatchers.ANY));
        }

        public static Criterion<Instance> forAny() {
            return AetherAdvancementTriggers.LORE_ENTRY.get().createCriterion(new LoreTrigger.Instance(Optional.empty(), Optional.empty()));
        }

        public boolean test(ItemStack stack) {
            return this.item.isEmpty() || this.item.get().test(stack);
        }
    }
}
