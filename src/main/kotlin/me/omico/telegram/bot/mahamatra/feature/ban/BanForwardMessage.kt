package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatSenderChat
import eu.vendeli.tgbot.types.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.omico.telegram.bot.mahamatra.cachedConfiguration
import me.omico.telegram.bot.utility.deleteMessage

context (TelegramBot)
    suspend fun Message.setupBanForwardMessage() {
    coroutineScope {
        launch {
            val forwardFromChat = forwardFromChat ?: return@launch
            if (forwardFromChat.id !in cachedConfiguration.banForwardMessage.chatIds) return@launch
            deleteMessage(this@setupBanForwardMessage)
            banChatSenderChat(from?.id ?: return@launch)
                .send(to = chat.id, via = this@TelegramBot)
        }
    }
}
