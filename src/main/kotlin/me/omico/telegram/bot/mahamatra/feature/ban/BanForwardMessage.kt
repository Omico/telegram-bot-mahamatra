package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatSenderChat
import eu.vendeli.tgbot.core.ManualHandlingDsl
import eu.vendeli.tgbot.interfaces.sendAsync
import me.omico.telegram.bot.utility.deleteMessage

fun ManualHandlingDsl.setupBanForwardMessage(
    bot: TelegramBot,
    chatIds: Set<Long> = setOf(BANNED_CHAT_ID_1), // TODO Load from configuration file
) = onMessage {
    val message = data
    val forwardFromChat = message.forwardFromChat ?: return@onMessage
    if (forwardFromChat.id !in chatIds) return@onMessage
    bot.deleteMessage(message)
    val user = message.from ?: return@onMessage
    banChatSenderChat(user.id)
        .sendAsync(to = message.chat.id, via = bot)
        .await()
}

private const val BANNED_CHAT_ID_1 = -1001761534525L
