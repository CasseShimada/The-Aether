package com.aetherteam.nitrogen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class BossRoomTracker<T extends Mob & BossMob<T>> {
    private final T boss;
    private final Vec3 originCoordinates;
    private final AABB roomBounds;
    private final Set<UUID> trackedPlayers = new HashSet<>();

    public BossRoomTracker(T boss, Vec3 originCoordinates, AABB roomBounds) {
        this.boss = boss;
        this.originCoordinates = originCoordinates;
        this.roomBounds = roomBounds;
    }

    public static <T extends Mob & BossMob<T>> BossRoomTracker<T> createFallback(T boss) {
        Vec3 origin = boss.position();
        AABB bounds = boss.getBoundingBox().inflate(16.0D, 8.0D, 16.0D);
        return new BossRoomTracker<>(boss, origin, bounds);
    }

    public Vec3 originCoordinates() {
        return this.originCoordinates;
    }

    public AABB roomBounds() {
        return this.roomBounds;
    }

    public boolean isBossWithinRoom() {
        return this.roomBounds.contains(this.boss.position());
    }

    public boolean isPlayerWithinRoomInterior(Entity entity) {
        return entity != null && this.roomBounds.deflate(1.0, 1.0, 1.0).contains(entity.position());
    }

    public boolean isPlayerTracked(Player player) {
        return player != null && (this.trackedPlayers.contains(player.getUUID()) || this.isPlayerWithinRoomInterior(player));
    }

    public void modifyRoom(Function<BlockState, BlockState> converter) {
        if (!(this.boss.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos min = BlockPos.containing(this.roomBounds.minX, this.roomBounds.minY, this.roomBounds.minZ);
        BlockPos max = BlockPos.containing(Math.nextDown(this.roomBounds.maxX), Math.nextDown(this.roomBounds.maxY), Math.nextDown(this.roomBounds.maxZ));
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState oldState = level.getBlockState(pos);
            BlockState newState = converter.apply(oldState);
            if (newState != null && !newState.equals(oldState)) {
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    public void grantAdvancements(DamageSource source) {
        if (!(this.boss.level() instanceof ServerLevel level)) {
            return;
        }
        for (UUID playerId : this.trackedPlayers) {
            Player player = level.getPlayerByUUID(playerId);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardKillScore(this.boss, source);
            }
        }
    }

    public Set<Player> dungeonPlayers() {
        if (!(this.boss.level() instanceof ServerLevel level)) {
            return Collections.emptySet();
        }
        Set<Player> players = new HashSet<>();
        for (UUID playerId : this.trackedPlayers) {
            Player player = level.getPlayerByUUID(playerId);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public void updateTrackedPlayers(Consumer<Player> onPlayerAdded, Consumer<Player> onPlayerRemoved) {
        if (!(this.boss.level() instanceof ServerLevel level)) {
            return;
        }
        Set<UUID> currentlyInside = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            if (player.isAlive() && this.isPlayerWithinRoomInterior(player)) {
                UUID playerId = player.getUUID();
                currentlyInside.add(playerId);
                if (this.trackedPlayers.add(playerId)) {
                    onPlayerAdded.accept(player);
                }
            }
        }
        Set<UUID> removed = new HashSet<>();
        for (UUID trackedPlayer : this.trackedPlayers) {
            if (!currentlyInside.contains(trackedPlayer)) {
                removed.add(trackedPlayer);
            }
        }
        for (UUID removedPlayer : removed) {
            this.trackedPlayers.remove(removedPlayer);
            onPlayerRemoved.accept(level.getPlayerByUUID(removedPlayer));
        }
    }
}
