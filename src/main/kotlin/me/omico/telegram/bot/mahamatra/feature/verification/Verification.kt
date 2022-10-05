package me.omico.telegram.bot.mahamatra.feature.verification

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.SendMessageAction
import eu.vendeli.tgbot.api.chat.approveChatJoinRequest
import eu.vendeli.tgbot.api.chat.declineChatJoinRequest
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.core.ManualHandlingDsl
import eu.vendeli.tgbot.types.ChatJoinRequest
import eu.vendeli.tgbot.utils.inlineKeyboardMarkup
import me.omico.telegram.bot.utility.deleteMessage

fun ManualHandlingDsl.setupVerification(bot: TelegramBot) {
    onChatJoinRequest {
        val user = data.from
        message { "你好，你需要回答以下问题来通过验证。" }.send(to = user, via = bot)
        val qa = verificationQAs.entries.random()
        verificationMessage(data, qa.key, qa.value).send(to = user, via = bot)
    }
    onCallbackQuery {
        val callbackData = data.data ?: return@onCallbackQuery
        if (!callbackData.startsWith("verification")) return@onCallbackQuery
        bot.deleteMessage(data.message ?: return@onCallbackQuery)
        val (type, chatId) = callbackData.split(":")
        when (type) {
            "verification_failed" -> {
                declineChatJoinRequest("${data.from.id}")
                    .send(to = chatId, via = bot)
                message { "验证失败，你已被拒绝加入。" }.send(to = data.from, via = bot)
            }
            "verification_succeeded" -> {
                approveChatJoinRequest("${data.from.id}")
                    .send(to = chatId, via = bot)
                message { "验证成功。" }.send(to = data.from, via = bot)
            }
            else -> Unit
        }
    }
}

internal fun verificationMessage(
    chatJoinRequest: ChatJoinRequest,
    question: String,
    choices: Set<Choice>,
): SendMessageAction = message(question).markup {
    inlineKeyboardMarkup {
        choices.shuffled().forEach { choice ->
            callbackData(choice.text) {
                when {
                    choice.correct -> "verification_succeeded:${chatJoinRequest.chat.id}"
                    else -> "verification_failed:${chatJoinRequest.chat.id}"
                }
            }
            newLine()
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
