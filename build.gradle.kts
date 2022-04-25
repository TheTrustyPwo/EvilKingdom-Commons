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
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    implementation("org.mongodb:mongo-java-driver:3.12.10")
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.1.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.1.1") {
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