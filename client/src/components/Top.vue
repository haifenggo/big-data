<template>
    <div class="wrapper-video">
        <template v-for="item in data">
            <el-row class="video-row" :gutter="20">
                <el-col :span="6">
                    <div>{{ translateToChinese(item.board) }}</div>
                </el-col>
                <el-col :span="18">
                    <el-link :href="'https:' + item.url" target="_blank" :underline="false">{{ item.title }}
                    </el-link>

                </el-col>

            </el-row>
        </template>


    </div>
</template>
<script setup lang="ts">
import * as echarts from 'echarts';
import chinaMap from '@/assets/china.json';
import { onMounted, reactive, defineProps, onBeforeUnmount, ref } from 'vue';
import { startVideoTop } from '@/api/feng';
import { translateToChinese } from '@/util.ts';
let baseUrl = import.meta.env.VITE_WEBSOCKET_URL;
const webSocketurl = baseUrl + '/websocket/video_top';



interface LocationCount {
    name: string;
    value: number;
}
const locationCount = ref([] as any);

var myChart: any;
var data = ref([] as any);

let socket: WebSocket;
onMounted(async () => {
    const ele = document.getElementById('main');
    myChart = echarts.init(ele);
    echarts.registerMap('中国', chinaMap as any);
    const res = await startVideoTop()
    if (res.success) {
        socket = new WebSocket(webSocketurl);
        socket.addEventListener('message', function (event) {

            data.value = JSON.parse(event.data);


        })

    }


})
onBeforeUnmount(() => {
    socket.close();
})




</script>
<style scoped>
.wrapper-video {
    max-height: 500px;
    display: flex;
    flex-direction: column;
    width: 100%;
    margin: 0;
    padding: 10px;
    box-sizing: border-box;
    color: red;
}

.video-row {
    font-size: 16px;


}

.video-row:hover {
    font-size: 18px;
    color: blue;
    box-shadow: 0 0 20px 0 #dfe1e6;
}
</style>