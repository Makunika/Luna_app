import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.4.21"
}

group = "ru.pshiblo.luna"
version = "1.1.9"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io/")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.0")
                implementation("org.slf4j:slf4j-api:2.0.7")
                implementation("com.google.inject:guice:5.1.0")
                implementation("com.google.inject.extensions:guice-assistedinject:5.1.0")
                implementation("org.kohsuke:github-api:1.314")
                implementation("com.github.philippheuer.events4j:events4j-api:0.12.0")
                implementation("com.github.philippheuer.events4j:events4j-core:0.12.0")
                implementation("com.github.philippheuer.events4j:events4j-kotlin:0.12.0")
                implementation("com.github.philippheuer.events4j:events4j-handler-simple:0.12.0")
                implementation("com.github.twitch4j:twitch4j:1.15.0")
                implementation("com.google.api-client:google-api-client:2.2.0")
                implementation("com.google.apis:google-api-services-youtube:v3-rev20230123-2.0.0")
                implementation("com.google.apis:google-api-services-oauth2:v2-rev20200213-2.0.0")
                implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
                implementation("com.sedmelluq:lavaplayer:1.3.78")
                implementation("net.dv8tion:JDA:5.0.0-beta.9")
                implementation("com.github.minndevelopment:jda-ktx:0.10.0-beta.1")
                implementation("org.slf4j:slf4j-api:2.0.7")
                implementation("org.slf4j:slf4j-log4j12:2.0.7")
                implementation("com.github.kwhat:jnativehook:2.2.2")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "ru.pshiblo.luna.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Luna"
            packageVersion = "1.1.9"
            macOS {
                iconFile.set(project.file("icon.icns"))
            }
            windows {
                iconFile.set(project.file("icon.ico"))
            }
            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
    }
}
