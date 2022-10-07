package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.mahamatra.modifyConfiguration
import me.omico.telegram.bot.utility.ifMessageFromChatOwnerOrAdministrator

private const val COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID = "/add_ban_forward_message_chat_id"

suspend fun Message.setupAddBanForwardMessageChatId(bot: TelegramBot) {
    val text = text ?: return
    if (!text.startsWith(COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID)) return
    if (!bot.ifMessageFromChatOwnerOrAdministrator(this)) return
    val chatIds = text
        .removePrefix(COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID).trim()
        .split(" ", ",")
        .mapNotNull(String::toLongOrNull)
    modifyConfiguration {
        copy(
            banForwardMessage = banForwardMessage.copy(
                chatIds = banForwardMessage.chatIds + chatIds,
            ),
        )
    }
}
