package com.aetherteam.aether.accessories.effect;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.item.accessories.abilities.AllowWalkingOnSnow;
import com.aetherteam.aether.item.accessories.abilities.PiglinNeutralInducer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Shared bridge for treating Aether accessory slots as held/equipped equivalents.
 */
public final class AccessoryEffectBridge {
    private static final Map<Class<?>, MethodHandle> ENCHANTMENT_VISITOR_HANDLES = new ConcurrentHashMap<>();

    private AccessoryEffectBridge() {
    }

    public static boolean isHoldingEquivalent(LivingEntity entity, Predicate<ItemStack> predicate) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return false;
        }

        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            if (predicate.test(reference.stack())) {
                return true;
            }
        }
        return false;
    }

    public static EquipmentSlot resolveVirtualSlot(LivingEntity entity, String slotName, ItemStack stack) {
        if (slotName.endsWith("shield_slot") && AccessorySlotResolver.isShieldLike(stack)) {
            return EquipmentSlot.OFFHAND;
        }

        EquipmentSlot preferred = resolveEquipmentSlot(entity, stack);
        if (preferred.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || preferred == EquipmentSlot.OFFHAND || preferred == EquipmentSlot.MAINHAND) {
            return preferred;
        }

        if (AccessorySlotResolver.isShieldLike(stack)) {
            return EquipmentSlot.OFFHAND;
        }

        return EquipmentSlot.MAINHAND;
    }

    @Nullable
    public static SlotEntryReference findFirstElytraReference(LivingEntity entity) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return null;
        }

        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            if (reference.stack().is(Items.ELYTRA)) {
                return reference;
            }
        }

        return null;
    }

    /**
     * Consumes exactly one equipped death-protection stack.
     */
    @Nullable
    public static DeathProtectionResult consumeDeathProtection(LivingEntity entity) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return null;
        }

        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            ItemStack stack = reference.stack();
            DeathProtection deathProtection = stack.get(DataComponents.DEATH_PROTECTION);
            if (deathProtection == null) {
                continue;
            }

            ItemStack previousStack = stack.copy();
            ItemStack usedStack = stack.copyWithCount(1);
            stack.shrink(1);
            setAccessoryStack(entity, reference, stack, previousStack);
            return new DeathProtectionResult(usedStack, deathProtection);
        }

        return null;
    }

    public static boolean applyDeathProtection(LivingEntity entity) {
        DeathProtectionResult result = consumeDeathProtection(entity);
        if (result == null) {
            return false;
        }

        ItemStack usedStack = result.usedStack();
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(usedStack.getItem()));
            CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, usedStack);
            usedStack.causeUseVibration(entity, GameEvent.ITEM_INTERACT_FINISH);
        }

        entity.setHealth(1.0F);
        result.deathProtection().applyEffects(usedStack, entity);
        entity.level().broadcastEntityEvent(entity, (byte) 35);
        return true;
    }

    public static void syncAccessorySlotMutation(LivingEntity entity, SlotEntryReference reference, ItemStack previousStack) {
        if (sameStack(previousStack, reference.stack())) {
            return;
        }
        setAccessoryStack(entity, reference, reference.stack(), previousStack);
    }

    public static void syncAccessoryStackMutation(LivingEntity entity, ItemStack mutatedStack, ItemStack previousStack) {
        SlotEntryReference reference = findReferenceByStackIdentity(entity, mutatedStack);
        if (reference != null) {
            syncAccessorySlotMutation(entity, reference, previousStack);
        }
    }

    public static boolean isAccessoryStack(LivingEntity entity, ItemStack stack) {
        return findReferenceByStackIdentity(entity, stack) != null;
    }

    public static void addEnchantedAccessoryCandidates(List<EnchantedItemInUse> candidates, DataComponentType<?> componentType, LivingEntity entity, Predicate<ItemStack> predicate) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return;
        }

        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            ItemStack stack = reference.stack();
            if (!predicate.test(stack)) {
                continue;
            }

            EquipmentSlot virtualSlot = resolveVirtualSlot(entity, reference.slotName(), stack);
            ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            if (enchantments.isEmpty()) {
                continue;
            }

            for (var entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey().value();
                if (enchantment.effects().has(componentType) && enchantment.matchingSlot(virtualSlot)) {
                    candidates.add(new EnchantedItemInUse(stack, virtualSlot, entity));
                }
            }
        }
    }

    public static void runEquipmentEnchantmentIteration(LivingEntity entity, Object visitor) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return;
        }

        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            ItemStack stack = reference.stack();
            EquipmentSlot virtualSlot = resolveEnchantmentEquipmentSlot(entity, stack);
            if (virtualSlot == null) {
                continue;
            }
            runEnchantmentIterationOnItem(reference, stack, virtualSlot, entity, visitor);
        }
    }

    public static TriState shouldAllowWalkingOnSnow(LivingEntity entity) {
        return evaluateAccessoryTriState(entity, AllowWalkingOnSnow.class, (effect, stack, reference) -> effect.allowWalkingOnSnow(stack, reference));
    }

    public static TriState shouldMakePiglinsNeutral(LivingEntity entity) {
        return evaluateAccessoryTriState(entity, PiglinNeutralInducer.class, (effect, stack, reference) -> effect.makePiglinsNeutral(stack, reference));
    }

    public static boolean modifyPiglinNeutrality(LivingEntity entity, boolean original) {
        return augmentVanillaResult(original, shouldMakePiglinsNeutral(entity));
    }

    public static boolean modifyPowderSnowWalking(Entity entity, boolean original) {
        if (original) {
            return true;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        return augmentVanillaResult(false, shouldAllowWalkingOnSnow(livingEntity));
    }

    @Nullable
    private static SlotEntryReference findReferenceByStackIdentity(LivingEntity entity, ItemStack stack) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return null;
        }

        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            if (reference.stack() == stack) {
                return reference;
            }
        }

        return null;
    }

    private static void setAccessoryStack(LivingEntity entity, SlotEntryReference reference, ItemStack stack, ItemStack previousStack) {
        boolean removed = stack.isEmpty();

        if (removed) {
            AccessoriesAPI.getOrDefaultAccessory(previousStack).onUnequip(previousStack.copy(), reference.reference());
        }

        reference.reference().setStack(removed ? ItemStack.EMPTY : stack);

        if (removed) {
            var accessories = AccessoriesAPI.getAccessories(entity);
            if (accessories != null) {
                accessories.handleImmediateUnequip(reference.reference());
            }
        } else {
            var accessories = AccessoriesAPI.getAccessories(entity);
            if (accessories != null) {
                accessories.handleImmediateStackMutation(reference.reference());
            }
        }

        AccessoryRuntime.forceSync(entity);
    }

    private static EquipmentSlot resolveEquipmentSlot(LivingEntity entity, ItemStack stack) {
        if (AccessorySlotResolver.matchesHead(stack)) {
            return EquipmentSlot.HEAD;
        }
        if (AccessorySlotResolver.isShieldLike(stack)) {
            return EquipmentSlot.OFFHAND;
        }
        return entity.getEquipmentSlotForItem(stack);
    }

    @Nullable
    private static EquipmentSlot resolveEnchantmentEquipmentSlot(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (stack.is(Items.ELYTRA)) {
            return EquipmentSlot.CHEST;
        }
        if (AccessorySlotResolver.isShieldLike(stack)) {
            return EquipmentSlot.OFFHAND;
        }
        if (AccessorySlotResolver.matchesHead(stack)) {
            return EquipmentSlot.HEAD;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || !entity.canUseSlot(equippable.slot())) {
            return null;
        }

        EquipmentSlot slot = equippable.slot();
        return slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || slot == EquipmentSlot.OFFHAND ? slot : null;
    }

    private static void runEnchantmentIterationOnItem(SlotEntryReference reference, ItemStack stack, EquipmentSlot slot, LivingEntity entity, Object visitor) {
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) {
            return;
        }

        ItemStack previousStack = stack.copy();
        try {
            EnchantedItemInUse itemInUse = new EnchantedItemInUse(stack, slot, entity);
            for (var entry : enchantments.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                if (holder.value().matchingSlot(slot)) {
                    invokeEnchantmentVisitor(visitor, holder, entry.getIntValue(), itemInUse);
                }
            }
        } finally {
            syncAccessorySlotMutation(entity, reference, previousStack);
        }
    }

    private static void invokeEnchantmentVisitor(Object visitor, Holder<Enchantment> enchantment, int level, EnchantedItemInUse itemInUse) {
        try {
            enchantmentVisitorHandle(visitor.getClass()).invoke(visitor, enchantment, level, itemInUse);
        } catch (RuntimeException | Error exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new IllegalStateException("Unable to apply accessory equipment enchantment", throwable);
        }
    }

    private static MethodHandle enchantmentVisitorHandle(Class<?> visitorClass) {
        return ENCHANTMENT_VISITOR_HANDLES.computeIfAbsent(visitorClass, AccessoryEffectBridge::createEnchantmentVisitorHandle);
    }

    private static MethodHandle createEnchantmentVisitorHandle(Class<?> visitorClass) {
        try {
            Method method = findEnchantmentVisitorMethod(visitorClass);
            method.setAccessible(true);
            return MethodHandles.lookup().unreflect(method);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to access equipment enchantment visitor", exception);
        }
    }

    private static Method findEnchantmentVisitorMethod(Class<?> visitorClass) throws NoSuchMethodException {
        Method method = findDeclaredEnchantmentVisitorMethod(visitorClass);
        if (method != null) {
            return method;
        }

        for (Method candidate : visitorClass.getMethods()) {
            if (isEnchantmentVisitorMethod(candidate)) {
                return candidate;
            }
        }

        throw new NoSuchMethodException(visitorClass.getName() + ".accept(Holder,int,EnchantedItemInUse)");
    }

    @Nullable
    private static Method findDeclaredEnchantmentVisitorMethod(Class<?> visitorClass) {
        Class<?> currentClass = visitorClass;
        while (currentClass != null) {
            for (Method method : currentClass.getDeclaredMethods()) {
                if (isEnchantmentVisitorMethod(method)) {
                    return method;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    private static boolean isEnchantmentVisitorMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return method.getReturnType() == void.class
            && parameterTypes.length == 3
            && Holder.class.isAssignableFrom(parameterTypes[0])
            && parameterTypes[1] == int.class
            && EnchantedItemInUse.class.isAssignableFrom(parameterTypes[2]);
    }

    private static boolean sameStack(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameComponents(first, second) && first.getCount() == second.getCount();
    }

    private static <T> TriState evaluateAccessoryTriState(LivingEntity entity, Class<T> effectClass, TriStateEvaluator<T> evaluator) {
        var accessories = AccessoriesAPI.getAccessories(entity);
        if (accessories == null) {
            return TriState.DEFAULT;
        }

        TriState fallback = TriState.DEFAULT;
        for (SlotEntryReference reference : accessories.getAllEquipped()) {
            ItemStack stack = reference.stack();
            T effect = effectClass.isInstance(stack.getItem()) ? effectClass.cast(stack.getItem()) : null;
            if (effect == null) {
                var accessory = AccessoriesAPI.getOrDefaultAccessory(stack);
                if (effectClass.isInstance(accessory)) {
                    effect = effectClass.cast(accessory);
                }
            }
            if (effect == null) {
                continue;
            }

            TriState state = evaluator.evaluate(effect, stack, reference.reference());
            if (state == TriState.TRUE) {
                return TriState.TRUE;
            }
            if (state == TriState.FALSE) {
                fallback = TriState.FALSE;
            }
        }

        return fallback;
    }

    private static boolean augmentVanillaResult(boolean original, TriState state) {
        if (original || state == TriState.TRUE) {
            return true;
        }
        if (state == TriState.FALSE) {
            return false;
        }
        return original;
    }

    @FunctionalInterface
    private interface TriStateEvaluator<T> {
        TriState evaluate(T effect, ItemStack stack, com.aetherteam.aether.accessories.api.slot.SlotReference reference);
    }

    public record DeathProtectionResult(ItemStack usedStack, DeathProtection deathProtection) {
    }
}
