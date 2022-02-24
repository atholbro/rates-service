plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":db"))

    api("com.michael-bull.kotlin-result:kotlin-result:1.1.14")

    testImplementation(testFixtures(project(":db")))
}
