package net.mark.renderdistancecycler;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class RenderDistanceCyclerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // renderDistance command
        CommandRegistrationCallback.EVENT.register(
                ((dispatcher, registry, environment) -> dispatcher.register(
                        CommandManager.literal("renderDistance")
                                .executes(RenderDistanceCyclerClient::getRenderDistance)
                                .then(CommandManager.argument("target_render_distance", IntegerArgumentType.integer( 2))
                                        .executes(RenderDistanceCyclerClient::setRenderDistance)))));
        // rd command
        CommandRegistrationCallback.EVENT.register(
                ((dispatcher, registry, environment) -> dispatcher.register(// renderDistance command alias: "rd"
                        CommandManager.literal("rd")
                                .executes(RenderDistanceCyclerClient::getRenderDistance)
                                .then(CommandManager.argument("target_render_distance", IntegerArgumentType.integer(2))
                                        .executes(RenderDistanceCyclerClient::setRenderDistance)))));
    }

    private static int setRenderDistance(CommandContext<ServerCommandSource> context) {
        SimpleOption<Integer> renderDistance = MinecraftClient.getInstance().options.getViewDistance();
        SimpleOption.ValidatingIntSliderCallbacks callbacks = (SimpleOption.ValidatingIntSliderCallbacks) renderDistance.getCallbacks();

        renderDistance.setValue(MathHelper.clamp(IntegerArgumentType.getInteger(context, "target_render_distance"), callbacks.minInclusive(), callbacks.maxInclusive()));

        context.getSource().getPlayer().sendMessage(
                getDebugMessage(MutableText.of(new TranslatableTextContent(
                        "debug.cycle_renderdistance.message", null, new Integer[]{renderDistance.getValue()}))));
        return 0;
    }

    private static int getRenderDistance(CommandContext<ServerCommandSource> context) {
        context.getSource().getPlayer().sendMessage(
                getDebugMessage(MutableText.of(new TranslatableTextContent(
                        "debug.cycle_renderdistance.message", null, new Integer[]{
                        MinecraftClient.getInstance().options.getViewDistance().getValue()
                }))));
        return 0;
    }

    private static Text getDebugMessage(Text message) {
        return Text.empty().append(Text.translatable("debug.prefix").formatted(Formatting.YELLOW, Formatting.BOLD)).append(ScreenTexts.SPACE).append(message);
    }
}
