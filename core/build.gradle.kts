plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":db"))

    api("com.michael-bull.kotlin-result:kotlin-result:1.1.16")

    implementation("io.github.microutils:kotlin-logging:2.1.23")

    testImplementation(testFixtures(project(":db")))
}
