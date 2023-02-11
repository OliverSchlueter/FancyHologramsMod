package de.oliver.fancyholograms;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Fancyholograms implements ModInitializer {

    @Override
    public void onInitialize() {
        Command<ServerCommandSource> command = context -> {

            DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, context.getSource().getWorld());
            entity.setPosition(context.getSource().getPosition());
            entity.getDataTracker().set((TrackedData<Text>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "TEXT"), Text.literal("Hello world"));
            entity.getDataTracker().set((TrackedData<Integer>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "BACKGROUND"), 0);
            entity.getDataTracker().set((TrackedData<Byte>) ReflectionHelper.getStaticValue(DisplayEntity.class, "BILLBOARD"), (byte) 3); // center

            context.getSource().getWorld().spawnEntity(entity);
            return 1;
        };

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("holo").executes(command)));
    }
}
