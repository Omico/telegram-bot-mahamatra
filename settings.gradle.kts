import java.util.Properties

rootProject.name = "telegram-bot-mahamatra"

val localProperties = Properties().apply {
    val file = file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}

val localTelegramBotApiDir: String? = localProperties.getProperty("telegram.bot.api.dir", null)

if (localTelegramBotApiDir != null) includeBuild(localTelegramBotApiDir) {
    dependencySubstitution {
        substitute(module("eu.vendeli:telegram-bot")).using(project(":telegram-bot"))
    }
}
