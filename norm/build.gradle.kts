plugins {
    kotlin("jvm")
    `java-test-fixtures`
}

dependencies {
    api("com.michael-bull.kotlin-result:kotlin-result:1.1.14")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")

    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.14")
}
