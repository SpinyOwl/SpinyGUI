val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://raw.githubusercontent.com/SpinyOwl/repo/releases") }
}

dependencies {
    implementation(libs.findLibrary("slf4j").get())
    implementation(libs.findLibrary("logback").get())

    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("junit").get())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testCompileOnly(libs.findLibrary("lombok").get())
    testAnnotationProcessor(libs.findLibrary("lombok").get())
}

java {
    modularity.inferModulePath = true
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }

    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.release.set(25)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
