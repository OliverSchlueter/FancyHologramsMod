package de.oliver.fancyholograms;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

public class Fancyholograms implements ModInitializer {

    private static Object getStaticValue(Class clazz, String fieldName){
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object val = field.get(null);
            field.setAccessible(false);
            return val;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onInitialize() {
        Command<ServerCommandSource> command = context -> {
            DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, context.getSource().getWorld());
            entity.setPosition(context.getSource().getPosition());
            entity.getDataTracker().set((TrackedData<Text>) getStaticValue(DisplayEntity.TextDisplayEntity.class, "TEXT"), Text.literal("Hello world"));
            entity.getDataTracker().set((TrackedData<Integer>) getStaticValue(DisplayEntity.TextDisplayEntity.class, "BACKGROUND"), 0);
            entity.getDataTracker().set((TrackedData<Byte>) getStaticValue(DisplayEntity.class, "BILLBOARD"), (byte) 3); // center

            context.getSource().getWorld().spawnEntity(entity);
            return 1;
        };

        CommandRegistrationCallback.EVENT.register(new CommandRegistrationCallback() {
            @Override
            public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
                dispatcher.register(CommandManager.literal("holo").executes(command));
            }
        });
    }
}
