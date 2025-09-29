package net.mark.renderdistancecycler.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void debugLog(String key);


    private void sendMessage(MutableText translatable) {}

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    public void tryCycleRenderDistance(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && input.key() == 70) {
            SimpleOption<Integer> renderDistance = client.options.getViewDistance();
            SimpleOption.ValidatingIntSliderCallbacks callbacks = (SimpleOption.ValidatingIntSliderCallbacks) renderDistance.getCallbacks();

            renderDistance.setValue(MathHelper.clamp(renderDistance.getValue() + (input.hasShift() ? -1 : 1), callbacks.minInclusive(), callbacks.maxInclusive()));
            this.debugLog("debug.cycle_renderdistance.message." + renderDistance.getValue());
            cir.setReturnValue(true);
        }
    }
    /* not required, so comment out for now */
    //@Inject(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V", ordinal = 4))
    public void addCycleRenderHelpMessage(int key, CallbackInfoReturnable<Boolean> cir) {
        this.sendMessage(Text.translatable("debug.cycle_renderdistance.help"));
    }

}