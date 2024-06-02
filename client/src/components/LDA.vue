<template>
    <div>
        <div class="m-4">

            <el-cascader v-model="value" :options="options" @change="handleChange" placeholder="选择主题" />
        </div>
        <div style="display: flex;justify-content: space-between;">
            <div id="main-pie1" style="width: 100%;height: 300px;"></div>
            <div id="main-pie2" style="width: 100%;height: 300px;"></div>
            <div id="main-pie3" style="width: 100%;height: 300px;"></div>
        </div>
    </div>
</template>
<script setup lang="ts">
import * as echarts from 'echarts';

import { onMounted, reactive, defineProps, onBeforeUnmount, ref } from 'vue';

import { startLDA } from '@/api/feng';
import { translateToChinese } from '@/util';

let baseUrl = import.meta.env.VITE_WEBSOCKET_URL;
const webSocketurl = baseUrl + '/websocket/lda_topics';

const value = ref([] as any)
const handleChange = (value: any) => {

    curData = data[value[0]];

    let i = 0;
    for (; i < 3; i++) {
        initCharts(myChart[i], curData[i]);
    }
}
var isOption = false;
const options = ref([] as any);
var myChart: any = [];
var data: any = {};
var curData: any = {};
const initCharts = (chart: any, curData: any) => {


    let pieData = [];
    for (let key of Object.getOwnPropertyNames(curData)) {
        let obj: any = {};
        obj.name = key;
        obj.value = curData[key];
        pieData.push(obj);
    }

    const option = {
        title: {


            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },

        series: [
            {
                name: '权重',
                type: 'pie',
                radius: '50%',
                data: pieData,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };


    chart.setOption(option);

}
let socket: WebSocket;
onMounted(async () => {
    const ele1 = document.getElementById('main-pie1');
    const ele2 = document.getElementById('main-pie2');
    const ele3 = document.getElementById('main-pie3');

    myChart.push(echarts.init(ele1));
    myChart.push(echarts.init(ele2));
    myChart.push(echarts.init(ele3));
    const res = await startLDA()
    if (res.success) {

        socket = new WebSocket(webSocketurl);
        socket.addEventListener('message', function (event) {

            data = JSON.parse(event.data);

            if (!isOption) {
                for (let key of Object.getOwnPropertyNames(data)) {
                    let obj = {} as any;

                    obj.value = key;

                    obj.label = translateToChinese(key);
                    options.value.push(obj);
                }

                value.value = [options.value[0].value!];
                handleChange(value.value);
                isOption = true;


            }

        })

    }


})
onBeforeUnmount(() => {
    socket.close();
})
</script>