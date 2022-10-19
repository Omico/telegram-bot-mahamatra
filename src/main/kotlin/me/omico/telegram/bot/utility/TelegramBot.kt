package me.omico.telegram.bot.utility

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.getChatMember
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.core.ManualHandlingDsl
import eu.vendeli.tgbot.interfaces.Action
import eu.vendeli.tgbot.interfaces.sendAsync
import eu.vendeli.tgbot.types.Chat
import eu.vendeli.tgbot.types.ChatMember
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.getOrNull
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
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

suspend fun Action<Message>.send(
    to: Long,
    via: TelegramBot,
    duration: Duration,
    onTimeout: suspend (message: Message) -> Unit = {},
) {
    coroutineScope {
        launch {
            val message = sendAsync(to = to, via = via).await().getOrNull() ?: return@launch
            withContext(Dispatchers.IO) {
                delay(duration)
                onTimeout(message)
            }
        }
    }
}

suspend fun Action<Message>.send(
    to: User,
    via: TelegramBot,
    duration: Duration,
    onTimeout: suspend (message: Message) -> Unit = {},
) = send(
    to = to.id,
    via = via,
    duration = duration,
    onTimeout = onTimeout,
)

suspend fun Action<Message>.sendTimeLimited(duration: Duration, to: Long, via: TelegramBot) =
    send(
        to = to,
        via = via,
        duration = duration,
        onTimeout = via::deleteMessage,
    )

suspend fun Action<Message>.sendTimeLimited(duration: Duration, to: User, via: TelegramBot) =
    send(
        to = to,
        via = via,
        duration = duration,
        onTimeout = via::deleteMessage,
    )

suspend fun TelegramBot.deleteMessage(message: Message) =
    deleteMessage(message.messageId).send(message.chat, this@deleteMessage)

context (TelegramBot)
    suspend fun Message.ifMessageFromChatOwnerOrAdministrator(): Boolean =
    run {
        val user = from ?: return@run false
        val chatMember = getChatMember(user.id)
            .sendAsync(to = chat.id, via = this@TelegramBot)
            .await()
            .getOrNull() ?: return@run false
        chatMember is ChatMember.Owner || chatMember is ChatMember.Administrator
    }

context (TelegramBot)
    suspend fun Message.chatOwnerOrAdministratorOnly(
    onFalse: suspend Message.() -> Unit = { deleteMessage(this@Message) },
    onTrue: suspend Message.() -> Unit,
) = when {
    ifMessageFromChatOwnerOrAdministrator() -> onTrue()
    else -> onFalse()
}

context (TelegramBot)
    suspend fun Message.onCommand(
    command: String,
    handle: suspend Message.(text: String) -> Unit,
) {
    val text = text ?: return
    if (!text.startsWith(command)) return
    this@Message.handle(text)
}
