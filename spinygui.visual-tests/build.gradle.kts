import java.io.File
import java.io.Serializable
import org.gradle.api.specs.Spec

class MatchingNative(private val classifier: String) : Spec<File>, Serializable {
    override fun isSatisfiedBy(file: File): Boolean =
        !file.name.contains("-natives-") || file.name.endsWith("-$classifier.jar")
}

plugins {
    id("buildlogic.java-application-conventions")
}

val nativeOs = when {
    System.getProperty("os.name").startsWith("Windows") -> "windows"
    System.getProperty("os.name").startsWith("Mac") -> "macos"
    System.getProperty("os.name").startsWith("Linux") -> "linux"
    else -> error("Visual tests require Windows, macOS or Linux")
}
val nativeArch = when (System.getProperty("os.arch").lowercase()) {
    "aarch64", "arm64" -> "-arm64"
    "amd64", "x86_64" -> ""
    else -> error("Visual tests require an x64 or ARM64 JVM")
}
val nativeClassifier = "natives-$nativeOs$nativeArch"

dependencies {
    implementation(project(":spinygui.core"))
    implementation(project(":spinygui.core.backend.lwjgl.nanovg"))
    implementation(libs.gson)
    implementation(libs.jsoup)
    implementation("com.microsoft.playwright:playwright:1.58.0")
    for (library in listOf(libs.lwjgl, libs.lwjglGlfw, libs.lwjglOpengl,
            libs.lwjglNanovg, libs.lwjglStb, libs.lwjglYoga)) {
        runtimeOnly(variantOf(library) { classifier(nativeClassifier) })
    }
}

// Older library subprojects supply x64 classifiers; only load the matching native variant here.
val visualRuntime = sourceSets.main.get().runtimeClasspath.filter(MatchingNative(nativeClassifier))
val browserCache = providers.environmentVariable("PLAYWRIGHT_BROWSERS_PATH")
    .getOrElse(layout.buildDirectory.dir("playwright-browsers").get().asFile.absolutePath)

application {
    mainClass.set("com.spinyowl.spinygui.visual.CompareViewsMain")
}

// Explicit graphics tasks: never dependencies of test/check or benchmark runs.
tasks.register<JavaExec>("installChromium") {
    group = "verification"
    description = "Installs the pinned Playwright Chromium browser (one-time setup)."
    classpath = visualRuntime
    mainClass.set("com.microsoft.playwright.CLI")
    environment("PLAYWRIGHT_BROWSERS_PATH", browserCache)
    args("install", "chromium")
}

tasks.register<JavaExec>("compareViews") {
    group = "verification"
    description = "Compares shared views in Chromium and native SpinyGUI; writes an HTML report."
    classpath = visualRuntime
    mainClass.set(application.mainClass)
    args(rootProject.projectDir.absolutePath,
        layout.buildDirectory.dir("reports/view-comparison").get().asFile.absolutePath,
        providers.gradleProperty("visualCases").getOrElse("all"))
    environment("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")
    environment("PLAYWRIGHT_BROWSERS_PATH", browserCache)
    outputs.upToDateWhen { false }
}

tasks.register<JavaExec>("launchDemos") {
    group = "application"
    description = "Opens native and Chromium demos side by side; select with -Pdemo=overflow-demo."
    classpath = visualRuntime
    mainClass.set("com.spinyowl.spinygui.visual.LaunchDemosMain")
    args(rootProject.projectDir.absolutePath,
        layout.buildDirectory.dir("demo-sessions").get().asFile.absolutePath,
        providers.gradleProperty("demo").getOrElse("button-demo"),
        providers.gradleProperty("demoSeconds").getOrElse("0"))
    environment("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")
    environment("PLAYWRIGHT_BROWSERS_PATH", browserCache)
}
