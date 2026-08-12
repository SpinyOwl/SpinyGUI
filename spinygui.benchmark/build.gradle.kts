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
            || archive.resolve("text-diagnostics-$identifier.json").exists()

    override fun close() {
        reservation?.delete()
    }
}

class TimestampedReportArgumentAction(
    private val archive: File,
    private val filePrefix: String,
    private val outputOption: String?,
    private val runIdService: Provider<BenchmarkRunIdService>,
    private val pairing: String
) : Action<Task> {
    override fun execute(task: Task) {
        archive.mkdirs()
        val runId = runIdService.get().runId()
        val output = archive.resolve("$filePrefix-$runId.json").absolutePath
        if (outputOption == null) {
            (task as JavaExec).args(output, runId, pairing)
        } else {
            (task as JavaExec).args(outputOption, output, "--spiny-run-id", runId, "--spiny-pairing", pairing)
        }
    }
}

class ArchiveReportArgumentAction(
    private val archive: File,
    private val runIdService: Provider<BenchmarkRunIdService>
) : Action<Task> {
    override fun execute(task: Task) {
        archive.mkdirs()
        val runId = runIdService.get().runId()
        (task as JavaExec).args(archive.absolutePath, archive.resolve("index.html").absolutePath, runId)
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

fun Task.freshBenchmarkRun() {
    doNotTrackState(
        "Benchmark artifacts use a runtime-reserved archive ID and must execute fresh; Gradle cannot safely track dynamic output paths."
    )
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
    mainClass.set("com.spinyowl.spinygui.benchmark.cpu.CpuBenchmarkMain")
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
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "text-calculation", "-rff", benchmarkRunId, "unpaired-investigation"))
    freshBenchmarkRun()
}

tasks.register<JavaExec>("jmhRendering") {
    group = "benchmark"
    description = "Runs the hidden-context NanoVG rendering benchmark and writes a JSON report."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.rendering.RenderingBenchmarkMain")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "nanovg-text", null, benchmarkRunId, "unpaired-investigation"))
    freshBenchmarkRun()
}

tasks.register<JavaExec>("counterDiagnostics") {
    group = "benchmark"
    description = "Runs identified text workloads without timing and writes diagnostic counters."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.diagnostic.CounterDiagnosticsMain")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "text-diagnostics", null, benchmarkRunId, "unpaired-investigation"))
    freshBenchmarkRun()
}

tasks.register<JavaExec>("localImageComparison") {
    group = "verification"
    description = "Explicitly captures and compares approved local renderer boundary scenes."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonMain")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    args(
        layout.projectDirectory.dir("local-image-references").asFile.absolutePath,
        layout.buildDirectory.dir("local-image-comparison").get().asFile.absolutePath
    )
    providers.systemProperty("spinygui.rendering.localImageComparison").orNull?.let {
        systemProperty("spinygui.rendering.localImageComparison", it)
    }
}

val benchmarkReportCpu = tasks.register<JavaExec>("benchmarkReportCpu") {
    group = "benchmark"
    description = "Runs the CPU half of one report-owned paired benchmark run."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.cpu.CpuBenchmarkMain")
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
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "text-calculation", "-rff", benchmarkRunId, "paired-report"))
    freshBenchmarkRun()
}

val benchmarkReportRendering = tasks.register<JavaExec>("benchmarkReportRendering") {
    group = "benchmark"
    description = "Runs the rendering half of one report-owned paired benchmark run."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.rendering.RenderingBenchmarkMain")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    dependsOn(benchmarkReportCpu)
    mustRunAfter(benchmarkReportCpu)
    doFirst(TimestampedReportArgumentAction(benchmarkArchive.asFile, "nanovg-text", null, benchmarkRunId, "paired-report"))
    freshBenchmarkRun()
}

tasks.register<JavaExec>("generateBenchmarkReport") {
    group = "benchmark"
    description = "Regenerates the HTML report and normalized manifest from the existing local archive."
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGenerator")
    args(benchmarkArchive.asFile.absolutePath, benchmarkArchive.file("index.html").asFile.absolutePath)
}

tasks.register<JavaExec>("benchmarkReport") {
    group = "benchmark"
    description = "Runs local benchmarks and writes a self-contained HTML report plus normalized archive manifest."
    dependsOn(benchmarkReportRendering)
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGenerator")
    doFirst(ArchiveReportArgumentAction(benchmarkArchive.asFile, benchmarkRunId))
    freshBenchmarkRun()
}
