<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>数据展示</title>
<#--    <link rel="Shortcut Icon" href="${request.contextPath}/static/favicon.ico">-->
<#--    <link href="${request.contextPath}/static/js/bootstrap.min.css" rel="stylesheet" />-->
    <link href="https://cdn.staticfile.net/bootstrap/5.2.0/css/bootstrap.min.css" rel="stylesheet" />

<#--    <script src="${request.contextPath}/static/js/jquery.min.js"></script>-->
<#--    <script src="${request.contextPath}/static/js/vue.min.js"></script>-->
<#--    <script src="${request.contextPath}/static/js/echarts.min.js"></script>-->

    <script src="https://cdn.staticfile.net/jquery/3.2.0/jquery.min.js"></script>
    <script src="https://cdn.staticfile.net/vue/2.6.0/vue.min.js"></script>
    <script src="https://cdn.staticfile.net/echarts/4.8.0/echarts.js"></script>
<#--    <script src="https://cdn.jsdelivr.net/npm/echarts@5.3.2/dist/echarts.js"></script>-->
<#--    <script src="https://cdn.staticfile.net/echarts/5.3.0/echarts.simple.min.js"></script>-->
<#--    <script src="https://cdn.staticfile.net/echarts/5.3.2/echarts.simple.min.js"></script>-->
<#--    <script src="https://cdn.staticfile.net/echarts/5.3.2/theme/dark.min.js"></script>-->

<#--    https://www.staticfile.net/
腾讯云
http://lib.sinaapp.com/
https://blog.csdn.net/qq_62541773/article/details/145587724

-->

</head>
<body>

<div class="container-fluid">
    <div class="row-fluid">

        <div id="rrapp" v-cloak>


            <div>
                <button type="button" class="btn btn-primary btn-sm" @click="query">查询</button>
            </div>

            <div id="main" style="width: 600px; height: 400px;"></div>

        </div>

    </div>
</div>




<#--<script src="https://cdn.staticfile.net/jquery/3.3.0/jquery.min.js"></script>-->
<#--<script src="https://cdn.staticfile.net/vue/2.7.14/vue.min.js"></script>-->
<#--<script src="https://cdn.staticfile.net/echarts/5.4.3/echarts.min.js"></script>-->
<#--<script src="https://cdn.staticfile.net/bootstrap/5.3.2/css/bootstrap.min.css"></script>-->

<script>

    $(function () {

        var vm = new Vue({
            el: '#rrapp',
            data: {
                q: {
                    code: null,
                    codes: ''
                },
                showList: true,
                title: null,
                dict: {}
            },
            mounted() {
                this.query();
            },
            methods: {

                query: function () {

                    console.info("data !")
                    var app_chart = echarts.init(document.getElementById('main'));
                    // 指定图表的配置项和数据


                    var option = {
                        title: {
                            text: 'ECharts 示例'
                        },
                        tooltip: {},
                        legend: {
                            data:['销量']
                        },
                        xAxis: {
                            data: ["衬衫","羊毛衫","雪纺衫","裤子","高跟鞋","袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    app_chart.setOption(option);

                    // window.addEventListener('resize', function () {
                    //     app_chart.resize();
                    // });
                    console.info("data 123")
                },
                add: function () {
                    vm.showList = false;
                    vm.title = "新增";
                    vm.dict = {};
                },
                reload: function (){

                }


            }
        });



    })


</script>

</body>
</html>
