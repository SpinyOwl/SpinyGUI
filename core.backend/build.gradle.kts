plugins {
    id("buildlogic.java-library-conventions")
    id("jacoco")
}

dependencies {
    api(project(":core"))
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
