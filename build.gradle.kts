plugins {
    kotlin("jvm") version "2.+" apply false
    id("com.gradleup.shadow") version "9.0.0-beta8" apply false
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("de.eldoria.plugin-yml.paper") version "0.7.0" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18" apply false
    kotlin("plugin.serialization") version "2.+" apply false
    kotlin("kapt") version "2.3.20" apply false
}


group = "net.crystopia.crystalbench"
version = "0.1.0"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        // TODO
        // maven("https://repo.xyzify.ing/releases")
    }
}
