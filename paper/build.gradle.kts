import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper")
    id("de.eldoria.plugin-yml.paper")
    id("io.papermc.paperweight.userdev")
    kotlin("plugin.serialization")
}

val projectVersion = version

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Crystal Shard
    implementation("net.crystopia.crystalshard:common:1.0.0")
    implementation("net.crystopia.crystalshard:paper-core:1.0.0")
    implementation("net.crystopia.crystalshard:paper-box:1.0.0")
    implementation("net.crystopia.crystalshard:paper-custom:1.0.0")
    implementation("net.crystopia.crystalshard:paper-dhl:1.0.0")
    implementation("net.crystopia.crystalshard:paper-pack:1.0.0")

    // Paper
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    // Command API
    compileOnly("dev.jorel:commandapi-paper-core:11.1.0")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:11.1.0")
    implementation("dev.jorel:commandapi-paper-kotlin:11.1.0")


}

kotlin {
    jvmToolchain(22)
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks {
    shadowJar {
        archiveBaseName.set("CrystalBenchPaper-$projectVersion")
    }
    runServer {
        minecraftVersion("1.21.11")
    }
}

paper {
    name = "CrystalBench-Paper"
    version = "$projectVersion"
    description = "ResourcePack Manager for Custom Items, Models and more!"
    main = "net.crystopia.crystalbench.CrystalBenchPluginPaper"
    authors = listOf("xyzjesper")
    apiVersion = "1.21"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
}
