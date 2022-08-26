plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":norm"))

    implementation(platform("io.netty:netty-bom:4.1.79.Final"))
    implementation("com.github.jasync-sql:jasync-postgresql:2.0.8")

    testImplementation(testFixtures(project(":norm")))
}
