plugins {
    id("buildlogic.java-library-conventions")
    id("jacoco")
    id("antlr")
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
    api(variantOf(libs.lwjgl) { classifier("natives-windows") })
    api(variantOf(libs.lwjgl) { classifier("natives-linux") })
    api(variantOf(libs.lwjgl) { classifier("natives-macos") })

    api(libs.lwjglStb)
    api(variantOf(libs.lwjglStb) { classifier("natives-windows") })
    api(variantOf(libs.lwjglStb) { classifier("natives-linux") })
    api(variantOf(libs.lwjglStb) { classifier("natives-macos") })

    api(libs.lwjglYoga)
    api(variantOf(libs.lwjglYoga) { classifier("natives-windows") })
    api(variantOf(libs.lwjglYoga) { classifier("natives-linux") })
    api(variantOf(libs.lwjglYoga) { classifier("natives-macos") })

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
