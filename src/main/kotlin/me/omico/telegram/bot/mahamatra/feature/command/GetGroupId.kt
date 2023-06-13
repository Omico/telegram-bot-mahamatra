package me.omico.telegram.bot.mahamatra.feature.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.Message
import me.omico.telegram.bot.utility.chatOwnerOrAdministratorOnly
import me.omico.telegram.bot.utility.deleteMessage
import me.omico.telegram.bot.utility.onCommand
import me.omico.telegram.bot.utility.sendTimeLimited
import kotlin.time.Duration.Companion.seconds

private const val COMMAND_GET_GROUP_ID = "/get_group_id"

context (TelegramBot)
suspend fun Message.setupGetGroupId() =
    onCommand(COMMAND_GET_GROUP_ID) {
        deleteMessage(this)
        chatOwnerOrAdministratorOnly {
            message("${chat.id}").sendTimeLimited(
                duration = 10.seconds,
                to = chat.id,
                via = this@TelegramBot,
            )
        }
    }
