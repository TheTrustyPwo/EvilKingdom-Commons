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
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("org.mongodb:mongo-java-driver:3.12.10")
//    compileOnly("com.google.code.gson:gson:2.7")
//    compileOnly("org.json:json:20090211")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.1.0")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.1.0") {
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