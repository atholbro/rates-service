plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":norm"))

    implementation("com.github.jasync-sql:jasync-postgresql:2.0.6")

    testImplementation(testFixtures(project(":norm")))
}
