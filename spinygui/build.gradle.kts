plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    api(project(":spinygui.core"))
    api(project(":spinygui.core.backend"))
    api(project(":spinygui.core.backend.lwjgl.nanovg"))

    api(libs.cbchain)
}
