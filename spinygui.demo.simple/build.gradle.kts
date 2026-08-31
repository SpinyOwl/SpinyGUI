plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":spinygui"))
}

application {
    mainClass.set("com.spinyowl.spinygui.demo.simple.Main")
    applicationDefaultJvmArgs = listOf(
        "--add-opens", "java.base/sun.misc=ALL-UNNAMED",
        "--add-opens", "com.spinyowl.cbchain/com.spinyowl.cbchain=org.lwjgl",
        "--add-reads", "org.lwjgl=com.spinyowl.cbchain"
    )
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "--add-opens", "java.base/sun.misc=ALL-UNNAMED",
        "--add-opens", "com.spinyowl.cbchain/com.spinyowl.cbchain=org.lwjgl",
        "--add-reads", "org.lwjgl=com.spinyowl.cbchain"
    )
}

fun registerExampleTask(name: String, exampleMainClass: String) {
    tasks.register<JavaExec>(name) {
        group = "application"
        description = "Runs $exampleMainClass."
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set(exampleMainClass)
        jvmArgs = application.applicationDefaultJvmArgs.toList()
    }
}

registerExampleTask(
    "runHelloWorldExample",
    "com.spinyowl.spinygui.demo.simple.HelloWorldExample"
)
registerExampleTask(
    "runCounterExample",
    "com.spinyowl.spinygui.demo.simple.CounterExample"
)
