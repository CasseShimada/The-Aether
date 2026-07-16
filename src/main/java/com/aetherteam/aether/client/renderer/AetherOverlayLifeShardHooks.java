package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.mixin.mixins.client.accessor.GuiAccessor;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

final class AetherOverlayLifeShardHooks {
    private static final Identifier TEXTURE_LIFE_SHARD_FULL = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_full");
    private static final Identifier TEXTURE_LIFE_SHARD_HALF = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_half");
    private static final Identifier TEXTURE_LIFE_SHARD_FULL_BLINKING = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_full_blinking");
    private static final Identifier TEXTURE_LIFE_SHARD_HALF_BLINKING = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_half_blinking");
    private static final Identifier TEXTURE_LIFE_SHARD_POISONED_FULL = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_poisoned_full");
    private static final Identifier TEXTURE_LIFE_SHARD_POISONED_HALF = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_poisoned_half");
    private static final Identifier TEXTURE_LIFE_SHARD_POISONED_FULL_BLINKING = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_poisoned_full_blinking");
    private static final Identifier TEXTURE_LIFE_SHARD_POISONED_HALF_BLINKING = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_poisoned_half_blinking");
    private static final Identifier TEXTURE_LIFE_SHARD_WITHERED_FULL = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_withered_full");
    private static final Identifier TEXTURE_LIFE_SHARD_WITHERED_HALF = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_withered_half");
    private static final Identifier TEXTURE_LIFE_SHARD_WITHERED_FULL_BLINKING = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_withered_full_blinking");
    private static final Identifier TEXTURE_LIFE_SHARD_WITHERED_HALF_BLINKING = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_withered_half_blinking");
    private static final Identifier TEXTURE_LIFE_SHARD_ABSORBING_FULL = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_absorbing_full");
    private static final Identifier TEXTURE_LIFE_SHARD_ABSORBING_HALF = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_absorbing_half");
    private static final Identifier TEXTURE_LIFE_SHARD_FROZEN_FULL = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_frozen_full");
    private static final Identifier TEXTURE_LIFE_SHARD_FROZEN_HALF = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/heart/shard_frozen_half");

    private AetherOverlayLifeShardHooks() {
    }

    static void renderSilverLifeShardHearts(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, Window window, Gui gui, LocalPlayer player) {
        Hud hud = gui.hud;
        GuiAccessor guiAccessor = (GuiAccessor) hud;
        if (!AetherConfig.CLIENT.enable_silver_hearts.get() || !minecraft.gameMode.canHurtPlayer() || hud.isHidden()) {
            return;
        }

        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (aetherPlayer.getLifeShardCount() <= 0) {
            return;
        }

        AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance == null) {
            return;
        }

        int lastLifeShardHealth = 0;
        int lastOverallHealth = 0;
        double overallHealth = attributeInstance.getValue();
        double maxLifeShardHealth = aetherPlayer.getLifeShardHealthAttributeModifier().amount();
        int maxDefaultHealth = Mth.ceil(overallHealth - maxLifeShardHealth);
        int currentOverallHealth = Mth.ceil(player.getHealth());
        int currentLifeShardHealth = Mth.ceil(maxDefaultHealth > 20
                ? Mth.clamp(currentOverallHealth - 20, 0, maxLifeShardHealth)
                : Math.min(player.getHealth(), currentOverallHealth - maxDefaultHealth));

        boolean highlight = guiAccessor.aether$getHealthBlinkTime() > (long) hud.getGuiTicks()
                && (guiAccessor.aether$getHealthBlinkTime() - (long) hud.getGuiTicks()) / 3L % 2L == 1L;
        if (Util.getMillis() - guiAccessor.aether$getLastHealthTime() > 1000L) {
            lastOverallHealth = currentOverallHealth;
            lastLifeShardHealth = currentLifeShardHealth;
        }

        guiAccessor.aether$getRandom().setSeed(hud.getGuiTicks() * 312871L);

