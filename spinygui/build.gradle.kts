plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    api(project(":core"))
    api(project(":core.backend"))
    api(project(":core.backend.lwjgl.nanovg"))

    api(libs.cbchain)
}
