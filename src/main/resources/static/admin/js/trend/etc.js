const chartLib = {
    init() {
        const el = document.getElementById("chart-data");
        if (el == null) return;

        const {now, oneWeek, oneMonth } = JSON.parse(el.innerHTML);

        // 현재 트렌드는 파이 그래프로 처리
        this.drawPieGraph(now.keywords, 'now-chart');

        // 일주일간 누적 트렌드 라인 그래프로 처리
        this.drawLineGraph(oneWeek, "week-chart");

        // 한달간 누적 트렌드 라인 그래프로 처리
        this.drawLineGraph(oneMonth, "month-chart");
    },
    // 파이 그래프 그리기
    drawPieGraph(keywords, targetId) {
       keywords = JSON.parse(keywords);
       const labels = Object.keys(keywords);
       const values = Object.values(keywords);

       const ctx = document.getElementById(targetId);

       new Chart(ctx, {
            type: 'pie',
            data: {
                labels,
                datasets: [{
                  label: '핫 트렌드',
                  data: values,
                  borderWidth: 1
                }]
            },
            options: {
                scales: {
                  y: {
                    beginAtZero: true
                  }
                }
            }
       });
    },
    // 라인 그래프 그리기
    drawLineGraph(data, targetId) {
        labels = Object.keys(data);

        const _datasets = {};
        for (const [key, value] of Object.entries(data)) {
            for (const [k, v] of Object.entries(value)) {
                _datasets[k] = _datasets[k] ?? {}
                for (const label of labels) {
                    _datasets[k][label] = _datasets[k][label] ?? 0;
                }
                 _datasets[k][key] = v;
            }
        }


        const datasets = [];
        for (const [key, value] of Object.entries(_datasets)) {
            datasets.push({
               label: key,
               data: Object.values(value),
               fill: false,
               tension: 0.1
            });
        }

        const ctx = document.getElementById(targetId);
        const _data = {
            labels,
            datasets,
        };
        const config = {
          type: 'line',
          data: _data,
        };

       new Chart(ctx,config);
    }
};

window.addEventListener("DOMContentLoaded", function() {
    chartLib.init();
});