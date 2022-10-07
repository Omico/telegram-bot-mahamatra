package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.utility.deleteMessage
import me.omico.telegram.bot.utility.ifMessageFromChatOwnerOrAdministrator

suspend fun Message.setupShowForwardFromChatId(bot: TelegramBot) {
    val text = text ?: return
    if (!text.startsWith("/show_forward_from_chat_id")) return
    if (!bot.ifMessageFromChatOwnerOrAdministrator(this)) {
        bot.deleteMessage(this)
        return
    }
    val forwardFromChat = replyToMessage?.forwardFromChat ?: return
    message("Message from ${forwardFromChat.id}").send(to = chat.id, via = bot)
}
