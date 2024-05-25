<template>
    <div id="sentiment-wrapper" class="wrapper"></div>

</template>
<script setup lang="ts">
import { startSentimentTrend } from '@/api/feng';
import * as echarts from 'echarts';
import { onMounted } from 'vue';
let baseUrl = import.meta.env.VITE_WEBSOCKET_URL;
const webSocketurl = baseUrl + "/websocket/sentiment_trend";
let socket: WebSocket;

let positiveData: any = [];
let negativeData: any = [];
let neutralData: any = [];
let xAxisData: any = [];
var myChart: any;
function initEcharts() {


    var option = {
        title: {
            text: '情感趋势'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['积极的', '消极的', '中立的']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: xAxisData
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '积极的',
                type: 'line',

                data: positiveData,
            },
            {
                name: '消极的',
                type: 'line',
                stack: 'Total',
                data: negativeData
            },
            {
                name: '中立的',
                type: 'line',
                stack: 'Total',
                data: neutralData
            },

        ]
    };


    option && myChart.setOption(option);
}
onMounted(async () => {
    const res = await startSentimentTrend();
    var chartDom = document.getElementById('sentiment-wrapper');
    myChart = echarts.init(chartDom);
    if (res.success) {
        socket = new WebSocket(webSocketurl);
        socket.addEventListener('message', function (event) {
            const data = JSON.parse(event.data);
            xAxisData = [];
            positiveData = [];
            negativeData = [];
            neutralData = [];
            for (let key in Reflect.ownKeys(data)) {
                xAxisData.push(key);
                positiveData.push(data[key]?.positive);
                negativeData.push(data[key]?.negative);
                neutralData.push(data[key]?.neutral)
            }
            myChart.setOption({
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: xAxisData
                },
                series: [
                    {
                        data: positiveData
                    },
                    {
                        data: negativeData
                    },
                    {
                        data: neutralData
                    }
                ]
            })

        })
    }
    initEcharts();
})
</script>
<style scoped>
.wrapper {
    height: 300px;
    width: 700px;
    padding: 10px;
}
</style>