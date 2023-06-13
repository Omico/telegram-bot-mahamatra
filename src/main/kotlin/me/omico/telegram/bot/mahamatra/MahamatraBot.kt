@file:JvmName("MahamatraBot")

package me.omico.telegram.bot.mahamatra

import eu.vendeli.tgbot.TelegramBot
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import me.omico.telegram.bot.mahamatra.feature.ban.setupBanForwardMessage
import me.omico.telegram.bot.mahamatra.feature.ban.setupBanMessage
import me.omico.telegram.bot.mahamatra.feature.command.setupAddBanForwardMessageChatId
import me.omico.telegram.bot.mahamatra.feature.command.setupBanThis
import me.omico.telegram.bot.mahamatra.feature.command.setupGetGroupId
import me.omico.telegram.bot.mahamatra.feature.command.setupManageJoinRequests
import me.omico.telegram.bot.mahamatra.feature.command.setupReload
import me.omico.telegram.bot.mahamatra.feature.command.setupShowForwardFromChatId
import me.omico.telegram.bot.mahamatra.feature.verification.setupVerification
import me.omico.telegram.bot.utility.autoRetry

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
    loadConfiguration()
    bot.autoRetry {
        with(bot) {
            setupVerification()
            onMessage {
                with(data) {
                    setupReload()
                    setupBanThis()
                    setupBanForwardMessage()
                    setupBanMessage()
                    setupAddBanForwardMessageChatId()
                    setupShowForwardFromChatId()
                    setupGetGroupId()
                    setupManageJoinRequests()
                }
            }
        }
    }
}
