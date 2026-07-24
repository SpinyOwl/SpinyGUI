import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.JavaExec

interface BenchmarkRunIdParameters : BuildServiceParameters {
    val archive: DirectoryProperty
    val baseOverride: org.gradle.api.provider.Property<String>
}

abstract class BenchmarkRunIdService : BuildService<BenchmarkRunIdParameters>, AutoCloseable {
    private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSSSSSSSS")
    private var reservation: File? = null
    private val runId by lazy { reserveRunId() }

    fun runId(): String = runId

    private fun reserveRunId(): String {
        val archive = parameters.archive.asFile.get()
        archive.mkdirs()
        val base = parameters.baseOverride.orNull?.takeIf { it.isNotBlank() } ?: formatter.format(LocalDateTime.now())
        require(runCatching { LocalDateTime.parse(base, formatter) }.isSuccess) {
            "benchmarkRunIdBase must use yyyyMMdd-HHmmss-SSSSSSSSS"
        }
        var sequence = 0
        while (true) {
            val identifier = if (sequence == 0) base else "$base-${sequence.toString().padStart(6, '0')}"
            val candidate = archive.resolve(".$identifier.benchmark-run.lock")
            if (hasArchivedOutput(archive, identifier)) {
                sequence++
                continue
            }
            if (!candidate.createNewFile()) {
                sequence++
                continue
            }
            if (!hasArchivedOutput(archive, identifier)) {
                reservation = candidate
                return identifier
            }
            candidate.delete()
            sequence++
        }
    }

    private fun hasArchivedOutput(archive: File, identifier: String): Boolean =
        archive.resolve("text-calculation-$identifier.json").exists()
            || archive.resolve("nanovg-text-$identifier.json").exists()

    override fun close() {
        reservation?.delete()
    }
}

class TimestampedReportArgumentAction(
    private val archive: File,
    private val filePrefix: String,
    private val outputOption: String?,
    private val runIdService: Provider<BenchmarkRunIdService>
) : Action<Task> {
    override fun execute(task: Task) {
        archive.mkdirs()
        val output = archive.resolve("$filePrefix-${runIdService.get().runId()}.json").absolutePath
        if (outputOption == null) (task as JavaExec).args(output) else (task as JavaExec).args(outputOption, output)
    }
}

class ArchiveReportArgumentAction(private val archive: File) : Action<Task> {
    override fun execute(task: Task) {
        archive.mkdirs()
        (task as JavaExec).args(archive.absolutePath, archive.resolve("index.html").absolutePath)
    }
}

class PrintReservedRunIdAction(
    private val runIdService: Provider<BenchmarkRunIdService>,
    private val holdMillis: Long
) : Action<Task> {
    override fun execute(task: Task) {
        println("Reserved benchmark run ID: ${runIdService.get().runId()}")
        if (holdMillis > 0) Thread.sleep(holdMillis)
    }
}

plugins {
    id("buildlogic.java-library-conventions")
    alias(libs.plugins.jte)
}

dependencies {
    implementation(project(":spinygui.core"))
    implementation(project(":spinygui.core.backend.lwjgl.nanovg"))
    implementation(libs.gson)
    implementation(libs.jmhCore)
    implementation(libs.jteRuntime)
    annotationProcessor(libs.jmhGeneratorAnnprocess)
}

val jteClasses = layout.buildDirectory.dir("jte-classes")

jte {
    precompile()
    targetDirectory = layout.buildDirectory.dir("jte-classes").get().asFile.toPath()
    compilePath.setFrom(
        sourceSets["main"].output.classesDirs,
        configurations.runtimeClasspath
    )
}

sourceSets.named("main") {
    output.dir(jteClasses)
}

tasks.named("classes") {
    dependsOn("precompileJte")
}

tasks.named<Jar>("jar") {
    dependsOn("precompileJte")
    exclude("gg/jte/generated/precompiled/**/*.java")
}

tasks.named<JavaCompile>("compileTestJava") {
    dependsOn("precompileJte")
}

tasks.withType<Test>().configureEach {
    dependsOn("precompileJte")
}

val benchmarkArchive = layout.projectDirectory.dir("reports")
val benchmarkRunId = gradle.sharedServices.registerIfAbsent("benchmarkRunId", BenchmarkRunIdService::class) {
    parameters.archive.set(benchmarkArchive)
    parameters.baseOverride.set(providers.gradleProperty("benchmarkRunIdBase").orElse(""))
}

tasks.register("reserveBenchmarkRunId") {
    group = "benchmark"
    description = "Reserves and prints a fresh archive run ID without running benchmarks."
    doLast(PrintReservedRunIdAction(benchmarkRunId, providers.gradleProperty("benchmarkRunIdHoldMillis").orNull?.toLongOrNull() ?: 0))
}

tasks.register<JavaExec>("jmhCpu") {
    group = "benchmark"
    description = "Runs CPU text benchmarks and writes a JSON report."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.openjdk.jmh.Main")
    args(
        "com.spinyowl.spinygui.benchmark.cpu.*",
        "-wi", "3",
        "-i", "5",
        "-w", "500ms",
        "-r", "500ms",
        "-f", "2",
        "-jvmArgsAppend", "--enable-native-access=ALL-UNNAMED",
        "-prof", "gc",
        "-rf", "json"
    )
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "text-calculation", "-rff", benchmarkRunId))
}

tasks.register<JavaExec>("jmhRendering") {
    group = "benchmark"
    description = "Runs the hidden-context NanoVG rendering benchmark and writes a JSON report."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.rendering.RenderingBenchmarkMain")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "nanovg-text", null, benchmarkRunId))
}

tasks.register<JavaExec>("benchmarkReport") {
    group = "benchmark"
    description = "Runs local benchmarks and writes a self-contained HTML report."
    dependsOn("jmhCpu", "jmhRendering")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGenerator")
    doFirst(ArchiveReportArgumentAction(benchmarkArchive.asFile))
}
