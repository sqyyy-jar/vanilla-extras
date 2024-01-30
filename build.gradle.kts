plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

group = "com.github.sqyyy"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
        maxHeapSize = "2G"
    }
}