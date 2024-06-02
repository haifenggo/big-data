<template>
    <div id="main" style="width: 100%;height: 100%;"></div>
</template>
<script setup lang="ts">
import * as echarts from 'echarts';
import chinaMap from '@/assets/china.json';
import { onMounted, reactive, defineProps, onBeforeUnmount, ref } from 'vue';
import { top10 } from '../mock';
import { startLocationCount } from '@/api/feng';

let baseUrl = import.meta.env.VITE_WEBSOCKET_URL;
const webSocketurl = baseUrl + '/websocket/locationCount';


const speacialProvinces = ['北京市', '天津市', '重庆市', '上海市', '内蒙古自治区', '广西壮族自治区', '西藏自治区', '宁夏回族自治区', '新疆维吾尔自治区',
    '香港特别行政区', '澳门特别行政区'
]
interface LocationCount {
    name: string;
    value: number;
}
const locationCount = ref([] as any);
const solveProvince = (name: string) => {
    let len = name.length;
    let res: any = null;
    speacialProvinces.forEach(city => {
        let count = 0;

        for (let c of name) {
            if (city.indexOf(c) != -1) count++;
        }
        if (count / len > 0.5) res = city;

    })
    if (res) return res;
    return name + '省';
}
var myChart: any;
var data: any = {};
const initCharts = () => {
    let min = 100;
    let max = 0;
    locationCount.value = [];
    const curDate = new Date();
    for (let key of Object.getOwnPropertyNames(data)) {

        if (curDate.getTime() - data[key].timestamp < 60000) {
            locationCount.value.push({
                name: solveProvince(key),
                value: + (data as any)[key]?.count

            })
            min = Math.min(min, +(data as any)[key]?.count)
            max = Math.max(max, +(data as any)[key]?.count)
        }


    }

    const option = {
        title: {
            text: '热点',
            subtext: '数据来源：哔哩哔哩',
            sublink:
                'http://zh.wikipedia.org/wiki/%E9%A6%99%E6%B8%AF%E8%A1%8C%E6%94%BF%E5%8D%80%E5%8A%83#cite_note-12'
        },
        tooltip: {
            trigger: 'item',
            formatter: '{b}<br/>{c}'
        },
        visualMap: {
            min: min,
            max: max,
            text: ['高', '低'],
            realtime: false,
            calculable: true,
            inRange: {
                color: ['lightskyblue', 'yellow', 'orangered']
            },
            top: "50%"
        },

        // visualMap: {
        //     min: 800,
        //     max: 50000,
        //     text: ['High', 'Low'],
        //     realtime: false,
        //     calculable: true,
        //     inRange: {
        //         color: ['lightskyblue', 'yellow', 'orangered']
        //     }
        // },
        series: [
            {
                name: '热点分布',
                type: 'map',
                map: '中国',
                label: {
                    show: false
                },
                data: locationCount.value,

            }
        ]
    }
    myChart.setOption(option);

}
let socket: WebSocket;
onMounted(async () => {
    const ele = document.getElementById('main');
    myChart = echarts.init(ele);
    echarts.registerMap('中国', chinaMap as any);
    initCharts();
    const res = await startLocationCount()
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