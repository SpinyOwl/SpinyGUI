plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    api(project(":core"))
    api(project(":core.backend"))
    api(project(":core.backend.lwjgl.nanovg"))

    api(libs.cbchain)

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
}

application {
    mainClass.set("com.spinyowl.spinygui.demo.complex.NvgExample")
    applicationDefaultJvmArgs = listOf(
        "--add-opens", "java.base/sun.misc=ALL-UNNAMED",
        "--add-opens", "com.spinyowl.cbchain/com.spinyowl.cbchain=org.lwjgl",
        "--add-reads", "org.lwjgl=com.spinyowl.cbchain", "--enable-native-access=ALL-UNNAMED"
    )
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "--add-opens", "java.base/sun.misc=ALL-UNNAMED",
        "--add-opens", "com.spinyowl.cbchain/com.spinyowl.cbchain=org.lwjgl",
        "--add-reads", "org.lwjgl=com.spinyowl.cbchain"
    )
}
