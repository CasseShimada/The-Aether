package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.command.AetherCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class CommandRegistrationHooks {
    private CommandRegistrationHooks() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        AetherCommands.registerCommands(dispatcher);
    }
}
