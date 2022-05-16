plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("7.1.2")
    id("net.minecrell.plugin-yml.bukkit").version("0.5.1")
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
    implementation("org.mongodb:mongo-java-driver:3.12.11")
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.1.2")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.1.2") {
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

bukkit {
    name = "Commons"
    author = "kodirati"
    website = "kodirati.com"
    description = "Used as the library for the servers of Evil Kingdom."
    main = "net.evilkingdom.commons.Commons"
    version = "master-" + gitCommit()
    apiVersion = "1.18"
    depend = listOf("FastAsyncWorldEdit", "LuckPerms")
}

fun gitCommit(): String {
    return try {
        val byteOut = org.apache.commons.io.output.ByteArrayOutputStream()
        project.exec {
            commandLine = "git rev-parse --short HEAD".split(" ")
            standardOutput = byteOut
        }
        String(byteOut.toByteArray()).trim().also {
            if (it == "HEAD")
                logger.warn("Unable to determine current commit: Project is checked out with detached head!")
        }
    } catch (e: Exception) {
        logger.warn("Unable to determine current commit: ${e.message}")
        "Unknown Commit"
    }
}