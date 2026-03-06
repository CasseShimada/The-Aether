package com.aetherteam.aether.client.renderer.entity.state;

import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public class MoaRenderState extends BipedBirdRenderState {
    public boolean renderLegs;
    public boolean sitting;
    public boolean saddle;
    public ResourceKey<MoaType> type = AetherMoaTypes.BLUE;
    public Identifier location;
    public Identifier saddleLocation;

    public UUID lastRider;
    public UUID rider;
    public UUID moaUUID;

    @Override
    public void clearExtraData() {
    }

}
