val exposedVersion: String by project
val mockkVersion: String by project

plugins {
    kotlin("jvm") version "1.9.0"
}

group = "com.mariofronza"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("ch.qos.logback:logback-classic:1.2.9")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
