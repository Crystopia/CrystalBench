plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("com.gradleup.shadow")
    kotlin("plugin.serialization")
}

group = "net.crystopia.crystalbench"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Crystal Shard
    implementation("net.crystopia.crystalshard:common:1.0.0")
    implementation("net.crystopia.crystalshard.paper:core:1.0.0")
    implementation("net.crystopia.crystalshard.paper:box:1.0.0")
    implementation("net.crystopia.crystalshard.paper:custom:1.0.0")
    implementation("net.crystopia.crystalshard.paper:dhl:1.0.0")
    implementation("net.crystopia.crystalshard.paper:pack:1.0.0")

    // Paper
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    // CrystalBench
    implementation(project(":common"))
}

kotlin {
    jvmToolchain(22)
}
