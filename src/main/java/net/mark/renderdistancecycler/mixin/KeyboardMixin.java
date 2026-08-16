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

        if (!cir.getReturnValue() && RenderDistanceCyclerClient.CYCLER_KEY.matches(event)) {

            OptionInstance<Integer> renderDistance = minecraft.options.renderDistance();
            OptionInstance.IntRange range = (OptionInstance.IntRange) renderDistance.values();

            renderDistance.set(Mth.clamp(renderDistance.get() + (event.hasShiftDown() ? -1 : 1), range.minInclusive(), range.maxInclusive()));
            this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents("debug.render-distance-cycler.message", null, new Integer[]{renderDistance.get()})));

            Minecraft.getInstance().options.save();
            cir.setReturnValue(true);
        }
    }
}