package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.onCommand

context (TelegramBot)
    suspend fun Message.setupShowForwardFromChatId() =
    onCommand("/show_forward_from_chat_id") {
        chatOwnerOrAdministratorOnly {
            val forwardFromChat = replyToMessage?.forwardFromChat ?: return@chatOwnerOrAdministratorOnly
            message("Message from ${forwardFromChat.id}").send(to = chat.id, via = this@TelegramBot)
        }
    }
