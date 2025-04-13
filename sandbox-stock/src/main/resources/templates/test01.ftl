<!DOCTYPE html>
<html lang="en" style="height: 100%">
<head>
    <meta charset="utf-8">
    <link href="https://cdn.staticfile.net/bootstrap/5.2.0/css/bootstrap.min.css" rel="stylesheet" />

</head>
<body style="height: 100%; margin: 0">
<button id="start_btn" type="button" class="btn btn-sm btn-info">开始</button>
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

    // $.get('/data/asset/data/life-expectancy-table',
    //     function (_rawData) {
    //         console.info(_rawData);
    //         console.info(JSON.parse(_rawData));
    //         // run(JSON.parse(_rawData));
    //     }
    // );

    $("#start_btn").click(function (){

        $.get('/api/stock/data/list?start=20230101&end=20300101&codes=002032,000333,000538,600036&klt=101',
            function (res) {
                var codeList  =  res.codeList;
                var _rawData  =  res.dataList;
                run(codeList, _rawData);
            }
        );
    });




    function run(codeList, _rawData) {
        const countries = [
            '苏泊尔',
            '美的集团'
        ];
        const datasetWithFilters = [];
        const seriesList = [];
        echarts.util.each(codeList, function (country) {
            var datasetId = 'dataset_' + country;
            datasetWithFilters.push({
                id: datasetId,
                fromDatasetId: 'dataset_raw',
                transform: {
                    type: 'filter',
                    config: {
                        and: [
                            { dimension: 'date', gte: 20160101},
                            { dimension: 'name', '=': country }
                        ]
                    }
                }
            });
            seriesList.push({
                type: 'line',
                datasetId: datasetId,
                showSymbol: false,
                name: country,
                endLabel: {
                    show: true,
                    formatter: function (params) {
                        // console.info(params) 2-name 1-code
                        return params.value[2] + ' ' + params.value[3];
                    }
                },
                labelLayout: {
                    moveOverlap: 'shiftY'
                },
                emphasis: {
                    focus: 'series'
                },
                encode: {
                    x: 'date',
                    y: 'price',
                    label: ['name', 'price'],
                    itemName: 'date',
                    tooltip: ['price']
                }
            });
        });


        // debugger;
        option = {
            animationDuration: 50000,
            dataset: [
                {
                    id: 'dataset_raw',
                    source: _rawData
                },
                ...datasetWithFilters
            ],
            title: {
                text: '如果贾老板23年100万年买了这些票',
                left: 'center',
                textAlign : 'center'
            },
            tooltip: {
                order: 'valueDesc',
                trigger: 'axis'
            },
            xAxis: {
                name: '日期',
                type: 'category',
                nameLocation: 'middle'
            },
            yAxis: {
                name: '市值',
                position: 'left'
            },
            grid: {
                right: 140
            },
            series: seriesList
        };
        myChart.setOption(option);
    }

    if (option && typeof option === 'object') {
        myChart.setOption(option);
    }

    window.addEventListener('resize', myChart.resize);
</script>
</body>
</html>
