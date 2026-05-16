plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":spinygui"))
}

application {
    mainClass.set("com.spinyowl.spinygui.demo.simple.Main")
}
