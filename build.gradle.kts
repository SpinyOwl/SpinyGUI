plugins {
    id("buildlogic.java-application-conventions")
}

group = "com.spinyowl"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

