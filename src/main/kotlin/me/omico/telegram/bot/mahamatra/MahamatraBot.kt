@file:JvmName("MahamatraBot")

package me.omico.telegram.bot.mahamatra

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.banChatSenderChat
import eu.vendeli.tgbot.interfaces.sendAsync
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import me.omico.telegram.bot.utility.autoRetry
import me.omico.telegram.bot.utility.deleteMessage

suspend fun main(arguments: Array<String>) {
    val parser = ArgParser("MahamatraBot")
    val token by parser
        .option(
            type = ArgType.String,
            shortName = "t",
            description = "Bot Token",
        )
        .required()
    parser.parse(arguments)
    val bot = TelegramBot(
        token = token,
        commandsPackage = "me.omico.telegram.bot.mahamatra",
    )
    bot.autoRetry {
        onMessage {
            val message = data
            val forwardFromChat = message.forwardFromChat ?: return@onMessage
            if (forwardFromChat.id != -1001761534525L) return@onMessage
            bot.deleteMessage(message)
            val user = message.from ?: return@onMessage
            banChatSenderChat(user.id)
                .sendAsync(to = message.chat.id, via = bot)
                .await()
        }
    }
}
