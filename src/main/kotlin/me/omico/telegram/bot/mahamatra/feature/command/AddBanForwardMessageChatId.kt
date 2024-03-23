package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.mahamatra.modifyConfiguration
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.onCommand
import me.omico.telegram.bot.utility.sendTimeLimited
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID = "/add_ban_forward_message_chat_id"

context (TelegramBot)
suspend fun Message.setupAddBanForwardMessageChatId() =
    onCommand(COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID) { text ->
        chatOwnerOrAdministratorOnly {
            runCatching { addBanForwardMessageChatId(text) }
                .onSuccess {
                    message("Added.").sendTimeLimited(
                        duration = 3.seconds,
                        to = chat.id,
                        via = this@TelegramBot,
                    )
                }
                .onFailure { exception ->
                    when (exception) {
                        is MissingCommandArgumentException -> "Usage: $COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID <chat_id>"
                        else -> "Add failed: ${exception.message ?: "Unknown error"}"
                    }.let(::message).sendTimeLimited(
                        duration = 1.minutes,
                        to = chat.id,
                        via = this@TelegramBot,
                    )
                }
        }
    }

private suspend fun addBanForwardMessageChatId(text: String) {
    val chatIds = text
        .removePrefix(COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID).trim()
        .split(" ", ",")
        .mapNotNull(String::toLongOrNull)
    if (chatIds.isEmpty()) throw MissingCommandArgumentException()
    modifyConfiguration {
        copy(
            banForwardMessage = banForwardMessage.copy(
                chatIds = banForwardMessage.chatIds + chatIds,
            ),
        )
    }
}
