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


    private void debugLog(String key, Object[] value) {
        this.debugFeedbackComponent(MutableComponent.create(new TranslatableContents(key, (String)null, value)));
    }

    @Inject(method = "handleDebugKeys", at = @At("RETURN"), cancellable = true)
    public void tryCycleRenderDistance(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && input.key() == 70) {
            OptionInstance<Integer> renderDistance = minecraft.options.renderDistance();
            OptionInstance.IntRange callbacks = (OptionInstance.IntRange) renderDistance.values();

            renderDistance.set(Mth.clamp(renderDistance.get() + (input.hasShiftDown() ? -1 : 1), callbacks.minInclusive(), callbacks.maxInclusive()));
            this.debugLog("debug.cycle_renderdistance.message", new Integer[]{renderDistance.get()});
            cir.setReturnValue(true);
        }
    }
    /* not required, so comment out for now *//*
    //@Inject(method = "handleDebugKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V", ordinal = 4))
    public void addCycleRenderHelpMessage(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        this.sendMessage(Component.translatable("debug.cycle_renderdistance.help"));
    }

    private void sendMessage(MutableComponent translatable) {}*/
}