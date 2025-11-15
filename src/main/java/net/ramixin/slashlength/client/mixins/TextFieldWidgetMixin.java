package net.ramixin.slashlength.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.ramixin.slashlength.TextFieldWidgetDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin implements TextFieldWidgetDuck {

    @Shadow private int selectionStart;
    @Shadow private int selectionEnd;

    @Unique
    private boolean isChatBox = false;

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;write(Ljava/lang/String;)V", ordinal = 0))
    private void preventTruncationIfPastingCommand(TextFieldWidget instance, String text, Operation<Void> original) {
        boolean isCommand = false;
        if(isChatBox) {
            if (Math.min(Math.min(this.selectionStart, this.selectionEnd), instance.getCursor()) > 0) {
                isCommand = instance.getText().startsWith("/");
            } else isCommand = text.startsWith("/");
        }
        if (isCommand) instance.setMaxLength(Integer.MAX_VALUE);
        else instance.setMaxLength(256);
        original.call(instance, text);
    }

    @Override
    public void slashlength$setAsChatBox() {
        isChatBox = true;
    }
}
