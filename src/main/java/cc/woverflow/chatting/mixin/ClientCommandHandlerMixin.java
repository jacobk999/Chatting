package cc.woverflow.chatting.mixin;

import cc.woverflow.chatting.chat.ChatShortcuts;
import cc.woverflow.chatting.config.ChattingConfig;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = ClientCommandHandler.class, remap = false)
public class ClientCommandHandlerMixin extends CommandHandler {
    @Redirect(method = "autoComplete", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ClientCommandHandler;getTabCompletionOptions(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;Lnet/minecraft/util/BlockPos;)Ljava/util/List;"))
    private List<String> addChatShortcuts(ClientCommandHandler instance, ICommandSender iCommandSender, String leftOfCursor, BlockPos blockPos) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        List<String> autocompleteList = instance.getTabCompletionOptions(mc.thePlayer, leftOfCursor, mc.thePlayer.getPosition());
        if (ChattingConfig.INSTANCE.getChatShortcuts()) {
            for (Pair<String, String> pair : ChatShortcuts.INSTANCE.getShortcuts()) {
                if (pair.getFirst().startsWith(leftOfCursor)) {
                    autocompleteList.add(pair.getFirst());
                }
            }
        }
        return autocompleteList;
    }
}
