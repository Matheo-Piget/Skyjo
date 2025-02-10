plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)

    // Add JavaFX dependencies
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.openjfx:javafx-media:21") // Ajoutez cette ligne

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
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media") // Ajoutez javafx.media ici
}

tasks.test {
    useJUnitPlatform()
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