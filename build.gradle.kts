import java.util.Properties

plugins {
    application
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "me.omico.telegram.bot.mahamatra"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf(
                    "-Xcontext-receivers",
                )
            }
        }
    }
}

application {
    applicationName = "MahamatraBot"
    mainClass.set("me.omico.telegram.bot.mahamatra.MahamatraBot")
}

dependencies {
    val ktorVersion = "2.1.2"
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("eu.vendeli:telegram-bot:2.2.2") {
        exclude(group = "io.ktor")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
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
