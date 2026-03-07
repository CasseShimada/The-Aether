package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Input;

public class CapabilityClientHooks {
    public static class AetherPlayerHooks {
        /**
         * Tracks whether the player is jumping or moving on the client to the {@link AetherPlayerAttachment}.
         *
         * @param player The {@link Player}.
         * @param input  The {@link Input}.
         * @see com.aetherteam.aether.client.event.listeners.capability.AetherPlayerClientListener#onMove(MovementInputUpdateEvent)
         */
        public static void movementInput(Player player, ClientInput input) {
            var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            Input keys = input.keyPresses;
            boolean isJumping = keys.jump();
            if (isJumping != aetherPlayer.isJumping()) {
                aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setJumping", isJumping);
            }
            boolean isMoving = isJumping || keys.forward() || keys.backward() || keys.left() || keys.right() || player.isFallFlying();
            if (isMoving != aetherPlayer.isMoving()) {
                aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setMoving", isMoving);
            }
        }

        /**
         * Fabric does not provide the same global post key/mouse events that NeoForge uses for this sync path.
         * Poll per-tick input states so the server always receives up-to-date hit and jump-ability flags.
         */
        public static void tickInput(Player player) {
            var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);

            boolean isHitting = Minecraft.getInstance().options.keyAttack.isDown();
            if (isHitting != aetherPlayer.isHitting()) {
                aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setHitting", isHitting);
            }

            boolean gravititeJumpActive = AetherKeys.GRAVITITE_JUMP_ABILITY.isDown();
            if (gravititeJumpActive != aetherPlayer.isGravititeJumpActive()) {
                aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setGravititeJumpActive", gravititeJumpActive);
            }
        }

        /**
         * Checks for mouse input.
         *
         * @param button The {@link Integer} ID for the button.
         * @see com.aetherteam.aether.client.event.listeners.capability.AetherPlayerClientListener#onClick(InputEvent.MouseButton.Post)
         */
        public static void mouseInput(int button) {
            checkHitMouse(button);
            checkJumpAbilityMouse(button);
        }

        /**
         * Checks for key input.
         *
         * @param key The {@link Integer} ID for the key.
         * @see com.aetherteam.aether.client.event.listeners.capability.AetherPlayerClientListener#onPress(InputEvent.Key)
         */
        public static void keyInput(int key) {
            checkHitKey(key);
            checkJumpAbilityKey(key);
        }

        /**
         * Checks whether the player is hitting, and tracks that to the {@link AetherPlayerAttachment}.
         *
         * @param input The {@link Integer} for the ID of the input.
         */
        private static void checkHitMouse(int button) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                boolean isAttack = Minecraft.getInstance().options.keyAttack.matchesMouse(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(button, 0)));
                boolean isPressing = Minecraft.getInstance().options.keyAttack.isDown();
                boolean isHitting = isAttack && isPressing;
                if (isHitting != aetherPlayer.isHitting()) {
                    aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setHitting", isHitting);
                }
            }
        }

        private static void checkHitKey(int key) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                boolean isAttack = Minecraft.getInstance().options.keyAttack.matches(new KeyEvent(key, 0, 0));
                boolean isPressing = Minecraft.getInstance().options.keyAttack.isDown();
                boolean isHitting = isAttack && isPressing;
                if (isHitting != aetherPlayer.isHitting()) {
                    aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setHitting", isHitting);
                }
            }
        }

        /**
         * Checks whether the player is pressing the {@link AetherKeys#GRAVITITE_JUMP_ABILITY} key, and tracks that to the {@link AetherPlayerAttachment}.
         *
         * @param input The {@link Integer} for the ID of the input.
         */
        private static void checkJumpAbilityKey(int key) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                if (AetherKeys.GRAVITITE_JUMP_ABILITY.matches(new KeyEvent(key, 0, 0))) {
                    aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setGravititeJumpActive", AetherKeys.GRAVITITE_JUMP_ABILITY.isDown());
                }
            }
        }

        private static void checkJumpAbilityMouse(int button) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                if (AetherKeys.GRAVITITE_JUMP_ABILITY.matchesMouse(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(button, 0)))) {
                    aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setGravititeJumpActive", AetherKeys.GRAVITITE_JUMP_ABILITY.isDown());
                }
            }
        }
    }
}
