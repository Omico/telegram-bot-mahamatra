package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.getChatMember
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.core.ManualHandlingDsl
import eu.vendeli.tgbot.interfaces.sendAsync
import eu.vendeli.tgbot.types.ChatMember
import eu.vendeli.tgbot.types.internal.getOrNull
import me.omico.telegram.bot.utility.deleteMessage

fun ManualHandlingDsl.setupShowForwardFromChatId(bot: TelegramBot) {
    onMessage {
        val message = data
        val text = message.text ?: return@onMessage
        if (!text.startsWith("/show_forward_from_chat_id")) return@onMessage
        val user = message.from ?: return@onMessage
        val chatMember = getChatMember(user.id)
            .sendAsync(to = message.chat.id, via = bot)
            .await()
            .getOrNull() ?: return@onMessage
        if (chatMember !is ChatMember.Owner && chatMember !is ChatMember.Administrator) {
            bot.deleteMessage(message)
            return@onMessage
        }
        val replyToMessage = message.replyToMessage ?: return@onMessage
        val forwardFromChat = replyToMessage.forwardFromChat ?: return@onMessage
        message("Message from ${forwardFromChat.id}").send(to = message.chat.id, via = bot)
    }
}
