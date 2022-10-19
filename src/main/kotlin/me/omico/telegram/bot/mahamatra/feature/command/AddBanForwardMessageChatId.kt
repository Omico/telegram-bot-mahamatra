package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.mahamatra.modifyConfiguration
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.onCommand

private const val COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID = "/add_ban_forward_message_chat_id"

context (TelegramBot)
    suspend fun Message.setupAddBanForwardMessageChatId() =
    onCommand(COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID) { text ->
        chatOwnerOrAdministratorOnly {
            val chatIds = text
                .removePrefix(COMMAND_ADD_BAN_FORWARD_MESSAGE_CHAT_ID).trim()
                .split(" ", ",")
                .mapNotNull(String::toLongOrNull)
            modifyConfiguration {
                copy(
                    banForwardMessage = banForwardMessage.copy(
                        chatIds = banForwardMessage.chatIds + chatIds,
                    ),
                )
            }
        }
    }
