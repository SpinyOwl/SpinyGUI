package com.spinyowl.spinygui.benchmark.report;

/** Trusted page inputs supplied to the local benchmark report template. */
public record BenchmarkReportPage(BenchmarkReportView report, String chartJs,
    String chartBootstrap, String chartDataJson) { }
