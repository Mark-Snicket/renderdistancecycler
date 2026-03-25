package net.mark.renderdistancecycler;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;

import java.util.Objects;

public class RenderDistanceCyclerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // renderDistance command
        CommandRegistrationCallback.EVENT.register(
                ((dispatcher, registry, environment) -> dispatcher.register(
                        Commands.literal("renderDistance")
                                .executes(RenderDistanceCyclerClient::getRenderDistance)
                                .then(Commands.argument("target_render_distance", IntegerArgumentType.integer( 2))
                                        .executes(RenderDistanceCyclerClient::setRenderDistance)))));
        // rd command
        CommandRegistrationCallback.EVENT.register(
                ((dispatcher, registry, environment) -> dispatcher.register(// renderDistance command alias: "rd"
                        Commands.literal("rd")
                                .executes(RenderDistanceCyclerClient::getRenderDistance)
                                .then(Commands.argument("target_render_distance", IntegerArgumentType.integer(2))
                                        .executes(RenderDistanceCyclerClient::setRenderDistance)))));
    }

    private static int setRenderDistance(CommandContext<CommandSourceStack> context) {
        OptionInstance<Integer> renderDistance = Minecraft.getInstance().options.renderDistance();
        OptionInstance.IntRange range = (OptionInstance.IntRange)  renderDistance.values();

        renderDistance.set(Mth.clamp(IntegerArgumentType.getInteger(context, "target_render_distance"), range.minInclusive(), range.maxInclusive()));

        Objects.requireNonNull(
            context.getSource().getPlayer()).displayClientMessage(
                getDebugMessage(MutableComponent.create(new TranslatableContents(
                        "debug.cycle_renderdistance.message", null, new Integer[]{renderDistance.get()}))), false);

        Minecraft.getInstance().options.save();
        return 0;
    }

    private static int getRenderDistance(CommandContext<CommandSourceStack> context) {
        Objects.requireNonNull(
            context.getSource().getPlayer()).displayClientMessage(
                getDebugMessage(MutableComponent.create(new TranslatableContents(
                        "debug.cycle_renderdistance.message", null, new Integer[]{
                        Minecraft.getInstance().options.renderDistance().get()
                }))), false);
        return 0;
    }

    private static Component getDebugMessage(Component message) {
        return Component.empty().append(Component.translatable("debug.prefix").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(message);
    }
}
