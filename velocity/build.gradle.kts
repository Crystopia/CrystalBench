plugins {
    kotlin("jvm") version "2.0.20-Beta1"
    id("com.gradleup.shadow") version "9.0.0-beta8"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    kotlin("plugin.serialization") version "2.1.10"

}

group = "net.crystopia"
version = "1.0.0"


dependencies {

    // Velocity
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.google.code.gson:gson:2.8.8")

    // CommandAPI
    // implementation("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
    implementation("dev.jorel:commandapi-velocity-kotlin:9.7.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-velocity-core:9.7.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-velocity-shade:9.7.1-SNAPSHOT")
    
    // Adventure
    implementation("net.kyori:adventure-api:4.18.0")

    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveBaseName.set("CrystalBenchVelocity")
    }
    assemble {
        dependsOn(shadowJar)
    }
}
