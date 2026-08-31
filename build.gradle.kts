plugins {
    id("buildlogic.java-application-conventions")
}

group = "com.spinyowl"

subprojects {
    group = rootProject.group
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

val staticAnalysis by tasks.registering {
    group = "verification"
    description = "Runs static analysis for all Java source sets."
    dependsOn(
        subprojects.flatMap { project ->
            listOf(
                "${project.path}:pmdMain",
                "${project.path}:pmdTest",
                "${project.path}:spotbugsMain",
                "${project.path}:spotbugsTest"
            )
        }
    )
}

tasks.named("check") {
    dependsOn(staticAnalysis)
}

