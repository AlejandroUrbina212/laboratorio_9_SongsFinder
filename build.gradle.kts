import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.2.70"
}

group = "com.laboratorio9"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.github.kittinunf.fuel:fuel-gson:1.15.0")
    compile ("com.google.code.gson:gson:2.8.5")
    compile("org.postgresql:postgresql:42.2.5")
    compile("org.jetbrains.exposed:exposed:0.10.5")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}