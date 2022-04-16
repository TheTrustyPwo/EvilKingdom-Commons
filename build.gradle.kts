plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("7.1.2")
    id("io.papermc.paperweight.userdev").version("1.3.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("org.mongodb:mongo-java-driver:3.12.10")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.10")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.10") {
        isTransitive = false
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }
}