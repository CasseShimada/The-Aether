package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.entity.ai.attribute.AetherAttributes;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.item.AetherItems;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

final class AetherStatusHudRendering {
    private static final Identifier TEXTURE_COOLDOWN_BAR = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/cooldown");
    private static final Identifier TEXTURE_COOLDOWN_BAR_BACKGROUND = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/cooldown_background");
    static final Identifier TEXTURE_DEFAULT_JUMPS = Identifier.fromNamespaceAndPath(Aether.MODID, "hud/jumps");

    private AetherStatusHudRendering() {
    }

    static void renderHammerCooldownOverlay(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, Window window, LocalPlayer player) {
        if (!AetherConfig.CLIENT.enable_hammer_cooldown_overlay.get() || minecraft.gui.hud.isHidden()) {
            return;
        }

        Inventory inventory = player.getInventory();
        if (!inventory.contains(itemStack -> itemStack.is(AetherItems.HAMMER_OF_KINGBDOGZ))) {
            return;
        }

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (!itemStack.is(AetherItems.HAMMER_OF_KINGBDOGZ)) {
                continue;
            }

            float cooldownPercent = player.getCooldowns().getCooldownPercent(itemStack, 0.0F);
            if (cooldownPercent <= 0.0F) {
                continue;
            }

            ItemStack displayStack = resolveDisplayedHammer(player, itemStack);
            String text = displayStack.getHoverName().getString().concat(" ").concat(Component.translatable("aether.hammer_of_kingbdogz_cooldown").getString());
            guiGraphics.text(minecraft.font, text, (int) ((window.getGuiScaledWidth() / 2.0F) - (minecraft.font.width(text) / 2.0F)), 32, 16777215);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE_COOLDOWN_BAR_BACKGROUND, 128, 8, 0, 0, window.getGuiScaledWidth() / 2 - 64, 42, 128, 8);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURE_COOLDOWN_BAR, 128, 8, 0, 0, window.getGuiScaledWidth() / 2 - 64, 42, (int) (cooldownPercent * 128), 8);
            return;
        }
    }

    static void renderMoaJumps(GuiGraphicsExtractor guiGraphics, Window window, LocalPlayer player) {
        if (!(player.getVehicle() instanceof Moa moa) || Minecraft.getInstance().gui.hud.isHidden()) {
            return;
        }

        for (int jumpCount = 0; jumpCount < moa.getMaxJumps(); jumpCount++) {
            int xPos = ((window.getGuiScaledWidth() / 2) + (jumpCount * 8)) - (moa.getMaxJumps() * 8) / 2;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, appendBackground(jumpCount >= moa.getRemainingJumps(), getMoaJumpTexture(moa, jumpCount)), xPos, 18, 9, 11);
        }
    }

    static Identifier getDefaultJumpsTexture(@Nullable MoaType type) {
        if (type == null) {
            return TEXTURE_DEFAULT_JUMPS;
        }
        return type.jumpsTexture().orElse(TEXTURE_DEFAULT_JUMPS);
    }

    private static ItemStack resolveDisplayedHammer(LocalPlayer player, ItemStack inventoryStack) {
        if (player.getMainHandItem().is(AetherItems.HAMMER_OF_KINGBDOGZ)) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().is(AetherItems.HAMMER_OF_KINGBDOGZ)) {
            return player.getOffhandItem();
        }
        return inventoryStack;
    }

    private static Identifier getMoaJumpTexture(Moa moa, double count) {
        AttributeInstance instance = moa.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(AetherAttributes.MOA_MAX_JUMPS));
        if (instance != null) {
            if (count < instance.getBaseValue()) {
                return getDefaultJumpsTexture(moa.getMoaType());
            }

            Set<AttributeModifier> modifiers = instance.getModifiers();
            double currentCount = instance.getBaseValue();
            for (AttributeModifier modifier : modifiers) {
                if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                    currentCount += instance.getBaseValue() * modifier.amount();
                } else {
                    currentCount += modifier.amount();
                }

                if (currentCount >= count) {
                    return moa.getOverlayTexture(modifier.id());
                }
            }
        }
        return TEXTURE_DEFAULT_JUMPS;
    }

    private static Identifier appendBackground(boolean background, Identifier location) {
        return background ? location.withSuffix("_background") : location;
    }
}
