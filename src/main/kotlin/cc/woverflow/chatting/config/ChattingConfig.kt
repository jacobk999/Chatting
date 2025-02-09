package cc.woverflow.chatting.config

import cc.woverflow.chatting.Chatting
import cc.woverflow.chatting.chat.ChatShortcuts
import cc.woverflow.chatting.chat.ChatTab
import cc.woverflow.chatting.chat.ChatTabs
import cc.woverflow.chatting.gui.ChatShortcutViewGui
import cc.woverflow.chatting.updater.DownloadGui
import cc.woverflow.chatting.updater.Updater
import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import java.awt.Color
import java.io.File

object ChattingConfig : Vigilant(File(Chatting.modDir, "${Chatting.ID}.toml"), Chatting.NAME, sortingBehavior = ConfigSorting) {

    @Property(
        type = PropertyType.SELECTOR,
        name = "Text Render Type",
        description = "Choose the type of rendering for the text.",
        category = "General",
        options = ["No Shadow", "Shadow", "Full Shadow"]
    )
    var textRenderType = 1

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Tooltip Background",
        description = "Remove the tooltip background.",
        category = "General"
    )
    var removeTooltipBackground = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Scroll Bar",
        description = "Remove the scroll bar.",
        category = "General"
    )
    var removeScrollBar = false

    @Property(
        type = PropertyType.COLOR,
        name = "Chat Background Color",
        description = "Change the color of the chat background.",
        category = "General",
        allowAlpha = false
    )
    var chatBackgroundColor = Color(0, 0, 0, 50)

    @Property(
        type = PropertyType.SWITCH,
        name = "Inform for Alternatives",
        description = "Inform the user if a mod they are using can be replaced by a feature in Chatting.",
        category = "General"
    )
    var informForAlternatives = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Custom Chat Height",
        description = "Allows you to change the height of chat to heights greater than before.",
        category = "Chat Window"
    )
    var customChatHeight = true

    @Property(
        type = PropertyType.SLIDER,
        min = 180,
        max = 10000,
        name = "Focused Height",
        description = "Height in pixels.",
        category = "Chat Window"
    )
    var focusedHeight = 180

    @Property(
        type = PropertyType.SLIDER,
        min = 180,
        max = 10000,
        name = "Unfocused Height",
        description = "Height in pixels.",
        category = "Chat Window"
    )
    var unfocusedHeight = 180

    @Property(
        type = PropertyType.SELECTOR,
        name = "Screenshot Mode",
        description = "The mode in which screenshotting will work.",
        category = "Screenshotting",
        options = [
            "Save To System",
            "Add To Clipboard",
            "Both"
        ]
    )
    var copyMode = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Searching",
        description = "Add a chat search bar.",
        category = "Searching"
    )
    var chatSearch = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Tabs",
        description = "Add chat tabs.",
        category = "Tabs"
    )
    var chatTabs = true
        get() {
            if (!field) return false
            return if (hypixelOnlyChatTabs) {
                EssentialAPI.getMinecraftUtil().isHypixel()
            } else {
                true
            }
        }

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Tabs Only on Hypixel",
        description = "Enable chat tabs only in Hypixel.",
        category = "Tabs"
    )
    var hypixelOnlyChatTabs = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Chat Shortcuts",
        description = "Add chat shortcuts.",
        category = "Shortcuts"
    )
    var chatShortcuts = false
        get() {
            if (!field) return false
            return if (hypixelOnlyChatShortcuts) {
                EssentialAPI.getMinecraftUtil().isHypixel()
            } else {
                true
            }
        }

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Shortcuts Only on Hypixel",
        description = "Enable chat shortcuts only in Hypixel.",
        category = "Shortcuts"
    )
    var hypixelOnlyChatShortcuts = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Edit Chat Shortcuts",
        description = "Edit chat shortcuts.",
        category = "Shortcuts"
    )
    fun openChatShortcutsGUI() {
        EssentialAPI.getGuiUtil().openScreen(ChatShortcutViewGui())
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "Updater"
    )
    var showUpdate = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadGui()) else EssentialAPI.getNotifications()
            .push(
                Chatting.NAME,
                "No update had been detected at startup, and thus the update GUI has not been shown."
            )
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "First Launch",
        category = "General",
        hidden = true
    )
    var firstLaunch = true

    init {
        initialize()
        registerListener("chatTabs") { funny: Boolean ->
            chatTabs = funny
            ChatTabs.initialize()
            if (!funny) {
                val dummy = ChatTab(true, "ALL", false, null, null, null, null, null, "")
                dummy.initialize()
                ChatTabs.currentTab = dummy
            } else {
                ChatTabs.currentTab = ChatTabs.tabs[0]
            }
        }
        registerListener("chatShortcuts") { funny: Boolean ->
            chatShortcuts = funny
            ChatShortcuts.initialize()
        }
    }

    private object ConfigSorting : SortingBehavior() {
        override fun getCategoryComparator(): Comparator<in Category> = Comparator { o1, o2 ->
            if (o1.name == "General") return@Comparator -1
            if (o2.name == "General") return@Comparator 1
            else compareValuesBy(o1, o2) {
                it.name
            }
        }
    }
}