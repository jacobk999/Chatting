package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.chat.ChatSearchingManager;
import cc.woverflow.chatting.chat.ChatShortcuts;
import cc.woverflow.chatting.chat.ChatTab;
import cc.woverflow.chatting.chat.ChatTabs;
import cc.woverflow.chatting.config.ChattingConfig;
import cc.woverflow.chatting.gui.components.ScreenshotButton;
import cc.woverflow.chatting.gui.components.SearchButton;
import cc.woverflow.chatting.hook.GuiNewChatHook;
import cc.woverflow.chatting.utils.ModCompatHooks;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.List;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen {

    @Unique
    private static final List<String> COPY_TOOLTIP = Lists.newArrayList(
            "\u00A7e\u00A7lCopy To Clipboard",
            "\u00A7b\u00A7lNORMAL CLICK\u00A7r \u00A78- \u00A77Full Message",
            "\u00A7b\u00A7lCTRL CLICK\u00A7r \u00A78- \u00A77Single Line",
            "\u00A7b\u00A7lSHIFT CLICK\u00A7r \u00A78- \u00A77Screenshot Line",
            "",
            "\u00A7e\u00A7lModifiers",
            "\u00A7b\u00A7lALT\u00A7r \u00A78- \u00A77Formatting Codes");

    private SearchButton searchButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch()) {
            searchButton = new SearchButton();
            buttonList.add(searchButton);
        }
        buttonList.add(new ScreenshotButton());
        if (ChattingConfig.INSTANCE.getChatTabs()) {
            for (ChatTab chatTab : ChatTabs.INSTANCE.getTabs()) {
                buttonList.add(chatTab.getButton());
            }
        }
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            searchButton.getInputField().updateCursorCounter();
        }
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (ChattingConfig.INSTANCE.getChatSearch() && searchButton.isEnabled()) {
            ci.cancel();
            if (keyCode == 1) {
                searchButton.onMousePress();
                return;
            }
            searchButton.getInputField().textboxKeyTyped(typedChar, keyCode);
            ChatSearchingManager.setPrevText(searchButton.getInputField().getText());
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.shouldCopy() && (hook.getRight() + ModCompatHooks.getXOffset() + 3) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 13 > x) {
            GuiUtils.drawHoveringText(COPY_TOOLTIP, mouseX, mouseY, width, height, -1, fontRendererObj);
            GlStateManager.disableLighting();
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        GuiNewChatHook hook = ((GuiNewChatHook) Minecraft.getMinecraft().ingameGUI.getChatGUI());
        float f = mc.ingameGUI.getChatGUI().getChatScale();
        int x = MathHelper.floor_float((float) mouseX / f);
        if (hook.shouldCopy() && (hook.getRight() + ModCompatHooks.getXOffset() + 3) <= x && (hook.getRight() + ModCompatHooks.getXOffset()) + 13 > x) {
            Transferable message = hook.getChattingChatComponent(Mouse.getY());
            if (message == null) return;
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(message, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ModifyArg(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;sendChatMessage(Ljava/lang/String;)V"), index = 0)
    private String modifySentMessage(String original) {
        if (ChattingConfig.INSTANCE.getChatShortcuts()) {
            if (original.startsWith("/")) {
                return "/" + ChatShortcuts.INSTANCE.handleSentCommand(StringUtils.substringAfter(original, "/"));
            }
        }
        return original;
    }
}
