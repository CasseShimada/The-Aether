package com.aetherteam.aether.integration.twilightforest.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class TwilightForestMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("Aether/TwilightForestCompat");
    private static final String CONSUME_METHOD = "consumeInventoryItem";
    private static final String CONSUME_DESCRIPTOR = "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/nbt/CompoundTag;Z)Z";
    private static boolean signatureWarningLogged;

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return FabricLoader.getInstance().isModLoaded("twilightforest")
            && targetClassName.equals("twilightforest.util.TFItemStackUtils");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        long matchingMethods = targetClass.methods.stream()
            .filter(method -> method.name.equals(CONSUME_METHOD))
            .filter(TwilightForestMixinPlugin::hasCurrentConsumeShape)
            .count();
        if (matchingMethods != 1 && !signatureWarningLogged) {
            signatureWarningLogged = true;
            LOGGER.error("Twilight Forest TFItemStackUtils.consumeInventoryItem(Player, ItemLike, CompoundTag, boolean) was not found exactly once; Aether charm-slot consumption compatibility will be skipped");
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean hasCurrentConsumeShape(MethodNode method) {
        return method.desc.equals(CONSUME_DESCRIPTOR);
    }
}
