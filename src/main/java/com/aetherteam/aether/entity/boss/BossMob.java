package com.aetherteam.aether.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public interface BossMob<T extends Mob & BossMob<T>> {
    TargetingConditions NON_COMBAT = TargetingConditions.forNonCombat().ignoreInvisibilityTesting();

    @Nullable
    BossRoomTracker<T> getDungeon();

    void setDungeon(@Nullable BossRoomTracker<T> dungeon);

    boolean isBossFight();

    void setBossFight(boolean fighting);

    Component getBossName();

    void setBossName(Component component);

    int getDeathScore();

    default void reset() {
        this.setBossFight(false);
    }

    @Nullable
    BlockState convertBlock(BlockState state);

    void onDungeonPlayerAdded(@Nullable Player player);

    void onDungeonPlayerRemoved(@Nullable Player player);

    @SuppressWarnings("unchecked")
    default void trackDungeon() {
        if (!(this instanceof Mob mob) || mob.level().isClientSide()) {
            return;
        }
        BossRoomTracker<T> dungeon = this.getDungeon();
        if (dungeon == null) {
            dungeon = BossRoomTracker.createFallback((T) this);
            this.setDungeon(dungeon);
        }
        dungeon.updateTrackedPlayers(this::onDungeonPlayerAdded, this::onDungeonPlayerRemoved);
        if (this.isBossFight() && (dungeon.dungeonPlayers().isEmpty() || !dungeon.isBossWithinRoom())) {
            this.reset();
        }
    }

    default void tearDownRoom() {
        BossRoomTracker<T> dungeon = this.getDungeon();
        if (dungeon != null) {
            dungeon.modifyRoom(this::convertBlock);
        }
    }

    default void displayTooFarMessage(Player player) {
        com.aetherteam.aether.util.MessageUtil.sendPlayerMessage(player, Component.literal("I am too far away to damage this boss."), true);
    }

    default void addBossSaveData(CompoundTag tag, HolderLookup.Provider provider) {
        BossRoomTracker<T> dungeon = this.getDungeon();
        if (dungeon != null) {
            tag.putDouble("DungeonOriginX", dungeon.originCoordinates().x);
            tag.putDouble("DungeonOriginY", dungeon.originCoordinates().y);
            tag.putDouble("DungeonOriginZ", dungeon.originCoordinates().z);
            tag.putDouble("DungeonMinX", dungeon.roomBounds().minX);
            tag.putDouble("DungeonMinY", dungeon.roomBounds().minY);
            tag.putDouble("DungeonMinZ", dungeon.roomBounds().minZ);
            tag.putDouble("DungeonMaxX", dungeon.roomBounds().maxX);
            tag.putDouble("DungeonMaxY", dungeon.roomBounds().maxY);
            tag.putDouble("DungeonMaxZ", dungeon.roomBounds().maxZ);
        }
        tag.putBoolean("BossFight", this.isBossFight());
    }

    @SuppressWarnings("unchecked")
    default void readBossSaveData(CompoundTag tag, HolderLookup.Provider provider) {
        if (!(this instanceof Mob)) {
            return;
        }
        if (tag.getDouble("DungeonOriginX").isPresent()) {
            net.minecraft.world.phys.Vec3 origin = new net.minecraft.world.phys.Vec3(
                    tag.getDouble("DungeonOriginX").orElse(0.0),
                    tag.getDouble("DungeonOriginY").orElse(0.0),
                    tag.getDouble("DungeonOriginZ").orElse(0.0)
            );
            AABB bounds = new AABB(
                    tag.getDouble("DungeonMinX").orElse(0.0),
                    tag.getDouble("DungeonMinY").orElse(0.0),
                    tag.getDouble("DungeonMinZ").orElse(0.0),
                    tag.getDouble("DungeonMaxX").orElse(0.0),
                    tag.getDouble("DungeonMaxY").orElse(0.0),
                    tag.getDouble("DungeonMaxZ").orElse(0.0)
            );
            this.setDungeon(new BossRoomTracker<>((T) this, origin, bounds));
        }
        tag.getBoolean("BossFight").ifPresent(this::setBossFight);
    }

    @SuppressWarnings("unchecked")
    default void addBossSaveData(CompoundTag tag) {
        if (this instanceof Mob mob) {
            this.addBossSaveData(tag, mob.registryAccess());
        }
    }

    @SuppressWarnings("unchecked")
    default void readBossSaveData(CompoundTag tag) {
        if (this instanceof Mob mob) {
            this.readBossSaveData(tag, mob.registryAccess());
        }
    }
}
