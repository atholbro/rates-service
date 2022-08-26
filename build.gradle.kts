import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    java
    `java-test-fixtures`

    id("org.jetbrains.kotlin.jvm") version "1.6.0"

    id("org.jlleitschuh.gradle.ktlint").version("10.2.0")
}

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

allprojects {
    group = "com.spothero"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "java-test-fixtures")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")

        testImplementation("io.kotest:kotest-assertions-core:5.4.2")
        testFixturesImplementation("io.kotest:kotest-assertions-core:5.4.2")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

        testImplementation("ch.qos.logback:logback-classic:1.2.11")
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }

        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.43.0")
        verbose.set(true)
    }
}
