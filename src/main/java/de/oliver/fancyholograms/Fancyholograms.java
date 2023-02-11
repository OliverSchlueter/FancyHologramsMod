package de.oliver.fancyholograms;

import de.oliver.fancyholograms.commands.HologramCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Fancyholograms implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> HologramCommand.register(dispatcher));
    }
}
