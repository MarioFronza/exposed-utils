val exposedVersion: String by project
val mockkVersion: String by project
val logbackVersion: String by project
val h2databaseVersion: String by project

plugins {
    kotlin("jvm") version "1.9.0"
    id("maven-publish")
}

group = "com.github.MarioFronza"
version = "1.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.h2database:h2:$h2databaseVersion")
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            from(components["kotlin"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

