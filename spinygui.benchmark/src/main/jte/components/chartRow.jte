@import com.spinyowl.spinygui.benchmark.report.BenchmarkReportView.ChartRow
@param ChartRow row
<div class="row"><span>${row.label()}</span><div class="track"><div class="bar ${row.cssClass()}" style="width:${row.width()}%"></div><span class="budget-marker marker-120"></span><span class="budget-marker marker-60"></span></div><span>${row.value()}${row.suffix()}</span></div>
