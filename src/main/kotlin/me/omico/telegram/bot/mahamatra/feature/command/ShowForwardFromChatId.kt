package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.onCommand
import me.omico.telegram.bot.utility.sendTimeLimited
import kotlin.time.Duration.Companion.minutes

context (TelegramBot)
suspend fun Message.setupShowForwardFromChatId() =
    onCommand("/show_forward_from_chat_id") {
        chatOwnerOrAdministratorOnly {
            when (val forwardFromChat = replyToMessage?.forwardOrigin?.fromChat) {
                null -> "Usage: Reply /show_forward_from_chat_id to a message."
                else -> "Message from ${forwardFromChat.id}"
            }.let(::message).sendTimeLimited(
                duration = 1.minutes,
                to = chat.id,
                via = this@TelegramBot,
            )
        }
    }
