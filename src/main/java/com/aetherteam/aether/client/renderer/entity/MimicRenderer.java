package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.MimicModel;
import com.aetherteam.aether.entity.monster.dungeon.Mimic;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import java.util.Calendar;

public class MimicRenderer extends MobRenderer<Mimic, LivingEntityRenderState, MimicModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/mimic/normal.png");
    private static final Identifier XMAS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/mimic/christmas.png");
    private static final Identifier LOOTR_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/mimic/lootr.png");

    private boolean isChristmas;

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context, new MimicModel(context.bakeLayer(AetherModelLayers.MIMIC)), 1.0F);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26) { // Time period when chests display as presents in Vanilla.
            this.isChristmas = true;
        }
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    /**
     * If the Lootr mod is installed or if it is Christmas, Mimics will have a custom texture.
     *
     * @param Mimic The {@link Mimic} entity.
     * @return The texture {@link Identifier}.
     */
    @Override
    public Identifier getTextureLocation(LivingEntityRenderState Mimic) {
        if (FabricLoader.getInstance().isModLoaded("lootr")) {
            return LOOTR_TEXTURE;
        }
        return this.isChristmas ? XMAS_TEXTURE : TEXTURE;
    }
}
