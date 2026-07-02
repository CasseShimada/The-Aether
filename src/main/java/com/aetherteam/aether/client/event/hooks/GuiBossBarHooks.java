package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.entity.AetherBossMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GuiBossBarHooks {
    public static final Map<UUID, Integer> BOSS_EVENTS = new HashMap<>();

    private GuiBossBarHooks() {
    }

    public static void drawBossHealthBar(GuiGraphicsExtractor guiGraphics, int x, int y, LerpingBossEvent bossEvent) {
        Integer entityId = BOSS_EVENTS.get(bossEvent.getId());
        if (entityId == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !(minecraft.level.getEntity(entityId) instanceof AetherBossMob<?> aetherBossMob)) {
            return;
        }

        drawBar(guiGraphics, x + 2, y + 2, bossEvent, aetherBossMob);
        Component component = aetherBossMob.getBossName();
        int nameLength = minecraft.font.width(component);
        int nameX = minecraft.getWindow().getGuiScaledWidth() / 2 - nameLength / 2;
        guiGraphics.text(minecraft.font, component, nameX, y - 9, 16777215);
    }

    public static void drawBar(GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossEvent, AetherBossMob<?> aetherBossMob) {
        if (aetherBossMob.getBossBarBackgroundTexture() == null || aetherBossMob.getBossBarTexture() == null) {
            return;
        }

        x -= 37;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, aetherBossMob.getBossBarBackgroundTexture(), 256, 16, 0, 0, x, y, 256, 16);
        int health = (int) (bossEvent.getProgress() * 256.0F);
        if (health > 0) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, aetherBossMob.getBossBarTexture(), 256, 16, 0, 0, x, y, health, 16);
        }
    }

    public static boolean isAetherBossBar(UUID uuid) {
        return BOSS_EVENTS.containsKey(uuid);
    }
}
