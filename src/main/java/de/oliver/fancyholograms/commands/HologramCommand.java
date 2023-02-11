package de.oliver.fancyholograms.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.oliver.fancyholograms.ReflectionHelper;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HologramCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                CommandManager.literal("holo")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .then(CommandManager.argument("text", TextArgumentType.text())
                                        .executes(context -> execute(context.getSource(), context.getArgument("name", String.class), context.getArgument("text", Text.class)))
                                )
                        )
        );
    }


    private static int execute(ServerCommandSource source, String name, Text text){
        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, source.getWorld());
        entity.setPosition(source.getPosition());
        entity.getDataTracker().set((TrackedData<Text>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "TEXT"), text);
        entity.getDataTracker().set((TrackedData<Integer>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "BACKGROUND"), 0);
        entity.getDataTracker().set((TrackedData<Byte>) ReflectionHelper.getStaticValue(DisplayEntity.class, "BILLBOARD"), (byte) 3); // center

        source.getWorld().spawnEntity(entity);
        return 1;
    }

}
