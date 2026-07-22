plugins {
    id("buildlogic.java-library-conventions")
    id("jacoco")
    id("antlr")
}

val lwjglNatives = when {
    providers.systemProperty("os.name").get().startsWith("Windows", ignoreCase = true) -> "natives-windows"
    providers.systemProperty("os.name").get().startsWith("Mac", ignoreCase = true) -> "natives-macos"
    providers.systemProperty("os.name").get().startsWith("Linux", ignoreCase = true) -> "natives-linux"
    else -> error("Unsupported operating system: ${providers.systemProperty("os.name").get()}")
}

dependencies {
    antlr(libs.antlr)

    api(libs.guava)
    api(libs.joml)

    implementation(libs.commonsIo)
    implementation(libs.jsoup)
    implementation(libs.commonsLang3)

    api(libs.antlrRuntime)
    api(libs.classgraph)

    api(libs.lwjgl)
    api(variantOf(libs.lwjgl) { classifier(lwjglNatives) })

    api(libs.lwjglStb)
    api(variantOf(libs.lwjglStb) { classifier(lwjglNatives) })

    api(libs.lwjglYoga)
    api(variantOf(libs.lwjglYoga) { classifier(lwjglNatives) })

    implementation(libs.gson)
    implementation(libs.snakeYaml)

    testImplementation(libs.junit)
    testImplementation(libs.junitParams)
    testImplementation(libs.mockito)
    testImplementation(libs.mockitoJunitJupiter)
}

tasks.generateGrammarSource {
    arguments = listOf(
        "-listener",
        "-visitor",
        "-long-messages",
        "-package", "com.spinyowl.spinygui.core.parser.impl.css.antlr"
    )
    outputDirectory = file("src/main/java")
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(tasks.generateGrammarSource)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.compileJava {
    dependsOn(tasks.generateGrammarSource)
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
