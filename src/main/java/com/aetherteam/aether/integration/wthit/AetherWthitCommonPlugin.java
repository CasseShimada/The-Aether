package com.aetherteam.aether.integration.wthit;

import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.api.data.ItemData;

/** Common/server half of the optional WTHIT integration. */
public final class AetherWthitCommonPlugin implements IWailaCommonPlugin {
    public static final String MIMIC_MASKED_KEY = "aether:mimic_masked";

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.blockData(MimicDataProvider.INSTANCE, ChestMimicBlockEntity.class);
    }

    private enum MimicDataProvider implements IDataProvider<ChestMimicBlockEntity> {
        INSTANCE;

        @Override
        public void appendData(
                IDataWriter data,
                IServerAccessor<ChestMimicBlockEntity> accessor,
                IPluginConfig config
        ) {
            boolean masked = !accessor.getPlayer().isCreative() && !accessor.getPlayer().isSpectator();
            data.raw().putBoolean(MIMIC_MASKED_KEY, masked);
            if (masked) {
                data.blockAll(ItemData.TYPE);
            }
        }
    }
}
