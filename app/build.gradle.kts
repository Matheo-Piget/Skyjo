plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")

    // Add JavaFX dependencies
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.openjfx:javafx-media:21")

    // JUnit dependencies for testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media") 
}

tasks.test {
    useJUnitPlatform()
}

// Dans build.gradle.kts
tasks.register<Jar>("clientJar") {
    archiveFileName.set("skyjo-client.jar")
    manifest {
        attributes["Main-Class"] = "org.App.App"
        // Ajouter cette ligne pour le classpath
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    }
    from(sourceSets.main.get().output)
    
    // Inclure TOUT depuis les dépendances, y compris JavaFX
    from({
        configurations.runtimeClasspath.get().map { 
            if (it.isDirectory) it else zipTree(it) 
        }
    }) {
        // Exclure les signatures META-INF qui causent des problèmes
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
    
    // Inclure explicitement les resources
    from("src/main/resources") {
        into("resources")
    }
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Jar>("serverJar") {
    archiveFileName.set("skyjo-server.jar")
    manifest {
        attributes["Main-Class"] = "org.App.ServerLauncher"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register("createExecutable") {
    dependsOn("clientJar")
    doLast {
        exec {
            workingDir("${buildDir}/libs")
            commandLine(
                "jpackage",
                "--input", ".",
                "--main-jar", "skyjo-client.jar",
                "--main-class", "org.App.App",
                "--type", "app-image",
                "--name", "Skyjo",
                "--app-version", "1.0",
                "--vendor", "YourName",
                "--module-path", System.getProperty("java.home") + "/jmods",
                "--add-modules", "java.base,java.desktop,javafx.controls,javafx.fxml,javafx.media"
            )
        }
    }
}

tasks.register("buildAll") {
    dependsOn("clientJar", "serverJar")
}

application {
    mainClass.set("org.App.App")
}

// Correct Kotlin syntax for adding resources
tasks.processResources {
    from("src/main/resources") {
        include("**/*")
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.javadoc {
    source = sourceSets.main.get().allJava
    classpath = configurations.compileClasspath.get()
    destinationDir = file("${buildDir}/docs/javadoc")
    options {
        this as StandardJavadocDocletOptions
        links("https://docs.oracle.com/en/java/javase/21/docs/api/")
    }
}

sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}