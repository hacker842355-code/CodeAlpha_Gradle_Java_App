plugins {
    application
    jacoco
}

group = "tech.codealpha"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Runtime code uses only the JDK (com.sun.net.httpserver) -> zero runtime dependencies.
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)          // builds with any JDK >= 17, targets Java 17
    options.compilerArgs.add("-Xlint:all")
}

application {
    mainClass.set("tech.codealpha.tasks.App")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    testLogging { events("passed", "failed", "skipped") }
}

tasks.jacocoTestReport {
    reports { xml.required.set(true); html.required.set(true) }
}

tasks.jar {
    manifest { attributes("Main-Class" to "tech.codealpha.tasks.App", "Implementation-Version" to project.version) }
}
