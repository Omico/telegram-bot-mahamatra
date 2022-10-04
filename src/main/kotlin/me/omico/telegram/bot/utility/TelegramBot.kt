package me.omico.telegram.bot.utility

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.core.ManualHandlingDsl
import eu.vendeli.tgbot.interfaces.Action
import eu.vendeli.tgbot.types.Chat
import eu.vendeli.tgbot.types.Message
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

typealias UpdatesHandler = suspend ManualHandlingDsl.() -> Unit

suspend fun TelegramBot.autoRetry(updatesHandler: UpdatesHandler) =
    coroutineScope {
        var isFailure = true
        while (isFailure) {
            val result = runCatching {
                println("Handle Updates...")
                handleUpdates(updatesHandler)
            }
            val exception = result.exceptionOrNull() ?: return@coroutineScope
            when (exception) {
                is ConnectTimeoutException -> {
                    println("Handle Updates Failure, retry in 5 seconds...")
                    delay(5.seconds)
                }
            }
            println("retrying...")
            isFailure = result.isFailure
        }
    }

suspend fun <ReturnType> Action<ReturnType>.send(to: Chat, bot: TelegramBot) =
    send(to = to.id, via = bot)

suspend fun TelegramBot.deleteMessage(message: Message) =
    deleteMessage(message.messageId).send(message.chat, this@deleteMessage)
