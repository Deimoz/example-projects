group = "org.csc.kotlin2021"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.5.31" apply true
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0" apply true
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.8")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
}

ktlint {
    enableExperimentalRules.set(true)
    filter {
        exclude("**/resources/**")
    }
}

tasks.test {
    useJUnitPlatform {
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "11"
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}
