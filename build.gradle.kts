plugins {
    kotlin("jvm") version "2.3.20"
}

group = "com.gigapi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("/home/vladimir/Documents/Java/GdxUtils/build/libs/GdxUtils-1.0-SNAPSHOT.jar"))

    implementation("com.badlogicgames.gdx:gdx-freetype:1.13.1")
    implementation("com.badlogicgames.gdx:gdx:1.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("de.javagl:obj:0.4.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}