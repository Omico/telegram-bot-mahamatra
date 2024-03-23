import java.util.Properties

plugins {
    application
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "me.omico.telegram.bot.mahamatra"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
    sourceSets {
        all {
            languageSettings.enableLanguageFeature("ContextReceivers")
        }
    }
}

application {
    applicationName = "MahamatraBot"
    mainClass.set("me.omico.telegram.bot.mahamatra.MahamatraBot")
}

dependencies {
    val ktorVersion = "2.3.9"
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("eu.vendeli:telegram-bot:5.0.4") {
        exclude(group = "io.ktor")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
}

val localProperties by lazy {
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (!localPropertiesFile.exists()) localPropertiesFile.createNewFile()
        localPropertiesFile.inputStream().use(::load)
    }
}

tasks.run<JavaExec> {
    args = listOf("--token", localProperties["BOT_TOKEN"] as String)
}
