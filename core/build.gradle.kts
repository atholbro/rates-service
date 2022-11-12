import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":db"))

    api("com.michael-bull.kotlin-result:kotlin-result:1.1.16")

    implementation("io.github.microutils:kotlin-logging:2.1.23")

    testImplementation(testFixtures(project(":db")))
	implementation(kotlin("stdlib-jdk8"))
}
repositories {
	mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}
