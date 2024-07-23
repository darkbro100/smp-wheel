plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer and runMojangMappedServer tasks for testing

  // Shades and relocates dependencies into our plugin jar. See https://imperceptiblethoughts.com/shadow/introduction/
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.paul.foliastuff"
version = "1.0.0-SNAPSHOT"
description = "Test plugin for paperweight-userdev"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
  mavenCentral()
  mavenLocal()

  maven {
    url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
  }

  maven {
    url = uri("https://oss.sonatype.org/content/groups/public/")
  }

  maven {
    url = uri("https://jcenter.bintray.com/")
  }
}

dependencies {
   paperweight.foliaDevBundle("1.20.4-R0.1-SNAPSHOT")

  implementation("org.reflections:reflections:0.10.2")
  implementation("com.github.johnnyjayjay:spigot-maps:2.1.2")
  compileOnly ("org.projectlombok:lombok:1.18.28")
  annotationProcessor ("org.projectlombok:lombok:1.18.28")
  testCompileOnly ("org.projectlombok:lombok:1.18.28")
  testAnnotationProcessor ("org.projectlombok:lombok:1.18.28")

  compileOnly("me.clip:placeholderapi:2.11.6")
  compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.1.0")
}

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    val props = mapOf(
      "name" to project.name,
      "version" to project.version,
      "description" to project.description,
      "apiVersion" to "\"1.20\""
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
      expand(props)
    }
    filesMatching("paper-plugin.yml") {
      expand(props)
    }
  }

  shadowJar {
//    from("src/main/java") // idk what this did but it temporarily solved my problem but i think for the wrong reasons

    // helper function to relocate a package into our package
    fun reloc(pkg: String) = relocate(pkg, "me.paul.foliastuff.dependency.$pkg")

    // relocate cloud and it's transitive dependencies
    reloc("com.github.johnnyjayjay")
    reloc("com.madgag.gif")
    reloc("org.reflections")
    reloc("org.slf4j")
  }

  /*
  reobfJar {
    // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
    // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
    outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
  }
   */
}
