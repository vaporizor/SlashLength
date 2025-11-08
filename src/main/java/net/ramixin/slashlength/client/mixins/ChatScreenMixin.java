package net.ramixin.slashlength.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.ramixin.slashlength.TextFieldWidgetDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At("TAIL"))
    private void markTextWidgetAsChatBox(CallbackInfo ci) {
        if(this.chatField != null)
            ((TextFieldWidgetDuck)this.chatField).slashlength$setAsChatBox();
    }

    @Inject(method = "onChatFieldUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;setWindowActive(Z)V", shift = At.Shift.BEFORE))
    private void modifyChatLengthIfCommand(String chatText, CallbackInfo ci) {
        if(this.chatField.getText().startsWith("/")) this.chatField.setMaxLength(Integer.MAX_VALUE);
        else this.chatField.setMaxLength(256);
    }

    @WrapOperation(method = "setChatFromHistory", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setText(Ljava/lang/String;)V"))
    private void allowCommandsInHistoryToPassLimit(TextFieldWidget instance, String text, Operation<Void> original) {
        if(text.startsWith("/")) instance.setMaxLength(Integer.MAX_VALUE);
        else instance.setMaxLength(256);
        original.call(instance, text);
    }

}
