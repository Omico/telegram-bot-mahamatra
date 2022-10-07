package me.omico.telegram.bot.mahamatra.feature.ban

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatSenderChat
import eu.vendeli.tgbot.types.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.omico.telegram.bot.mahamatra.cachedConfiguration
import me.omico.telegram.bot.mahamatra.loadConfiguration
import me.omico.telegram.bot.utility.deleteMessage

suspend fun Message.setupBanForwardMessage(bot: TelegramBot) =
    coroutineScope { launch { bot.purify(this@setupBanForwardMessage) } }

private suspend fun TelegramBot.purify(message: Message, firstTime: Boolean = true) {
    val forwardFromChat = message.forwardFromChat ?: return
    when {
        forwardFromChat.id in cachedConfiguration.banForwardMessage.chatIds -> {
            deleteMessage(message)
            val user = message.from ?: return
            banChatSenderChat(user.id).send(to = message.chat.id, via = this)
        }
        firstTime -> {
            cachedConfiguration = loadConfiguration()
            purify(message, false)
        }
    }
}
