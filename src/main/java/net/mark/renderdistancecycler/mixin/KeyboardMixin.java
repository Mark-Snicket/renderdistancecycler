package net.mark.renderdistancecycler.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    @Shadow
    protected abstract void sendMessage(Text translatable);

    @Shadow
    protected abstract void debugLog(Text text);

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    public void sendHelpMessageOrCycleRenderDistance(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 81) {
            this.sendMessage(Text.translatable("debug.cycle_renderdistance.help"));
        }
        else if (!cir.getReturnValue() && key == 70) {
            SimpleOption<Integer> renderDistance = MinecraftClient.getInstance().options.getViewDistance();
            SimpleOption.ValidatingIntSliderCallbacks callbacks = (SimpleOption.ValidatingIntSliderCallbacks) renderDistance.getCallbacks();

            renderDistance.setValue(MathHelper.clamp(renderDistance.getValue() + (Screen.hasShiftDown() ? -1 : 1), callbacks.minInclusive(), callbacks.maxInclusive()));
            this.debugLog(MutableText.of(new TranslatableTextContent("debug.cycle_renderdistance.message", null, new Integer[]{renderDistance.getValue()})));

            MinecraftClient.getInstance().options.write();
            cir.setReturnValue(true);
        }
    }
}