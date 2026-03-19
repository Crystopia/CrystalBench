plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    kotlin("plugin.serialization")

}

group = "net.crystopia"
version = "0.1.0"

val projectVersion = version


dependencies {

    // Velocity
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.google.code.gson:gson:2.8.9")

    // CommandAPI
    implementation("dev.jorel:commandapi-velocity-shade:11.1.0")

    implementation("net.crystopia.crystalshard:common:1.0.0")
    implementation("net.crystopia.crystalshard:velocity:1.0.0")
}

kotlin {
    jvmToolchain(22)
}

tasks {
    shadowJar {
        archiveBaseName.set("CrystalBenchVelocity-$projectVersion")
    }
    assemble {
        dependsOn(shadowJar)
    }
}
