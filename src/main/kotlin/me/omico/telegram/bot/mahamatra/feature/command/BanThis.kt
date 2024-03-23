package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatMember
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.User
import me.omico.telegram.bot.mahamatra.modifyConfiguration
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.deleteMessage
import me.omico.telegram.bot.utility.onCommand
import me.omico.telegram.bot.utility.send
import kotlin.time.Duration.Companion.seconds

private const val COMMAND_BAN_THIS = "/ban_this"

context (TelegramBot)
suspend fun Message.setupBanThis() =
    onCommand(COMMAND_BAN_THIS) {
        chatOwnerOrAdministratorOnly {
            when (val replyToMessage = replyToMessage) {
                null -> help()
                else -> {
                    val forwardFromChat = replyToMessage.forwardOrigin?.fromChat
                    val sender = replyToMessage.from
                    when {
                        sender == null -> banSenderNotFoundError()
                        forwardFromChat == null -> banChatMember(sender)
                        else -> banForwardMessage(sender, forwardFromChat.id)
                    }
                }
            }
        }
    }

context (TelegramBot)
private suspend fun Message.help() {
    message("Usage: Reply $COMMAND_BAN_THIS to a message.")
        .send(
            to = chat.id,
            via = this@TelegramBot,
            duration = 15.seconds,
            onTimeout = { message ->
                this@TelegramBot.deleteMessage(message)
                this@TelegramBot.deleteMessage(this@help)
            },
        )
}

context (TelegramBot)
private suspend fun Message.banSenderNotFoundError() {
    message("Cannot find the sender of this message.")
        .send(
            to = chat.id,
            via = this@TelegramBot,
            duration = 10.seconds,
            onTimeout = { message ->
                this@TelegramBot.deleteMessage(message)
                this@TelegramBot.deleteMessage(this@banSenderNotFoundError)
            },
        )
}

context (TelegramBot)
private suspend fun Message.banChatMember(sender: User) {
    println("xxxxxxxxxxxxxxxxxxxx ${sender.firstName}")
    banChatMember(
        userId = sender.id,
        untilDate = null,
        revokeMessages = true,
    ).send(to = chat.id, via = this@TelegramBot)
}

context (TelegramBot)
private suspend fun Message.banForwardMessage(sender: User, chatId: Long) {
    runCatching { addBanForwardMessageChatId(chatId) }
        .fold(
            onSuccess = { "Banned." },
            onFailure = {
                it.printStackTrace()
                "Cannot add $chatId to ban list."
            },
        )
        .let(::message)
        .send(
            to = chat.id,
            via = this@TelegramBot,
            duration = 15.seconds,
            onTimeout = { message ->
                this@TelegramBot.deleteMessage(message)
                this@TelegramBot.deleteMessage(this@banForwardMessage)
            },
        )
    banChatMember(sender)
}

private suspend fun addBanForwardMessageChatId(chatId: Long) {
    modifyConfiguration {
        copy(
            banForwardMessage = banForwardMessage.copy(
                chatIds = banForwardMessage.chatIds + chatId,
            ),
        )
    }
}
