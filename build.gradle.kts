import org.jetbrains.kotlin.cli.jvm.compiler.findMainClass

plugins {
    kotlin("jvm") version "1.9.22"
    application
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.ironbird"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("ch.qos.logback:logback-core:+")
    implementation("ch.qos.logback:logback-classic:+")
    implementation("com.github.weliem.blessed-bluez:blessed:0.62")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.ironbird.Main"
    }
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "com.ironbird.Main"
}