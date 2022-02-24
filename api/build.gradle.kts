plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.10"
    application
}

application {
    mainClass.set("com.spothero.rates.api.MainKt")
}

dependencies {
    implementation(project(":core"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")

    implementation("com.github.ajalt.clikt:clikt:3.4.0")

    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("io.ktor:ktor-serialization:1.6.7")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("ch.qos.logback:logback-classic:1.2.10")

    testImplementation("io.ktor:ktor-server-test-host:1.6.7")
    testImplementation(testFixtures(project(":db")))
}
