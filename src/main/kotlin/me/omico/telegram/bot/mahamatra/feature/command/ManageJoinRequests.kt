package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.mahamatra.modifyConfiguration
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.deleteMessage
import me.omico.telegram.bot.utility.onCommand
import me.omico.telegram.bot.utility.sendTimeLimited
import kotlin.time.Duration.Companion.seconds

private const val COMMAND_MANAGE_JOIN_REQUESTS = "/manage_join_requests"

context (TelegramBot)
suspend fun Message.setupManageJoinRequests(): Unit =
    onCommand(COMMAND_MANAGE_JOIN_REQUESTS) {
        deleteMessage(this)
        chatOwnerOrAdministratorOnly {
            modifyConfiguration {
                copy(
                    verification = verification.copy(
                        allowGroups = verification.allowGroups + chat.id,
                    ),
                )
            }
            message("done.").sendTimeLimited(
                duration = 3.seconds,
                to = chat.id,
                via = this@TelegramBot,
            )
        }
    }
