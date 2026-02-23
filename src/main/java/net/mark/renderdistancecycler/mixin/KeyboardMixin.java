package net.mark.renderdistancecycler.mixin;

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
    public void cycleRenderDistance(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {


        if (!cir.getReturnValue() && keyEvent.key() == 70) {

            OptionInstance<Integer> renderDistance = minecraft.options.renderDistance();
            OptionInstance.IntRange callbacks = (OptionInstance.IntRange) renderDistance.values();

            renderDistance.set(Mth.clamp(renderDistance.get() + (keyEvent.hasShiftDown() ? -1 : 1), callbacks.minInclusive(), callbacks.maxInclusive()));
            this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents("debug.cycle_renderdistance.message", null, new Integer[]{renderDistance.get()})));

            Minecraft.getInstance().options.save();

            cir.setReturnValue(true);
        }
    }
}