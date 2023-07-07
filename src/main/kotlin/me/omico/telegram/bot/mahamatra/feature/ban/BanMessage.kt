package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatMember
import eu.vendeli.tgbot.types.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.omico.telegram.bot.mahamatra.cachedConfiguration
import me.omico.telegram.bot.utility.deleteMessage

context (TelegramBot)
suspend fun Message.setupBanMessage() {
    coroutineScope {
        launch {
            val text = text ?: return@launch
            cachedConfiguration.banMessage.keywordsSet.forEach { keywords ->
                if (keywords.all { it in text }) {
                    deleteMessage(this@setupBanMessage)
                    banChatMember(
                        userId = from?.id ?: return@launch,
                        untilDate = null,
                        revokeMessages = true,
                    ).send(to = chat.id, via = this@TelegramBot)
                    return@launch
                }
            }
        }
    }
}
