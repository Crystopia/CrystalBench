plugins {
    kotlin("jvm")
    id("java-library")
    id("com.gradleup.shadow")
    id("io.papermc.paperweight.userdev") apply false
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Crystal Shard
    implementation("net.crystopia.crystalshard:common:1.0.0")

    // Adventure
    implementation("net.kyori:adventure-api:4.26.1")

    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
}

kotlin {
    jvmToolchain(22)
}
