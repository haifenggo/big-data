<template>
    <div id="like-wrapper" class="wrapper"></div>

</template>
<script setup lang="ts">
import { startLikesTrend } from '@/api/feng';
import * as echarts from 'echarts';
import { onMounted } from 'vue';
let baseUrl = 'ws://9206xosk7874.vicp.fun';
baseUrl = import.meta.env.VITE_WEBSOCKET_URL;
const webSocketurl = baseUrl + '/websocket/likes_trend';
let socket: WebSocket;

let coinsData: any = [];
let likesData: any = [];
let favoritesData: any = [];
let xAxisData: any = [];
var myChart: any;
function initEcharts() {


    var option = {
        title: {
            text: '点赞趋势'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['投币', '点赞数', '收藏数']
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
                name: '投币',
                type: 'line',

                data: coinsData,
            },
            {
                name: '点赞数',
                type: 'line',
                stack: 'Total',
                data: likesData
            },
            {
                name: '收藏数',
                type: 'line',
                stack: 'Total',
                data: favoritesData
            },

        ]
    };


    option && myChart.setOption(option);
}
onMounted(async () => {
    const res = await startLikesTrend();
    var chartDom = document.getElementById('like-wrapper');
    myChart = echarts.init(chartDom);
    if (res.success) {

        socket = new WebSocket(webSocketurl);
        socket.addEventListener('message', function (event) {
            const data = JSON.parse(event.data);
            xAxisData = [];
            coinsData = [];
            likesData = [];
            favoritesData = [];
            for (let key in Reflect.ownKeys(data)) {
                xAxisData.push(key);
                coinsData.push(data[key]?.coins);
                likesData.push(data[key]?.likes);
                favoritesData.push(data[key]?.favorites)
            }
            myChart.setOption({
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: xAxisData
                },
                series: [
                    {
                        data: coinsData
                    },
                    {
                        data: likesData
                    },
                    {
                        data: favoritesData
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