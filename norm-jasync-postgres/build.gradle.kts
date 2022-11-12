import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":norm-jasync"))

    implementation(platform("io.netty:netty-bom:4.1.79.Final"))
    implementation("com.github.jasync-sql:jasync-postgresql:2.0.8")

    testImplementation(testFixtures(project(":norm")))
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
