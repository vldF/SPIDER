import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "1.4.32"
}

group = "me.vldf"
version = "1.0-SNAPSHOT"
val githubProperties = Properties()
githubProperties.load(FileInputStream(rootProject.file("github.properties")))

repositories {
    mavenCentral()
    maven("https://jitpack.io")

    maven {
        name = "github-vorpal-research-kotlin-maven"
        url = uri("https://maven.pkg.github.com/vorpal-research/kotlin-maven")
        credentials {
            username =  githubProperties["gpr.usr"] as String
            password = githubProperties["gpr.key"] as String
        }
    }
    maven("https://kotlin.bintray.com/kotlinx")
    jcenter()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("com.github.vldF:LibSLParser:76d8226a67")
    implementation("org.jetbrains.research:kex-intrinsics:0.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.hendraanggrian:javapoet-ktx:0.8")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}