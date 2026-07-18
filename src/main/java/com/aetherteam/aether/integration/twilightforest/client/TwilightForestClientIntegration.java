package com.aetherteam.aether.integration.twilightforest.client;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TwilightForestClientIntegration {
    private static final Identifier CICADA_ITEM = Identifier.fromNamespaceAndPath("twilightforest", "cicada");
    private static final Identifier CICADA_SOUND = Identifier.fromNamespaceAndPath("twilightforest", "block.twilightforest.cicada");
    private static final Map<UUID, AccessoryCicadaSound> ACTIVE_CICADAS = new HashMap<>();
    private static Field silentCicadas;
    private static Field silentCicadasOnHead;
    private static boolean configFieldsResolved;

    private TwilightForestClientIntegration() {
    }

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("twilightforest")) {
            ClientTickEvents.END_CLIENT_TICK.register(TwilightForestClientIntegration::tick);
        }
    }

    private static void tick(Minecraft minecraft) {
        if (minecraft.level == null || !cicadaSoundEnabled()) {
            clear();
            return;
        }
        Set<UUID> tracked = new HashSet<>();
        for (var entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }
            UUID uuid = livingEntity.getUUID();
            tracked.add(uuid);
            AccessoryCicadaSound existing = ACTIVE_CICADAS.get(uuid);
            if (existing != null && existing.wearer != livingEntity) {
                existing.stopNow();
                ACTIVE_CICADAS.remove(uuid);
            }
            if (!hasVisibleAccessoryCicada(livingEntity) || ACTIVE_CICADAS.containsKey(livingEntity.getUUID())) {
                continue;
            }
            BuiltInRegistries.SOUND_EVENT.get(CICADA_SOUND).ifPresent(holder -> {
                AccessoryCicadaSound sound = new AccessoryCicadaSound(holder.value(), livingEntity);
                ACTIVE_CICADAS.put(livingEntity.getUUID(), sound);
                minecraft.getSoundManager().queueTickingSound(sound);
            });
        }
        ACTIVE_CICADAS.entrySet().removeIf(entry -> {
            AccessoryCicadaSound sound = entry.getValue();
            if (!tracked.contains(entry.getKey()) || !hasVisibleAccessoryCicada(sound.wearer)) {
                sound.stopNow();
                return true;
            }
            return sound.isStopped();
        });
    }

    private static boolean hasVisibleAccessoryCicada(LivingEntity entity) {
        if (entity.isRemoved() || !entity.isAlive() || itemId(entity.getItemBySlot(EquipmentSlot.HEAD)).equals(CICADA_ITEM)) {
            return false;
        }
        return !AccessoriesAPI.getAllVisible(entity, stack -> AccessorySlotResolver.isHeadTag(stack)
            && itemId(stack).equals(CICADA_ITEM)).isEmpty();
    }

    private static Identifier itemId(net.minecraft.world.item.ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private static boolean cicadaSoundEnabled() {
        resolveConfigFields();
        try {
            return silentCicadas == null || silentCicadasOnHead == null
                || (!silentCicadas.getBoolean(null) && !silentCicadasOnHead.getBoolean(null));
        } catch (IllegalAccessException ignored) {
            return true;
        }
    }

    private static void resolveConfigFields() {
        if (configFieldsResolved) {
            return;
        }
        configFieldsResolved = true;
        try {
            Class<?> config = Class.forName("twilightforest.config.TFConfig", false, TwilightForestClientIntegration.class.getClassLoader());
            silentCicadas = config.getField("silentCicadas");
            silentCicadasOnHead = config.getField("silentCicadasOnHead");
        } catch (ReflectiveOperationException ignored) {
            silentCicadas = null;
            silentCicadasOnHead = null;
        }
    }

    private static void clear() {
        ACTIVE_CICADAS.values().forEach(AccessoryCicadaSound::stopNow);
        ACTIVE_CICADAS.clear();
    }

    private static final class AccessoryCicadaSound extends AbstractTickableSoundInstance {
        private final LivingEntity wearer;

        private AccessoryCicadaSound(SoundEvent event, LivingEntity wearer) {
            super(event, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
            this.wearer = wearer;
            this.x = wearer.getX();
            this.y = wearer.getY();
            this.z = wearer.getZ();
            this.looping = true;
            this.delay = wearer.getRandom().nextInt(100) + 100;
        }

        @Override
        public void tick() {
            if (!hasVisibleAccessoryCicada(this.wearer)) {
                this.stop();
                return;
            }
            this.x = this.wearer.getX();
            this.y = this.wearer.getY();
            this.z = this.wearer.getZ();
        }

        @Override
        public boolean canPlaySound() {
            return cicadaSoundEnabled();
        }

        private void stopNow() {
            this.stop();
        }
    }
}
