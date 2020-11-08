import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}
group = "dev.syndek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://ci.ender.zone/plugin/repository/everything")
    maven("https://papermc.io/repo/repository/maven-public")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.ess3:EssentialsX:2.18.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    from(
        configurations.compileClasspath.get()
            .filter { "kotlin-stdlib" in it.name }
            .map { if (it.isDirectory) it else zipTree(it) }
    )
}