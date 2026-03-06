package com.aetherteam.aether.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class AetherCommands {
    /**
     * Registers all Aether commands into the provided dispatcher.
     */
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        AetherTimeCommand.register(dispatcher);
        EternalDayCommand.register(dispatcher);
        PlayerCapabilityCommand.register(dispatcher);
        SunAltarWhitelistCommand.register(dispatcher);
    }
}
