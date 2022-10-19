package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.mahamatra.loadConfiguration
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.deleteMessage
import me.omico.telegram.bot.utility.onCommand
import me.omico.telegram.bot.utility.sendTimeLimited
import kotlin.time.Duration.Companion.seconds

context (TelegramBot)
    suspend fun Message.setupReload() =
    onCommand("/reload") {
        chatOwnerOrAdministratorOnly {
            deleteMessage(this)
            runCatching { loadConfiguration() }
                .fold(
                    onSuccess = { "Reloaded." },
                    onFailure = { "Reload failed: ${it.message ?: "Unknown error"}" },
                )
                .let(::message)
                .sendTimeLimited(
                    duration = 3.seconds,
                    to = chat.id,
                    via = this@TelegramBot,
                )
        }
    }
