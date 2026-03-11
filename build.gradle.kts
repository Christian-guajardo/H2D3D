plugins {
    id("java")
    id("org.sonarqube") version "5.1.0.4882"
    id("jacoco") // pour la couverture de code
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "2D3D")
        property("sonar.projectName", "2D3D")
        property("sonar.host.url", "http://localhost:9000")
        // la variable d'env SONAR_TOKEN
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "main.java.com.ubo.tp.message.MessageAppLauncher"
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}