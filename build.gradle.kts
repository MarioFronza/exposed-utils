val exposedVersion: String by project

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
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
