package net.mark.renderdistancecycler.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {

    @Shadow
    protected abstract void showDebugChat(Component component);

    @Shadow
    protected abstract void debugFeedbackComponent(Component component);

    @Inject(method = "handleChunkDebugKeys", at = @At("RETURN"), cancellable = true)
    public void sendHelpMessageOrCycleRenderDistance(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 81) {
            this.showDebugChat(Component.translatable("debug.cycle_renderdistance.help"));
        }
        else if (!cir.getReturnValue() && key == 70) {
            OptionInstance<Integer> renderDistance = Minecraft.getInstance().options.renderDistance();
            OptionInstance.IntRange range = (OptionInstance.IntRange) renderDistance.values();

            renderDistance.set(Mth.clamp(renderDistance.get() + (Screen.hasShiftDown() ? -1 : 1), range.minInclusive(), range.maxInclusive()));
            this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents("debug.cycle_renderdistance.message", null, new Integer[]{renderDistance.get()})));

            Minecraft.getInstance().options.save();
            cir.setReturnValue(true);
        }
    }
}