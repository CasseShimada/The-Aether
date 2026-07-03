package com.aetherteam.aether.block.portal;

import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.ClientAccess;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.sound.PortalTriggerSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PortalClientUtil {
    public static void handleAetherPortal(Player player, AetherPlayerAttachment attachment) {
        if (player instanceof LocalPlayer localPlayer) {
            if (!(ClientAccess.screen(Minecraft.getInstance()) instanceof LevelLoadingScreen)) {
                attachment.oPortalIntensity = attachment.portalIntensity;
                float f = 0.0F;
                if (localPlayer.portalProcess != null && localPlayer.portalProcess.isInsidePortalThisTick() && localPlayer.portalProcess.isSamePortal(AetherBlocks.AETHER_PORTAL)) {
                    if (ClientAccess.screen(Minecraft.getInstance()) != null
                        && !ClientAccess.screen(Minecraft.getInstance()).isPauseScreen()
                        && !(ClientAccess.screen(Minecraft.getInstance()) instanceof DeathScreen)
                        && !(ClientAccess.screen(Minecraft.getInstance()) instanceof WinScreen)) {
                        if (ClientAccess.screen(Minecraft.getInstance()) instanceof AbstractContainerScreen) {
                            localPlayer.closeContainer();
                        }

                        ClientAccess.setScreen(Minecraft.getInstance(), null);
                    }

                    if (attachment.portalIntensity == 0.0F) {
                        playTriggerSound();
                    }

                    f = 0.0125F;
                    localPlayer.portalProcess.setAsInsidePortalThisTick(false);
                } else if (attachment.portalIntensity > 0.0F) {
                    f = -0.05F;
                }

                attachment.portalIntensity = Mth.clamp(attachment.portalIntensity + f, 0.0F, 1.0F);
            }
        }
    }

    public static void playTriggerSound() {
        Minecraft.getInstance().getSoundManager().play(PortalTriggerSoundInstance.forLocalAmbience(Minecraft.getInstance().player, AetherSoundEvents.BLOCK_AETHER_PORTAL_TRIGGER, Minecraft.getInstance().level.getRandom().nextFloat() * 0.4F + 0.8F, 0.25F));
    }

    public static void playTravelSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(AetherSoundEvents.BLOCK_AETHER_PORTAL_TRAVEL, Minecraft.getInstance().level.getRandom().nextFloat() * 0.4F + 0.8F, 0.25F));
    }
}
