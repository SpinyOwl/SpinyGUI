plugins {
    id("buildlogic.java-library-conventions")
    id("jacoco")
}

val lwjglNatives = when {
    providers.systemProperty("os.name").get().startsWith("Windows", ignoreCase = true) -> "natives-windows"
    providers.systemProperty("os.name").get().startsWith("Mac", ignoreCase = true) -> "natives-macos"
    providers.systemProperty("os.name").get().startsWith("Linux", ignoreCase = true) -> "natives-linux"
    else -> error("Unsupported operating system: ${providers.systemProperty("os.name").get()}")
}

dependencies {
    api(project(":spinygui.core"))
    api(project(":spinygui.core.backend"))

    api(libs.lwjgl)
    api(variantOf(libs.lwjgl) { classifier(lwjglNatives) })

    api(libs.lwjglGlfw)
    api(variantOf(libs.lwjglGlfw) { classifier(lwjglNatives) })

    api(libs.lwjglOpengl)
    api(variantOf(libs.lwjglOpengl) { classifier(lwjglNatives) })

    api(libs.lwjglNanovg)
    api(variantOf(libs.lwjglNanovg) { classifier(lwjglNatives) })
}

tasks.test {
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}
