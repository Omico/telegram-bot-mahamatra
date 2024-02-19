package me.omico.telegram.bot.mahamatra.feature.verification

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.SendMessageAction
import eu.vendeli.tgbot.api.chat.approveChatJoinRequest
import eu.vendeli.tgbot.api.chat.declineChatJoinRequest
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.core.FunctionalHandlingDsl
import eu.vendeli.tgbot.types.internal.ActivityCtx
import eu.vendeli.tgbot.types.internal.ChatJoinRequestUpdate
import eu.vendeli.tgbot.utils.builders.inlineKeyboardMarkup
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.omico.telegram.bot.mahamatra.cachedConfiguration
import me.omico.telegram.bot.utility.deleteMessage
import me.omico.telegram.bot.utility.send
import me.omico.telegram.bot.utility.sendTimeLimited
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

context (TelegramBot)
fun FunctionalHandlingDsl.setupVerification() {
    onChatJoinRequest {
        val data = update.chatJoinRequest
        if (data.chat.id !in cachedConfiguration.verification.allowGroups) return@onChatJoinRequest
        coroutineScope {
            launch {
                message { "你好，你需要回答以下问题来通过验证。" }
                    .sendTimeLimited(duration = 5.minutes, to = data.from, via = this@TelegramBot)
            }
            launch {
                message { "你有5分钟回答时间。" }
                    .send(to = data.from, via = this@TelegramBot, duration = 5.minutes) { message ->
                        deleteMessage(message)
                        declineChatJoinRequest(data.from).send(to = data.chat.id, via = this@TelegramBot)
                    }
            }
            launch {
                randomVerificationMessage()
                    .sendTimeLimited(duration = 5.minutes, to = data.from, via = this@TelegramBot)
            }
        }
    }
    onCallbackQuery {
        coroutineScope {
            val data = update.callbackQuery
            val callbackData = data.data ?: return@coroutineScope
            if (!callbackData.startsWith("verification")) return@coroutineScope
            val message = data.message ?: return@coroutineScope
            deleteMessage(message.messageId).send(message.chat, this@TelegramBot)
            val (type, chatId) = callbackData.split(":")
            when (type) {
                "verification_failed" -> {
                    launch { declineChatJoinRequest(data.from).send(to = chatId, via = this@TelegramBot) }
                    launch {
                        message { "回答错误，你已被拒绝加入。" }
                            .sendTimeLimited(duration = 5.seconds, to = data.from, via = this@TelegramBot)
                    }
                }
                "verification_succeeded" -> {
                    launch { approveChatJoinRequest(data.from).send(to = chatId, via = this@TelegramBot) }
                    launch {
                        message { "回答正确，欢迎加入！" }
                            .sendTimeLimited(duration = 5.seconds, to = data.from, via = this@TelegramBot)
                    }
                }
            }
        }
    }
}

internal fun ActivityCtx<ChatJoinRequestUpdate>.randomVerificationMessage(): SendMessageAction = run {
    val qa = verificationQAs.entries.random()
    val chatId = update.chatJoinRequest.chat.id
    message(qa.key).markup {
        inlineKeyboardMarkup {
            qa.value.shuffled().forEach { choice ->
                callbackData(choice.text) {
                    when {
                        choice.correct -> "verification_succeeded:${chatId}"
                        else -> "verification_failed:${chatId}"
                    }
                }
                newLine()
            }
        }
    }
}

internal data class Choice(
    val text: String,
    val correct: Boolean = false,
)

private val verificationQAs = mapOf(
    "在 Android Studio Chipmunk 上升级 compileSdk 至 33 会发生什么事？" to setOf(
        Choice("Android XML 文件出现高亮警告", correct = true),
        Choice("一切正常"),
        Choice("编译失败"),
        Choice("编译成功，但无法运行"),
    ),
    "下列哪个是 Android 的 web 组件？" to setOf(
        Choice("WebView", correct = true),
        Choice("UIWebView"),
        Choice("WKWebView"),
        Choice("AndroidWebView"),
    ),
    "当新版本的 Android API 释出时，以下哪个可以直接更新？" to setOf(
        Choice("compileSdk", correct = true),
        Choice("minSdk"),
        Choice("targetSdk"),
        Choice("以上全部"),
    ),
)
