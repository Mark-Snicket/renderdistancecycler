package net.mark.renderdistancecycler.mixin;

import net.mark.renderdistancecycler.RenderDistanceCyclerClient;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void debugFeedbackComponent(Component component);

    @Inject(method = "handleDebugKeys", at = @At("RETURN"), cancellable = true)
    public void cycleRenderDistance(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            // select case to use based on key used
            if (RenderDistanceCyclerClient.CYCLER_KEY.matches(event)) {
                cycleDynamic(event.hasShiftDown());
            } else if (RenderDistanceCyclerClient.CYCLER_KEY_UP.matches(event)) {
                cycleUp();
            } else if (RenderDistanceCyclerClient.CYCLER_KEY_DOWN.matches(event)) {
                cycleDown();
            }
        }

        Minecraft.getInstance().options.save();
        cir.setReturnValue(true);
    }

    @Unique
    private void cycleDynamic(boolean decrease) {
        OptionInstance<Integer> renderDistance = minecraft.options.renderDistance();
        OptionInstance.IntRange range = (OptionInstance.IntRange) renderDistance.values();

        renderDistance.set(Mth.clamp(renderDistance.get() + (decrease ? -1 : 1), range.minInclusive(), range.maxInclusive()));
        this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents("debug.render-distance-cycler.message", null, new Integer[]{renderDistance.get()})));
    }

    @Unique
    private void cycleUp() {
        OptionInstance<Integer> renderDistance = minecraft.options.renderDistance();
        OptionInstance.IntRange range = (OptionInstance.IntRange) renderDistance.values();

        renderDistance.set(Math.min(renderDistance.get() + 1, range.maxInclusive()));
        this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents("debug.render-distance-cycler.message", null, new Integer[]{renderDistance.get()})));
    }

    @Unique
    private void cycleDown() {
        OptionInstance<Integer> renderDistance = minecraft.options.renderDistance();
        OptionInstance.IntRange range = (OptionInstance.IntRange) renderDistance.values();

        renderDistance.set(Math.max(renderDistance.get() - 1, range.minInclusive()));
        this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents("debug.render-distance-cycler.message", null, new Integer[]{renderDistance.get()})));
    }
}