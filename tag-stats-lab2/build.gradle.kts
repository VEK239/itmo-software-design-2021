import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}
group = "me.lizzka239"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("com.google.code.gson:gson:2.8.8")

    testImplementation("junit:junit:4.13")
    testImplementation("org.mockito:mockito-all:2.0.2-beta")
    testImplementation("com.xebialabs.restito:restito:0.8.2")
    testImplementation("org.glassfish.grizzly:grizzly-http-server:2.4.4")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}