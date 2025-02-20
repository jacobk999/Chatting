package cc.woverflow.chatting.gui.components

import cc.woverflow.chatting.chat.ChatTab
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.config.ChattingConfig
import gg.essential.universal.UResolution

class TabButton(buttonId: Int, x: Int, widthIn: Int, heightIn: Int, private val chatTab: ChatTab) :
    CleanButton(buttonId, { x }, {
        UResolution.scaledHeight - 26
    }, widthIn, heightIn, chatTab.name, { RenderType.values()[ChattingConfig.textRenderType] }) {

    override fun onMousePress() {
        ChatTabs.currentTab = chatTab
    }

    override fun isEnabled(): Boolean {
        return chatTab != ChatTabs.currentTab
    }
}