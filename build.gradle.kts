import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "io.github.splotycode"
version = "git rev-parse --short=8 HEAD".runCommand(workingDir = rootDir)
val main = "MainKt";
project.setProperty("mainClassName", main)

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20230227")
    implementation("org.xerial:sqlite-jdbc:3.40.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set(main)
}
tasks {
    build {
        dependsOn(shadowJar)
    }
}
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/SplotyCode/wann-gym")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            artifact ("build/libs/wann-gym-$version-all.jar") {
                classifier = "fat"
                extension  = "jar"
            }
        }
    }
}

fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}