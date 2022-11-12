import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

dependencies {
    api("com.michael-bull.kotlin-result:kotlin-result:1.1.16")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.16")
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
