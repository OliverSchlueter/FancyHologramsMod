package de.oliver.fancyholograms.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.oliver.fancyholograms.HologramManager;
import de.oliver.fancyholograms.ReflectionHelper;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HologramCommand {

    private static final SuggestionProvider<ServerCommandSource> HOLOGRAM_NAMES_SUGGESTION_PROVIDER = (context, builder) -> {
        HologramManager.getAllNames().forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                CommandManager.literal("hologram")
                        .then(CommandManager.literal("create")
                            .then(CommandManager.argument("name", StringArgumentType.word())
                                    .then(CommandManager.argument("text", TextArgumentType.text())
                                            .executes(context -> executeCreate(context.getSource(), context.getArgument("name", String.class), context.getArgument("text", Text.class)))
                                    )
                            )
                        )
                        .then(CommandManager.literal("edit")
                                .then(CommandManager.argument("name", StringArgumentType.word()).suggests(HOLOGRAM_NAMES_SUGGESTION_PROVIDER)
                                        .then(CommandManager.literal("text")
                                            .then(CommandManager.argument("text", TextArgumentType.text())
                                                    .executes(context -> executeEditText(context.getSource(), context.getArgument("name", String.class), context.getArgument("text", Text.class)))
                                            )
                                        )
                                        .then(CommandManager.literal("position")
                                                .then(CommandManager.argument("position", Vec3ArgumentType.vec3())
                                                        .executes(context -> executeEditPos(context.getSource(), context.getArgument("name", String.class), context.getArgument("position", PosArgument.class)))
                                                )
                                        )
                                        .then(CommandManager.literal("background")
                                                .then(CommandManager.argument("background", net.minecraft.command.argument.ColorArgumentType.color())
                                                        .executes(context -> executeEditBackground(context.getSource(), context.getArgument("name", String.class), context.getArgument("background", Formatting.class)))
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("name", StringArgumentType.word()).suggests(HOLOGRAM_NAMES_SUGGESTION_PROVIDER)
                                        .executes(context -> executeRemove(context.getSource(), context.getArgument("name", String.class)))
                                )
                        )
        );
    }


    private static int executeCreate(ServerCommandSource source, String name, Text text){
        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, source.getWorld());
        entity.setPosition(source.getPosition());
        entity.getDataTracker().set((TrackedData<Text>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "TEXT"), text);
        entity.getDataTracker().set((TrackedData<Integer>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "BACKGROUND"), 0);
        entity.getDataTracker().set((TrackedData<Byte>) ReflectionHelper.getStaticValue(DisplayEntity.class, "BILLBOARD"), (byte) 3); // center

        source.getWorld().spawnEntity(entity);
        HologramManager.addHologram(name, entity);
        source.getPlayer().sendMessage(Text.literal("Created new hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditText(ServerCommandSource source, String name, Text text){
        DisplayEntity.TextDisplayEntity entity = (DisplayEntity.TextDisplayEntity) HologramManager.getHologram(name);
        entity.getDataTracker().set((TrackedData<Text>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "TEXT"), text);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditPos(ServerCommandSource source, String name, PosArgument pos){
        DisplayEntity.TextDisplayEntity entity = (DisplayEntity.TextDisplayEntity) HologramManager.getHologram(name);
        entity.setPosition(pos.toAbsolutePos(source));
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditBackground(ServerCommandSource source, String name, Formatting color){
        DisplayEntity.TextDisplayEntity entity = (DisplayEntity.TextDisplayEntity) HologramManager.getHologram(name);
        entity.getDataTracker().set((TrackedData<Integer>) ReflectionHelper.getStaticValue(DisplayEntity.TextDisplayEntity.class, "BACKGROUND"), color.getColorValue() | 0xC8000000);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeRemove(ServerCommandSource source, String name){
        DisplayEntity.TextDisplayEntity entity = (DisplayEntity.TextDisplayEntity) HologramManager.getHologram(name);
        entity.kill();
        HologramManager.removeHologram(name);
        source.getPlayer().sendMessage(Text.literal("Removed hologram").formatted(Formatting.GREEN));
        return 1;
    }

}
