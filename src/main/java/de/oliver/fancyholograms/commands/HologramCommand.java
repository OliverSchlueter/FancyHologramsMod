package de.oliver.fancyholograms.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.oliver.fancyholograms.HologramManager;
import de.oliver.fancyholograms.mixinInterfaces.IDisplayEntityMixin;
import de.oliver.fancyholograms.mixinInterfaces.ITextDisplayEntityMixin;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class HologramCommand {

    private static final SuggestionProvider<ServerCommandSource> BILLBOARD_SUGGESTION_PROVIDER = (context, builder) -> {
        Arrays.stream(DisplayEntity.BillboardMode.values()).map(DisplayEntity.BillboardMode::asString).forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                CommandManager.literal("hologram")
                        .then(CommandManager.literal("create")
                            .then(CommandManager.argument("name", StringArgumentType.word())
                                    .executes(context -> executeCreate(context.getSource(), context.getArgument("name", String.class), Text.literal("New hologram")))
                                    .then(CommandManager.argument("text", TextArgumentType.text())
                                            .executes(context -> executeCreate(context.getSource(), context.getArgument("name", String.class), context.getArgument("text", Text.class)))
                                    )
                            )
                        )
                        .then(CommandManager.literal("edit")
                                .then(CommandManager.argument("hologram", HologramArgumentType.hologram())
                                        .then(CommandManager.literal("text")
                                            .then(CommandManager.argument("text", TextArgumentType.text())
                                                    .executes(context -> executeEditText(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("text", Text.class)))
                                            )
                                        )
                                        .then(CommandManager.literal("position")
                                                .then(CommandManager.argument("position", Vec3ArgumentType.vec3())
                                                        .executes(context -> executeEditPos(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("position", PosArgument.class)))
                                                )
                                        )
                                        .then(CommandManager.literal("background")
                                                .then(CommandManager.argument("background", ColorArgumentType.color())
                                                        .executes(context -> executeEditBackground(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("background", Formatting.class)))
                                                )
                                        )
                                        .then(CommandManager.literal("billboard")
                                                .then(CommandManager.argument("billboard", StringArgumentType.word()).suggests(BILLBOARD_SUGGESTION_PROVIDER)
                                                        .executes(context -> executeEditBillboard(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("billboard", String.class)))
                                                )
                                        )
                                        .then(CommandManager.literal("scale")
                                                .then(CommandManager.argument("scale", FloatArgumentType.floatArg())
                                                        .executes(context -> executeEditScale(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class), context.getArgument("scale", Float.class)))
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("hologram", HologramArgumentType.hologram())
                                        .executes(context -> executeRemove(context.getSource(), context.getArgument("hologram", DisplayEntity.TextDisplayEntity.class)))
                                )
                        )
        );
    }


    private static int executeCreate(ServerCommandSource source, String name, Text text){
        if (HologramManager.getHologram(name) != null) {
            source.sendError(Text.literal("A hologram with this name already exists: '" + name + "'"));
            return 0;
        }
        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, source.getWorld());
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

        entity.setPosition(source.getPosition());
        entity.getDataTracker().set(textDisplayEntityMixin.getTextData(), text);
        entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
        entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3); // center

        textDisplayEntityMixin.setIsHologram(true);
        textDisplayEntityMixin.setHologramName(name);

        source.getWorld().spawnEntity(entity);
        source.getPlayer().sendMessage(Text.literal("Created new hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditText(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, Text text){
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) hologram;
        hologram.getDataTracker().set(textDisplayEntityMixin.getTextData(), text);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditPos(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, PosArgument pos){
        hologram.setPosition(pos.toAbsolutePos(source));
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditBackground(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, Formatting color){
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) hologram;
        hologram.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), color.getColorValue() | 0xC8000000);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditBillboard(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, String billboardName){
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) hologram;

        byte index = 3;

        switch (billboardName.toLowerCase()){
            case "fixed" -> index = 0;
            case "vertical" -> index = 1;
            case "horizontal" -> index = 2;
            case "center" -> index = 3;
            default -> {
                source.sendError(Text.literal("Unknown billboard: '" + billboardName + "'").formatted(Formatting.RED));
                return 0;
            }
        }

        hologram.getDataTracker().set(displayEntityMixin.getBillboardData(), index);
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeEditScale(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram, Float scale){
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) hologram;
        hologram.getDataTracker().set(displayEntityMixin.getScaleData(), new Vector3f(scale, scale, scale));
        source.getPlayer().sendMessage(Text.literal("Edited hologram").formatted(Formatting.GREEN));
        return 1;
    }

    private static int executeRemove(ServerCommandSource source, DisplayEntity.TextDisplayEntity hologram){
        ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
        displayEntityMixin.setIsHologram(false);

        hologram.kill();
        HologramManager.removeHologram(displayEntityMixin.getHologramName());
        source.getPlayer().sendMessage(Text.literal("Removed hologram").formatted(Formatting.GREEN));
        return 1;
    }


    public static class HologramArgumentType implements ArgumentType<DisplayEntity.TextDisplayEntity>{

        private HologramArgumentType(){ }

        public static HologramArgumentType hologram(){
            return new HologramArgumentType();
        }

        @Override
        public DisplayEntity.TextDisplayEntity parse(StringReader reader) throws CommandSyntaxException {
            String name = reader.readUnquotedString();

            DisplayEntity.TextDisplayEntity entity = (DisplayEntity.TextDisplayEntity) HologramManager.getHologram(name);

            if(entity == null){
                throw new DynamicCommandExceptionType(nameArg -> Text.literal("Unknown hologram: '" + nameArg + "'").formatted(Formatting.RED)).create(name);
            } else {
                return entity;
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            HologramManager.getAllNames().forEach(builder::suggest);
            return builder.buildFuture();
        }
    }
}
