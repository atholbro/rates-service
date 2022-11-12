import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":norm"))

    implementation("org.jdbi:jdbi3-core:3.30.0")
    implementation("org.jdbi:jdbi3-kotlin:3.30.0")
    implementation("org.jdbi:jdbi3-postgres:3.30.0")

    implementation("org.postgresql:postgresql:42.4.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    testImplementation(testFixtures(project(":norm")))

    testFixturesImplementation("org.jdbi:jdbi3-core:3.30.0")
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
