package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.mixin.mixins.common.accessor.FolderRepositorySourceAccessor;
import com.aetherteam.aether.resource.AetherBuiltinPacks;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(value = PackRepository.class, priority = 900)
public abstract class PackRepositoryMixin {
    @Shadow
    @Final
    @Mutable
    private Set<RepositorySource> sources;

    /**
     * Fabric's public built-in pack API cannot preserve released slash-form IDs or merge classic pack roots.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void aether$registerBuiltinPacks(RepositorySource[] initialSources, CallbackInfo ci) {
        PackType packType = PackType.CLIENT_RESOURCES;
        for (RepositorySource source : initialSources) {
            if (source instanceof FolderRepositorySource) {
                packType = ((FolderRepositorySourceAccessor) source).aether$getPackType();
                break;
            }
        }

        Set<RepositorySource> updatedSources = new LinkedHashSet<>(this.sources);
        updatedSources.add(AetherBuiltinPacks.repositorySource(packType));
        this.sources = updatedSources;
    }
}
