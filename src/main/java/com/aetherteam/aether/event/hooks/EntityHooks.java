package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.ai.goal.BeeGrowBerryBushGoal;
import com.aetherteam.aether.entity.ai.goal.FoxEatBerryBushGoal;
import com.aetherteam.aether.entity.monster.Swet;
import com.aetherteam.aether.entity.monster.Zephyr;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.entity.passive.Aerwhale;
import com.aetherteam.aether.entity.passive.FlyingCow;
import com.aetherteam.aether.entity.passive.MountableAnimal;
import com.aetherteam.aether.entity.projectile.crystal.ThunderCrystal;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.mixin.mixins.common.accessor.MobAccessor;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EntityHooks {
    /**
     * Adds a new goal to an entity.
     *
     * @param entity The {@link Entity}.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onEntityJoin(EntityJoinLevelEvent)
     */
    public static void addGoals(Entity entity) {
        if (entity.getClass() == Bee.class) {
            Bee bee = (Bee) entity;
            ((MobAccessor) bee).aether$getGoalSelector().addGoal(7, new BeeGrowBerryBushGoal(bee));
        } else if (entity.getClass() == Fox.class) {
            Fox fox = (Fox) entity;
            ((MobAccessor) fox).aether$getGoalSelector().addGoal(10, new FoxEatBerryBushGoal(fox, 1.2F, 12, 1));
        }
    }

    /**
     * Used to check whether an entity can spawn with accessories based on their {@link EntityType}.
     *
     * @param entity The {@link Entity} that is spawning.
     * @return Whether the entity can spawn in the world with accessories, as a {@link Boolean}.
     * @see com.aetherteam.aether.mixin.mixins.common.EventHooksMixin
     */
    public static boolean canMobSpawnWithAccessories(Entity entity) {
        return EntityAccessorySpawnHooks.canMobSpawnWithAccessories(entity);
    }

    /**
     * Equips entities with accessories during spawning.
     *
     * @param entity The {@link Entity} to equip accessories to.
     * @see com.aetherteam.aether.mixin.mixins.common.EventHooksMixin
     */
    public static void spawnWithAccessories(Entity entity, DifficultyInstance difficulty) {
        EntityAccessorySpawnHooks.spawnWithAccessories(entity, difficulty);
    }

    /**
     * Prevents dismounting Aether mounts in the air, and Swets when consumed.
     *
     * @param rider       The {@link Entity} riding the mount.
     * @param mount       The mounted {@link Entity}.
     * @param dismounting Whether the rider is trying to dismount, as a {@link Boolean}.
     * @return Whether to prevent the rider from dismounting, as a {@link Boolean}.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onMountEntity(EntityMountEvent)
     */
    public static boolean dismountPrevention(Entity rider, Entity mount, boolean dismounting) {
        if (dismounting && rider.isShiftKeyDown()) {
            return (mount instanceof MountableAnimal && !mount.onGround() && !mount.isInLiquid() && !mount.isPassenger()) || (mount instanceof Swet swet && !swet.isFriendly());
        }
        return false;
    }

    /**
     * Tracks whether a passenger has mounted or dismounted a {@link MountableAnimal}.
     *
     * @param mount       The mounted {@link Entity}.
     * @param dismounting Whether the rider is trying to dismount, as a {@link Boolean}.
     */
    public static void trackMount(Entity mount, boolean dismounting) {
        if (mount instanceof MountableAnimal mountableAnimal) {
            mountableAnimal.setHasPassenger(!dismounting);
        }
    }

    /**
     * Launches a mount when it interacts with a blue aercloud. This is handled as an event to get around a vanilla bug with it not working from the {@link com.aetherteam.aether.block.natural.BlueAercloudBlock} class.
     *
     * @param player The passenger {@link Player}.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onRiderTick(PlayerTickEvent.Post)
     */
    public static void launchMount(Player player) {
        Entity mount = player.getVehicle();
        if (player.isPassenger() && mount != null) {
            if (mount.level().getBlockStates(mount.getBoundingBox()).anyMatch((state) -> state.is(AetherBlocks.BLUE_AERCLOUD.get()))) {
                if (player.level().isClientSide()) {
                    mount.setDeltaMovement(mount.getDeltaMovement().x(), 2.0, mount.getDeltaMovement().z());
                }
            }
        }
    }

    /**
     * Handles milking cow entities with Skyroot Buckets.
     *
     * @param target The target {@link Entity} to milk.
     * @param player The {@link Player} milking the target.
     * @param hand   The {@link InteractionHand} with the bucket item.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onInteractWithEntity(PlayerInteractEvent.EntityInteractSpecific)
     */
    public static void skyrootBucketMilking(Entity target, Player player, InteractionHand hand) {
        EntityBucketHooks.skyrootBucketMilking(target, player, hand);
    }

    /**
     * Handles picking up aquatic entities with a Skyroot Bucket. This is done by checking for the result bucket that contains the entity and replacing it with a Skyroot equivalent.
     *
     * @param target The target {@link Entity}.
     * @param player The {@link Player}.
     * @param hand   The {@link InteractionHand} with the bucket item.
     * @return The {@link Optional} {@link InteractionResult} from this interaction.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onInteractWithEntity(PlayerInteractEvent.EntityInteractSpecific)
     */
    public static Optional<InteractionResult> pickupBucketable(Entity target, Player player, InteractionHand hand) {
        return EntityBucketHooks.pickupBucketable(target, player, hand);
    }

    /**
     * Handles the interaction for equipping and unequipping accessories to armor stands.
     *
     * @param target The target {@link Entity}.
     * @param player The {@link Player}.
     * @param stack  The held {@link ItemStack}.
     * @param pos    The right-click {@link Vec3} position.
     * @param hand   The {@link InteractionHand} with the item.
     * @return The {@link Optional} {@link InteractionResult} from this interaction.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onInteractWithEntity(PlayerInteractEvent.EntityInteractSpecific)
     */
    public static Optional<InteractionResult> interactWithArmorStand(Entity target, Player player, ItemStack stack, Vec3 pos, InteractionHand hand) {
        return EntityArmorStandHooks.interactWithArmorStand(target, player, stack, pos, hand);
    }

    /**
     * Prevents an entity from being hooked with a Fishing Rod.
     *
     * @param projectileEntity The hook projectile {@link Entity}.
     * @param rayTraceResult   The {@link HitResult} of the projectile.
     * @return Whether to prevent the hook interaction, as a {@link Boolean}.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onProjectileHitEntity(ProjectileImpactEvent)
     */
    public static boolean preventEntityHooked(Entity projectileEntity, HitResult rayTraceResult) {
        if (rayTraceResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity().getType().builtInRegistryHolder().is(AetherTags.Entities.UNHOOKABLE) && projectileEntity instanceof FishingHook;
        }
        return false;
    }

    /**
     * Disallows blocking the Slider with a shield.
     *
     * @param source The {@link DamageSource} to block.
     * @return Whether to disallow blocking, as a {@link Boolean}.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onShieldBlock(LivingShieldBlockEvent)
     */
    public static boolean preventSliderShieldBlock(DamageSource source) {
        return source.getEntity() instanceof Slider;
    }

    /**
     * Prevents lightning from damaging dungeon keys.
     *
     * @param entity The {@link Entity}.
     * @return Whether lightning hit a key item, as a {@link Boolean}.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onLightningStrike(EntityStruckByLightningEvent)
     */
    public static boolean lightningHitKeys(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return itemEntity.getItem().is(AetherTags.Items.DUNGEON_KEYS);
        } else {
            return false;
        }
    }

    /**
     * Prevents lightning summoned by Thunder Crystals from damaging items.
     *
     * @param entity    The {@link Entity} struck by the lightning bolt.
     * @param lightning The {@link LightningBolt} that struck the entity.
     * @return Whether the lightning was from a {@link ThunderCrystal} and hit an item, as a {@link Boolean}.
     */
    public static boolean thunderCrystalHitItems(Entity entity, LightningBolt lightning) {
        if (entity instanceof ItemEntity) {
            if (lightning.hasAttached(AetherDataAttachments.LIGHTNING_TRACKER)) {
                return lightning.getAttachedOrCreate(AetherDataAttachments.LIGHTNING_TRACKER).getOwner(lightning.level()) instanceof ValkyrieQueen;
            }
        }
        return false;
    }

    /**
     * Tracks if items were dropped by a player's death.
     *
     * @param entity    The {@link LivingEntity} that dropped the items.
     * @param itemDrops The {@link Collection} of dropped {@link ItemEntity}s.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onPlayerDrops(LivingDropsEvent)
     */
    public static void trackDrops(LivingEntity entity, Collection<ItemEntity> itemDrops) {
        if (entity instanceof Player player) {
            itemDrops.forEach(itemEntity -> itemEntity.getAttachedOrCreate(AetherDataAttachments.DROPPED_ITEM).setOwner(player));
        }
    }

    /**
     * Damages certain accessory items dropped from entities if they're not guaranteed drops.
     *
     * @param entity      The {@link LivingEntity} dropping the accessories.
     * @param itemStacks   The {@link List} of {@link ItemStack} drops.
     * @param recentlyHit Whether the entity was recently hit, as a {@link Boolean}.
     * @param looting     The {@link Integer} for the looting enchantment value.
     * @return The new {@link Collection} of {@link ItemEntity} drops.
     * @see com.aetherteam.aether.event.listeners.EntityListener#listen(IEventBus)
     */
    public static List<ItemStack> handleEntityAccessoryDrops(LivingEntity entity, List<ItemStack> itemStacks, boolean recentlyHit, int looting) {
        return EntityAccessorySpawnHooks.handleEntityAccessoryDrops(entity, itemStacks, recentlyHit, looting);
    }

    /**
     * Increase the experience drops of an entity based on whether they're wearing accessories.
     *
     * @param entity     The {@link LivingEntity} dropping the experience.
     * @param experience The original {@link Integer} amount of experience.
     * @return The new {@link Integer} amount of experience.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onDropExperience(LivingExperienceDropEvent)
     */
    public static int modifyExperience(LivingEntity entity, int experience) {
        return EntityAccessorySpawnHooks.modifyExperience(entity, experience);
    }

    /**
     * Prevents an entity from being inflicted with {@link AetherEffects#INEBRIATION} if it has {@link AetherEffects#REMEDY} applied.
     *
     * @param livingEntity    The {@link LivingEntity} that the effect is being applied to.
     * @param appliedInstance The {@link MobEffectInstance}.
     * @return Whether Inebriation application can be prevented.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onEffectApply(MobEffectEvent.Applicable)
     */
    public static boolean preventInebriation(LivingEntity livingEntity, MobEffectInstance appliedInstance) {
        return livingEntity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.REMEDY)) && appliedInstance.getEffect().value() == AetherEffects.INEBRIATION;
    }

    /**
     * Prevents Slime split behavior from carrying over to Swets.
     *
     * @param mob The splitting {@link Mob}.
     * @return Whether the {@link Mob} should split.
     * @see com.aetherteam.aether.event.listeners.EntityListener#onEntitySplit(MobSplitEvent)
     */
    public static boolean preventSplit(Mob mob) {
        return mob.getType().builtInRegistryHolder().is(AetherTags.Entities.SWETS);
    }

    /**
     * Bridges custom NeoForge mob-category behavior by performing low-frequency natural spawn attempts
     * for Aether sky mobs that should remain present around active players.
     */
    public static void tickAetherSkySpawns(ServerLevel level) {
        EntitySkySpawnHooks.tickAetherSkySpawns(level);
    }
}
