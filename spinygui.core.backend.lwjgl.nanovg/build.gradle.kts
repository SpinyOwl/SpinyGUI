plugins {
    id("buildlogic.java-library-conventions")
    id("jacoco")
}

dependencies {
    api(project(":spinygui.core"))
    api(project(":spinygui.core.backend"))

    api(libs.lwjgl)
    api(variantOf(libs.lwjgl) { classifier("natives-windows") })
    api(variantOf(libs.lwjgl) { classifier("natives-linux") })
    api(variantOf(libs.lwjgl) { classifier("natives-macos") })

    api(libs.lwjglGlfw)
    api(variantOf(libs.lwjglGlfw) { classifier("natives-windows") })
    api(variantOf(libs.lwjglGlfw) { classifier("natives-linux") })
    api(variantOf(libs.lwjglGlfw) { classifier("natives-macos") })

    api(libs.lwjglOpengl)
    api(variantOf(libs.lwjglOpengl) { classifier("natives-windows") })
    api(variantOf(libs.lwjglOpengl) { classifier("natives-linux") })
    api(variantOf(libs.lwjglOpengl) { classifier("natives-macos") })

    api(libs.lwjglNanovg)
    api(variantOf(libs.lwjglNanovg) { classifier("natives-windows") })
    api(variantOf(libs.lwjglNanovg) { classifier("natives-linux") })
    api(variantOf(libs.lwjglNanovg) { classifier("natives-macos") })

    api(libs.lwjglStb)
    api(variantOf(libs.lwjglStb) { classifier("natives-windows") })
    api(variantOf(libs.lwjglStb) { classifier("natives-linux") })
    api(variantOf(libs.lwjglStb) { classifier("natives-macos") })
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
