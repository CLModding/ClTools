import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group = "io.github.clmodding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.4.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("net.fabricmc:mapping-io:0.3.0")
    implementation("net.fabricmc:tiny-remapper:0.8.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClassName = ("io.github.clmodding.cltools.EntrypointKt")
}