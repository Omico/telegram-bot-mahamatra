package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatSenderChat
import eu.vendeli.tgbot.core.ManualHandlingDsl
import me.omico.telegram.bot.utility.deleteMessage

fun ManualHandlingDsl.setupBanForwardMessage(
    bot: TelegramBot,
    chatIds: Set<Long> = setOf(
        BANNED_CHAT_ID_1,
        BANNED_CHAT_ID_2,
    ), // TODO Load from configuration file
) = onMessage {
    val message = data
    val forwardFromChat = message.forwardFromChat ?: return@onMessage
    if (forwardFromChat.id !in chatIds) return@onMessage
    bot.deleteMessage(message)
    val user = message.from ?: return@onMessage
    banChatSenderChat(user.id).send(to = message.chat.id, via = bot)
}

private const val BANNED_CHAT_ID_1 = -1001761534525L
private const val BANNED_CHAT_ID_2 = -1001881510191L
