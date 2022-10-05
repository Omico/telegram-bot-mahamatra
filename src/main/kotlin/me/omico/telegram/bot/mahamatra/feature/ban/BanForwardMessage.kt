package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatMember
import eu.vendeli.tgbot.core.ManualHandlingDsl
import eu.vendeli.tgbot.interfaces.sendAsync

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
    val user = message.from ?: return@onMessage
    banChatMember(userId = user.id, untilDate = 0, revokeMessages = true)
        .sendAsync(to = message.chat.id, via = bot)
        .await()
}

private const val BANNED_CHAT_ID_1 = -1001761534525L
private const val BANNED_CHAT_ID_2 = -1001881510191L
