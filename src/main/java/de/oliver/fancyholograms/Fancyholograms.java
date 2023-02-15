package de.oliver.fancyholograms;

import de.oliver.fancyholograms.commands.HologramCommand;
import de.oliver.fancyholograms.mixinInterfaces.IDisplayEntityMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.decoration.DisplayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fancyholograms implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("FancyHolograms");

    @Override
    public void onInitialize() {
        ArgumentTypesAccessor.fabric_getClassMap().put(HologramCommand.HologramArgumentType.class, ConstantArgumentSerializer.of(HologramCommand.HologramArgumentType::hologram));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> HologramCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> HologramManager.clear());

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if(!(entity instanceof DisplayEntity displayEntity)){
                return;
            }

            IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) displayEntity;
            if (displayEntityMixin.isHologram()) {
                HologramManager.addHologram(displayEntityMixin.getHologramName(), entity);
                LOGGER.info("Loaded the " + displayEntityMixin.getHologramName() + " hologram");
            }
        });

    }
}