        float displayOverallHealth = Math.max((float) overallHealth, Math.max(lastOverallHealth, currentOverallHealth));
        float displayLifeShardHealth = Math.max((float) maxLifeShardHealth, Math.max(lastLifeShardHealth, currentLifeShardHealth));
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int healthRows = Mth.ceil((displayOverallHealth + absorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int left = window.getGuiScaledWidth() / 2 - 91;
        int top = window.getGuiScaledHeight() - 39;
        int regen = player.hasEffect(MobEffects.REGENERATION)
                ? hud.getGuiTicks() % Mth.ceil(displayOverallHealth + 5.0F)
                : Integer.MIN_VALUE;

        renderHearts(guiGraphics, player, hud, left, top, regen, displayOverallHealth, displayLifeShardHealth, maxDefaultHealth, currentLifeShardHealth, rowHeight, absorption, highlight);
    }

    private static void renderHearts(GuiGraphicsExtractor guiGraphics, Player player, Hud hud, int left, int top, int regen, float displayOverallHealth, float displayLifeShardHealth, int maxDefaultHealth, int lifeShardHealth, int rowHeight, int absorption, boolean highlight) {
        GuiAccessor guiAccessor = (GuiAccessor) hud;
        HeartType heartType = HeartType.forPlayer(player);
        int overallHearts = Mth.ceil((double) displayOverallHealth / 2.0);
        int lifeShardHearts = Mth.ceil((double) displayLifeShardHealth / 2.0);
        int maxDefaultHearts = Mth.ceil((double) maxDefaultHealth / 2.0);
        boolean tooManyHearts = overallHearts > 50;
        boolean tooLittleHearts = maxDefaultHearts < 10 && maxDefaultHearts > 0;
        for (int currentHeart = Math.min(overallHearts, lifeShardHearts - 1); currentHeart >= 0; --currentHeart) {
            int x = left + (currentHeart + (tooLittleHearts ? overallHearts - lifeShardHearts : 0)) % 10 * 8;
            int y = top - (currentHeart + (tooManyHearts ? 0 : maxDefaultHearts + currentHeart < 10 ? 0 : 10)) / 10 * rowHeight;

            if (Mth.ceil(player.getHealth()) + absorption <= 4) {
                y += guiAccessor.aether$getRandom().nextInt(2);
            }
            if (currentHeart + (maxDefaultHearts > 10 ? overallHearts - 10 : maxDefaultHearts) < overallHearts
                    && currentHeart + Math.min(maxDefaultHearts, 10) - (tooManyHearts ? overallHearts : 0) == regen) {
                y -= 2;
            }
            int selectedContainer = currentHeart * 2;
            if (highlight && selectedContainer < displayLifeShardHealth) {
                boolean halfHeart = selectedContainer + 1 == displayLifeShardHealth;
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getSprite(heartType, halfHeart, true), x, y, 9, 9);
            }
            if (selectedContainer < lifeShardHealth) {
                boolean halfHeart = selectedContainer + 1 == lifeShardHealth;
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getSprite(heartType, halfHeart, false), x, y, 9, 9);
            }
        }
    }

    private static Identifier getSprite(HeartType heartType, boolean halfHeart, boolean blinking) {
        if (heartType == HeartType.NORMAL) {
            return halfHeart
                    ? (!blinking ? TEXTURE_LIFE_SHARD_HALF : TEXTURE_LIFE_SHARD_HALF_BLINKING)
                    : (!blinking ? TEXTURE_LIFE_SHARD_FULL : TEXTURE_LIFE_SHARD_FULL_BLINKING);
        } else if (heartType == HeartType.POISONED) {
            return halfHeart
                    ? (!blinking ? TEXTURE_LIFE_SHARD_POISONED_HALF : TEXTURE_LIFE_SHARD_POISONED_HALF_BLINKING)
                    : (!blinking ? TEXTURE_LIFE_SHARD_POISONED_FULL : TEXTURE_LIFE_SHARD_POISONED_FULL_BLINKING);
        } else if (heartType == HeartType.WITHERED) {
            return halfHeart
                    ? (!blinking ? TEXTURE_LIFE_SHARD_WITHERED_HALF : TEXTURE_LIFE_SHARD_WITHERED_HALF_BLINKING)
                    : (!blinking ? TEXTURE_LIFE_SHARD_WITHERED_FULL : TEXTURE_LIFE_SHARD_WITHERED_FULL_BLINKING);
        } else if (heartType == HeartType.ABSORBING) {
            return !halfHeart ? TEXTURE_LIFE_SHARD_ABSORBING_FULL : TEXTURE_LIFE_SHARD_ABSORBING_HALF;
        } else if (heartType == HeartType.FROZEN) {
            return !halfHeart ? TEXTURE_LIFE_SHARD_FROZEN_FULL : TEXTURE_LIFE_SHARD_FROZEN_HALF;
        }
        return !halfHeart ? TEXTURE_LIFE_SHARD_FULL : TEXTURE_LIFE_SHARD_HALF;
    }

    private enum HeartType {
        NORMAL,
        POISONED,
        WITHERED,
        ABSORBING,
        FROZEN;

        private static HeartType forPlayer(Player player) {
            if (player.hasEffect(MobEffects.POISON)) {
                return POISONED;
            }
            if (player.hasEffect(MobEffects.WITHER)) {
                return WITHERED;
            }
            if (player.getAbsorptionAmount() > 0.0F) {
                return ABSORBING;
            }
            return NORMAL;
        }
    }
}
