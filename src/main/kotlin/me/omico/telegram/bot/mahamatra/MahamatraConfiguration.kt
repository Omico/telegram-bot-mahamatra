package me.omico.telegram.bot.mahamatra

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class MahamatraConfiguration(
    val banForwardMessage: BanForwardMessage = BanForwardMessage(),
) {
    @Serializable
    data class BanForwardMessage(
        val chatIds: Set<Long> = emptySet(),
    )
}

private val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

private val mutex = Mutex()
private val path = Path("mahamatra.json")

var cachedConfiguration: MahamatraConfiguration = MahamatraConfiguration()

suspend fun loadConfiguration(): MahamatraConfiguration =
    mutex.withLock {
        if (!path.exists()) return@withLock MahamatraConfiguration()
        json.decodeFromString(path.readText())
    }

suspend fun MahamatraConfiguration.save() =
    mutex.withLock { path.writeText(json.encodeToString(this)) }

suspend fun modifyConfiguration(block: suspend MahamatraConfiguration.() -> MahamatraConfiguration) {
    coroutineScope {
        launch(Dispatchers.IO) {
            loadConfiguration().block().save()
        }
    }
}
