import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "eu.duckrealm"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.28")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.milkbowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    test {
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
    withType<Jar> {
        archiveFileName.set(String.format("%s.jar", project.name))
    }
}

bukkit {
    name = project.name
    description = "A Minecraft plugin written in Java for Claims protected by the Quack Army"
    version = project.version.toString()
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    softDepend = listOf("Vault", "DuckCore")
    author = "MineKID_LP"
    main = "eu.duckrealm.quackclaim.QuackClaim"
    apiVersion = "1.19"

    permissions {
        register("quackclaim.claim") {
            description = "Allows you to claim"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("quackclaim.team") {
            description = "Allows you to use the team command"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("quackclaim.qadmin") {
            description = "Allows you to use the admin command"
            default = BukkitPluginDescription.Permission.Default.OP
        }

        register("quackclaim.map") {
            description = "Allows you to use the admin command"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }

    commands {
        register("claim") {
            description = "Claim an area"
            usage = "/claim"
            permission = "quackclaim.claim"
        }

        register("unclaim") {
            description = "unclaim an area"
            usage = "/unclaim"
            permission = "quackclaim.claim"
        }

        register("qteam") {
            description = "Team command"
            usage = "/qteam [action]"
            permission = "quackclaim.team"
            aliases = listOf("qt")
        }

        register("map") {
            description = "Team command"
            usage = "/qteam [action]"
            permission = "quackclaim.map"
        }

        register("qadmin") {
            description = "Admin command"
            usage = "/qadmin [action]"
            permission = "quackclaim.qadmin"
        }
    }
}