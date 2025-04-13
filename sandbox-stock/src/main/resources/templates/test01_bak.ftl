<!DOCTYPE html>
<html lang="en" style="height: 100%">
<head>
    <meta charset="utf-8">
</head>
<body style="height: 100%; margin: 0">
<div id="container" style="height: 100%"></div>

<script type="text/javascript" src="https://echarts.apache.org/zh/js/vendors/jquery@3.7.1/dist/jquery.min.js"></script>
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@5/dist/echarts.min.js"></script>

<!-- Uncomment this line if you want to dataTool extension
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@5/dist/extension/dataTool.min.js"></script>
-->
<!-- Uncomment this line if you want to use gl extension
<script type="text/javascript" src="https://echarts.apache.org/zh/js/vendors/echarts-gl/dist/echarts-gl.min.js"></script>
-->
<!-- Uncomment this line if you want to echarts-stat extension
<script type="text/javascript" src="https://echarts.apache.org/zh/js/vendors/echarts-stat/dist/ecStat.min.js"></script>
-->
<!-- Uncomment this line if you want to echarts-graph-modularity extension
<script type="text/javascript" src="https://echarts.apache.org/zh/js/vendors/echarts-graph-modularity/dist/echarts-graph-modularity.min.js"></script>
-->
<!-- Uncomment this line if you want to use map
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@4.9.0/map/js/world.js"></script>
-->
<!-- Uncomment these two lines if you want to use bmap extension
<script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=YOUR_API_KEY"></script>
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@5/dist/extension/bmap.min.js"></script>
-->

<script type="text/javascript">
    var dom = document.getElementById('container');
    var myChart = echarts.init(dom, null, {
        renderer: 'canvas',
        useDirtyRect: false
    });
    var app = {};
    var ROOT_PATH = 'https://echarts.apache.org/examples';
    var option;

    $.get('/data/asset/data/life-expectancy-table',
        function (_rawData) {
            run(JSON.parse(_rawData));
        }
    );

    function run(_rawData) {
        const countries = [
            'Finland',
            'France',
            'Germany',
            'Iceland',
            'Norway',
            'Poland',
            'Russia',
            'United Kingdom'
        ];
        const datasetWithFilters = [];
        const seriesList = [];
        echarts.util.each(countries, function (country) {
            var datasetId = 'dataset_' + country;
            // datasetWithFilters.push({
            //     id: datasetId,
            //     fromDatasetId: 'dataset_raw',
            //     transform: {
            //         type: 'filter',
            //         config: {
            //             and: [
            //                 { dimension: 'Year', gte: 1950 },
            //                 { dimension: 'Country', '=': country }
            //             ]
            //         }
            //     }
            // });
            seriesList.push({
                type: 'line',
                datasetId: datasetId,
                showSymbol: false,
                name: country,
                endLabel: {
                    show: true,
                    formatter: function (params) {
                        return params.value[3] + ': ' + params.value[0];
                    }
                },
                labelLayout: {
                    moveOverlap: 'shiftY'
                },
                emphasis: {
                    focus: 'series'
                },
                encode: {
                    x: 'Year',
                    y: 'Income',
                    label: ['Country', 'Income'],
                    itemName: 'Year',
                    tooltip: ['Income']
                }
            });
        });
        debugger;
        option = {
            animationDuration: 10000,
            dataset: [
                {
                    id: 'dataset_raw',
                    source: _rawData
                },
                ...datasetWithFilters
            ],
            title: {
                text: 'Income of Germany and France since 1950'
            },
            tooltip: {
                order: 'valueDesc',
                trigger: 'axis'
            },
            xAxis: {
                type: 'category',
                nameLocation: 'middle'
            },
            yAxis: {
                name: 'Income'
            },
            grid: {
                right: 140
            },
            series: seriesList
        };
        console.info(option)
        debugger
        myChart.setOption(option);
    }

    if (option && typeof option === 'object') {
        myChart.setOption(option);
    }

    window.addEventListener('resize', myChart.resize);
</script>
</body>
</html>
