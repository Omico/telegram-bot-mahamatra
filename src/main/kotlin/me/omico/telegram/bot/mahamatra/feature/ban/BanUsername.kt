package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatMember
import eu.vendeli.tgbot.types.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.omico.telegram.bot.mahamatra.cachedConfiguration

context (TelegramBot)
suspend fun Message.setupBanUsername() {
    coroutineScope {
        launch {
            val userId = from?.id ?: return@launch
            val username = from?.username ?: return@launch
            cachedConfiguration.banUsername.regexes.forEach {
                if (!it.toRegex().matches(username)) return@forEach
                banChatMember(
                    userId = userId,
                    untilDate = null,
                    revokeMessages = true,
                ).send(to = chat.id, via = this@TelegramBot)
            }
        }
    }
}
