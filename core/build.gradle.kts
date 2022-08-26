plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":db"))

    api("com.michael-bull.kotlin-result:kotlin-result:1.1.14")

    implementation("io.github.microutils:kotlin-logging:2.1.21")

    testImplementation(testFixtures(project(":db")))
}
