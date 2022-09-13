import java.util.Properties

plugins {
    application
    kotlin("jvm") version "1.7.10"
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
    compileOnly("io.ktor:ktor-client-core:2.1.1")
    implementation("eu.vendeli:telegram-bot:2.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
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
