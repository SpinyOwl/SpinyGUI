import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("com.github.spotbugs")
    java
    `java-library`
    pmd
}

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
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

pmd {
    toolVersion = libs.findVersion("pmd").get().requiredVersion
    isConsoleOutput = true
    ruleSets = emptyList()
    ruleSetFiles = files(rootProject.file("config/pmd/ruleset.xml"))
    threads = 1
}

spotbugs {
    toolVersion = libs.findVersion("spotbugs").get().requiredVersion
    effort = Effort.MAX
    reportLevel = Confidence.HIGH
    excludeFilter = rootProject.file("config/spotbugs/exclude.xml")
    maxHeapSize = "1g"
}

tasks.withType<JavaCompile> {
    options.release.set(25)
    options.compilerArgs.addAll(
        listOf(
            "-Xlint:fallthrough",
            "-Xlint:finally",
            "-Xlint:overrides",
            "-Xlint:static",
            "-Xlint:varargs"
        )
    )
}

tasks.withType<Pmd>().configureEach {
    exclude("**/parser/impl/css/antlr/**")

    reports {
        xml.required.set(true)
        html.required.set(true)
        sarif.required.set(true)
    }
}

tasks.withType<SpotBugsTask>().configureEach {
    val taskName = name
    onlyIf("there are class files other than module-info.class to analyze") {
        classes?.asFileTree?.matching {
            include("**/*.class")
            exclude("**/module-info.class")
        }?.files?.isNotEmpty() == true
    }

    reports.create("html") {
        required.set(true)
        outputLocation.set(layout.buildDirectory.file("reports/spotbugs/$taskName.html"))
        setStylesheet("fancy-hist.xsl")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
