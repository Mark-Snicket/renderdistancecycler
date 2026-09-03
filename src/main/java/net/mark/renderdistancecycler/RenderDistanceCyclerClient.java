package net.mark.renderdistancecycler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;

import java.util.Objects;

public class RenderDistanceCyclerClient implements ClientModInitializer {

    public static final KeyMapping CYCLER_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("key.debug.render-distance-cycler",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_F, KeyMapping.Category.DEBUG)
    );

    @Override
    public void onInitializeClient() {
        // renderDistance command
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, _) -> dispatcher.register(
                        ClientCommands.literal("renderDistance")
                                .executes(RenderDistanceCyclerClient::getRenderDistance)
                                .then(ClientCommands.argument("target_render_distance", IntegerArgumentType.integer(2))
                                        .executes(RenderDistanceCyclerClient::setRenderDistance)))));
        // rd command
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, _) -> dispatcher.register(
                        ClientCommands.literal("rd")
                                .executes(RenderDistanceCyclerClient::getRenderDistance)
                                .then(ClientCommands.argument("target_render_distance", IntegerArgumentType.integer(2))
                                        .executes(RenderDistanceCyclerClient::setRenderDistance)))));
    }

    private static int setRenderDistance(CommandContext<FabricClientCommandSource> context) {
        OptionInstance<Integer> renderDistance = Minecraft.getInstance().options.renderDistance();
        OptionInstance.IntRange range = (OptionInstance.IntRange) renderDistance.values();

        renderDistance.set(Mth.clamp(IntegerArgumentType.getInteger(context, "target_render_distance"), range.minInclusive(), range.maxInclusive()));

        Objects.requireNonNull(
                context.getSource().getPlayer()).sendSystemMessage(
                getDebugMessage(MutableComponent.create(new TranslatableContents(
                        "debug.render-distance-cycler.message", null, new Integer[]{renderDistance.get()}))));
        Minecraft.getInstance().options.save();
        return 0;
    }

    private static int getRenderDistance(CommandContext<FabricClientCommandSource> context) {
        Objects.requireNonNull(
                context.getSource().getPlayer()).sendSystemMessage(
                getDebugMessage(MutableComponent.create(new TranslatableContents(
                        "debug.render-distance-cycler.message", null, new Integer[]{
                        Minecraft.getInstance().options.renderDistance().get()
                }))));
        return 0;
    }

    private static Component getDebugMessage(Component message) {
        return Component.empty().append(Component.translatable("debug.prefix").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(message);
    }
}
