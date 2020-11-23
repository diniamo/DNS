import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.diniamo"
version = "1.0"

val jdaVersion = "4.2.0_221"

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version ("6.1.0")
}

sourceSets {
    sourceSets.main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")

    implementation("net.dv8tion", "JDA", jdaVersion)

    implementation("com.beust", "klaxon", "5.0.1")
    implementation("com.squareup.okhttp3", "okhttp", "4.9.0")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("org.jsoup", "jsoup", "1.13.1")
    implementation("org.codehaus.groovy", "groovy-jsr223", "3.0.6")
    //implementation("org.ktorm", "ktorm-core", "3.2.0")
}
