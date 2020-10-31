import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.diniamo"
version = "1.0"

val jdaVersion = "4.2.0_214"

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version ("6.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val shadowJar by tasks.getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    manifest {
        attributes["Main-Class"] = "me.diniamo.DNS"
    }
}

tasks.register("run") {
    group = "shadow"

    dependsOn("shadowJar")

    doLast {
        exec {
            println(workingDir)
            executable("java -jar /build/libs/DNS-1.0-all.jar")
            // args("")
            //commandLine("-jar", "DNS-1.0-all.jar")
        }
    }
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("net.dv8tion", "JDA", jdaVersion)
    implementation("com.jagrosh", "jda-utilities", "3.0.4")

    implementation("ch.jalu", "configme", "1.1.0")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("org.jsoup", "jsoup", "1.13.1")
    implementation("org.codehaus.groovy", "groovy-jsr223", "3.0.6")
    //implementation("com.github.ben-manes.caffeine", "caffeine", "2.8.6")
}
