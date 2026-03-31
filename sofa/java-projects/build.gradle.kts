plugins {
    java
    application
}

group = "sofa"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set(providers.gradleProperty("mainClass").getOrElse("explorer.MathExplorer"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "explorer.MathExplorer"
    }
}
