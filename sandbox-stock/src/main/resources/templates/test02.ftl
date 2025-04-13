<!--
	此示例下载自 https://echarts.apache.org/examples/zh/editor.html?c=bar-race-country
-->
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

    const updateFrequency = 1000;
    const dimension = 0;
    const codes = "002032,000333,000538,600036,000625,601038,000768,002714,002475,000830"

    $("#start_btn").click(function (){

        $.get('/api/stock/data/list?start=20230101&end=20300101&klt=101&codes=' + codes,
            function (res) {
                const data = res.dataList;
                const nodeList = res.colors;
                // 日期列表
                const years = [];
                for (let i = 0; i < data.length; ++i) {
                    if (years.length === 0 || years[years.length - 1] !== data[i][0]) {
                        years.push(data[i][0]);
                    }
                }

                let startIndex = 10;
                let startYear = years[startIndex];
                option = {
                    grid: {
                        top: 50,
                        bottom: 50,
                        left: 150,
                        right: 120
                    },
                    xAxis: {
                        name: '资产/百万元',
                        max: 'dataMax',
                        axisLabel: {
                            formatter: function (n) {
                                console.info(" n is " , n)
                                return n; // Math.round(n) + '';
                            }
                        }
                    },
                    dataset: {
                        // slice 提取指定范围的方法
                        source: data.slice(1).filter(function (d) {
                            return d[0] === startYear;
                        })
                    },
                    yAxis: {
                        name: '标的',
                        type: 'category',
                        inverse: true,
                        max: 10,
                        axisLabel: {
                            show: true,
                            fontSize: 20,
                            formatter: function (value) {
                                return value; // + '{flag|' + getFlag(value) + '}';
                            },
                            rich: {
                                flag: {
                                    fontSize: 25,
                                    padding: 5
                                }
                            }
                        },
                        animationDuration: 300,
                        animationDurationUpdate: 300
                    },
                    series: [
                        {
                            realtimeSort: true,
                            seriesLayoutBy: 'column',
                            type: 'bar',
                            itemStyle: {
                                color: function (param) {
                                    return nodeList[param.value[2]] || '#5470c6';
                                }
                            },
                            // 设置x=3 价格 和 y=2 名称
                            encode: {
                                x: 3,
                                y: 2
                            },
                            label: {
                                show: true,
                                precision: 1,
                                position: 'right',
                                fontSize: 20,
                                valueAnimation: true,
                                fontFamily: 'monospace',
                                // formatter: function (val) {
                                //     console.info(val);
                                //     return (val / 1000000).toFixed(4);
                                // }
                            }
                        }
                    ],
                    // Disable init animation.
                    animationDuration: 0,
                    animationDurationUpdate: updateFrequency,
                    animationEasing: 'linear',
                    animationEasingUpdate: 'linear',
                    graphic: {
                        elements: [
                            {
                                type: 'text',
                                right: 160,
                                bottom: 60,
                                style: {
                                    text: startYear,
                                    font: 'bolder 80px monospace',
                                    fill: 'rgba(100, 100, 100, 0.25)'
                                },
                                z: 100
                            }
                        ]
                    }
                };
                // console.log(option);
                myChart.setOption(option);
                for (let i = startIndex; i < years.length - 1; ++i) {
                    (function (i) {
                        setTimeout(function () {
                            updateYear(years[i + 1]);
                        }, (i - startIndex) * updateFrequency);
                    })(i);
                }

                function updateYear(year) {
                    let source = data.slice(1).filter(function (d) {
                        return d[0] === year;
                    });
                    option.series[0].data = source;
                    option.graphic.elements[0].style.text = year;
                    myChart.setOption(option);
                }
            }
        );
    });

    // $.when(
    //     $.getJSON('/data'),
    //     $.getJSON('/api/stock/data/list?start=20230101&end=20300101&klt=102&codes=' + codes)
    // ).done(function (res0, res1) {
    //     console.info(res0)
    //     console.info(res1)
    // });

    if (option && typeof option === 'object') {
        myChart.setOption(option);
    }

    window.addEventListener('resize', myChart.resize);
</script>
</body>
</html>
