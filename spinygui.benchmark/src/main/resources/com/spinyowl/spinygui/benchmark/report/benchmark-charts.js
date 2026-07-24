(() => {
    'use strict';
    const data = JSON.parse(document.getElementById('benchmark-chart-data').textContent);
    data.chartPayloadVersion;
    const report = data.charts;
    const colors = { text:'#e8edf5', muted:'#aebed0', grid:'#304052', blue:'#55b6e8', orange:'#e8a855', purple:'#b27ce8', warning:'#e85c55' };

    function mountChart(canvasId, config) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) return null;
        try {
            const chart = new Chart(canvas, config);
            canvas.closest('.chart-shell').dataset.chartReady = 'true';
            return chart;
        } catch (error) {
            console.error(`Unable to initialize ${canvasId}`, error);
            return null;
        }
    }

    function chartOptions(xScale) {
        return {
            indexAxis:'y',
            responsive:true,
            maintainAspectRatio:false,
            animation:false,
            scales:{
                x:{...xScale, ticks:{color:colors.muted}, grid:{color:colors.grid}},
                y:{ticks:{color:colors.muted}, grid:{color:colors.grid}}
            },
            plugins:{legend:{labels:{color:colors.text}}, tooltip:{backgroundColor:'#080c12', titleColor:colors.text, bodyColor:colors.text}}
        };
    }

    function cpuConfig(label, color, value, tooltipLines) {
        const options = chartOptions({type:'logarithmic'});
        options.plugins.tooltip.callbacks = {label(context) {
            return tooltipLines(report.cpu[context.dataIndex]);
        }};
        return {
            type:'bar',
            data:{labels:report.cpu.map(row => row.label), datasets:[{label, data:report.cpu.map(value), backgroundColor:color}]},
            options
        };
    }

    const budgetMarkers = {
        id:'budgetMarkers',
        afterDraw(chart) {
            const context = chart.ctx;
            const scale = chart.scales.x;
            context.save();
            context.strokeStyle = '#f2d05c';
            context.setLineDash([5, 4]);
            for (const value of [8333, 16667]) {
                const x = scale.getPixelForValue(value);
                context.beginPath();
                context.moveTo(x, chart.chartArea.top);
                context.lineTo(x, chart.chartArea.bottom);
                context.stroke();
            }
            context.restore();
        }
    };

    function renderingConfig(label, fields) {
        const options = chartOptions({type:'linear', min:0, max:16667});
        return {
            type:'bar',
            data:{
                labels:report.rendering.map(row => row.label),
                datasets:fields.map(([name, field, color]) => ({
                    label:name,
                    data:report.rendering.map(row => row[field]),
                    backgroundColor(context) { return Number(context.raw) > 16667 ? colors.warning : color; }
                }))
            },
            options,
            plugins:[budgetMarkers]
        };
    }

    mountChart('cpu-latency-chart', cpuConfig('Latency (us/op)', colors.blue, row => row.latency,
        row => [`${row.latency} us/op`, `Uncertainty: ${row.uncertainty === null ? 'not reported' : `${row.uncertainty} us/op`}`]));
    mountChart('cpu-allocation-chart', cpuConfig('Allocation (B/op)', colors.orange, row => row.allocation,
        row => [`${row.allocation} B/op`, `Allocation rate: ${row.allocationRate === null ? 'not reported' : `${row.allocationRate} MB/sec`}`]));
    mountChart('cpu-rendering-chart', renderingConfig('CPU submission latency', [
        ['Median', 'cpuMedian', colors.blue], ['p95', 'cpuP95', colors.orange], ['p99', 'cpuP99', colors.purple]
    ]));
    mountChart('gpu-rendering-chart', renderingConfig('GPU-complete latency', [
        ['Median', 'gpuMedian', colors.blue], ['p95', 'gpuP95', colors.orange], ['p99', 'gpuP99', colors.purple]
    ]));

    const trends = new Map(report.trends.map(trend => [trend.id, trend]));
    const trendButtons = Array.from(document.querySelectorAll('[data-trend-id]'));
    let historyChart = null;
    let activeTrend = null;

    function historyConfig(trend) {
        activeTrend = trend;
        return {
            type:'line',
            data:{labels:report.historyRuns, datasets:[{label:`${trend.label} (${trend.unit})`, data:trend.values,
                borderColor:colors.blue, backgroundColor:colors.blue, pointBackgroundColor:'#f2d05c', spanGaps:false}]},
            options:{responsive:true, maintainAspectRatio:false, animation:false,
                scales:{x:{ticks:{color:colors.muted},grid:{color:colors.grid}},
                    y:{min:trend.minimum,max:trend.maximum,ticks:{color:colors.muted},grid:{color:colors.grid}}},
                plugins:{legend:{labels:{color:colors.text}}, tooltip:{backgroundColor:'#080c12', titleColor:colors.text, bodyColor:colors.text,
                    callbacks:{title(context) { return report.historyRuns[context[0].dataIndex]; }, label(context) {
                        return `${Number(context.parsed.y).toLocaleString()} ${activeTrend.unit}`;
                    }, afterLabel(context) { return `Change: ${activeTrend.changes[context.dataIndex] ?? 'not available'}`; }}}}}
        };
    }

    function activateTrend(button) {
        const trend = trends.get(button.dataset.trendId);
        if (!trend) {
            console.error(`Unable to select missing history trend ${button.dataset.trendId}`);
            return;
        }
        if (!historyChart) return;
        const selectedButton = trendButtons.find(candidate => candidate.getAttribute('aria-pressed') === 'true');
        if (selectedButton) selectedButton.setAttribute('aria-pressed', 'false');
        button.setAttribute('aria-pressed', 'true');
        const canvas = document.getElementById('history-chart');
        canvas.setAttribute('aria-label', `${trend.label} history trend in ${trend.unit}. Use the precise history tables below for values.`);
        historyChart.data.datasets[0].label = `${trend.label} (${trend.unit})`;
        historyChart.data.datasets[0].data = trend.values;
        activeTrend = trend;
        historyChart.options.scales.y.min = trend.minimum;
        historyChart.options.scales.y.max = trend.maximum;
        historyChart.update();
    }

    const initialButton = trendButtons.find(button => button.getAttribute('aria-pressed') === 'true');
    if (initialButton) {
        const initialTrend = trends.get(initialButton.dataset.trendId);
        if (initialTrend) {
            const canvas = document.getElementById('history-chart');
            canvas.setAttribute('aria-label', `${initialTrend.label} history trend in ${initialTrend.unit}. Use the precise history tables below for values.`);
            historyChart = mountChart('history-chart', historyConfig(initialTrend));
        }
    }
    trendButtons.forEach(button => button.addEventListener('click', () => activateTrend(button)));
})();
