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

