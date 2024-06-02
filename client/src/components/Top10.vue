<template>
    <div id="main-top10" style="width: 100%;height: 100%;"></div>
</template>
<script setup lang="ts">
import * as echarts from 'echarts';

import { onMounted, onBeforeUnmount, ref } from 'vue';

import { startTop10 } from '@/api/feng';
import { translateToChinese } from '@/util.ts';
let baseUrl = import.meta.env.VITE_WEBSOCKET_URL;
const webSocketurl = baseUrl + '/websocket/top_10';




var myChart: any;
var data: any = {};
const initCharts = () => {


    let commentsData = [];
    let viewsData = [];
    let xData = [];
    let xDataLabel = [];

    for (let key of Object.getOwnPropertyNames(data['top_10_comments'])) {

        commentsData.push(data['top_10_comments'][key]);
        xDataLabel.push(translateToChinese(key));
        xData.push(key);
    }
    for (let key of xData) {

        viewsData.push(data['top_10_views'][key]);

    }

    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {},
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: xDataLabel
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '评论',
                type: 'bar',
                emphasis: {
                    focus: 'series'
                },
                data: commentsData
            },
            {
                name: '播放量',
                type: 'bar',

                emphasis: {
                    focus: 'series'
                },
                data: viewsData
            },

        ]
    };

    myChart.setOption(option);

}
let socket: WebSocket;
onMounted(async () => {
    const ele = document.getElementById('main-top10');
    myChart = echarts.init(ele);

    //initCharts();
    const res = await startTop10()
    if (res.success) {

        socket = new WebSocket(webSocketurl);
        socket.addEventListener('message', function (event) {

            data = JSON.parse(event.data);

            initCharts();

        })

    }


})
onBeforeUnmount(() => {
    socket.close();
})
</script>