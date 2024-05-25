<template>
    <div class="wrapper-video">
        <template v-for="item in data">
            <el-row class="video-row" :gutter="20">
                <el-col :span="4">
                    <div>{{ translateToChinese(item.board) }}</div>
                </el-col>
                <el-col :span="20">
                    <el-link :href=item.url target="_blank" :underline="false">{{ item.title }}</el-link>

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
// 创建一个映射对象，将英文字段映射到中文字段  
const translationMap: any = {
    "comprehensive": "综合热榜",
    "national_original": "国创相关",
    "anime": "动漫",
    "music": "音乐",
    "dancing": "舞蹈",
    "games": "游戏",
    "knowledge": "知识",
    "technology": "科技",
    "sports": "运动",
    "fashion": "时尚",
    "fun": "娱乐",
    "movies": "影视",
    "origin": "原创",
    "rookie": "新人"
};

// 定义一个函数来转换英文字段到中文字段  
function translateToChinese(englishTerm: string) {
    return translationMap[englishTerm] || englishTerm;
}


</script>
<style scoped>
.wrapper-video {
    max-height: 500px;
    display: flex;
    flex-direction: column;
    width: 30vw;
    margin: 0;
    padding: 10px;
    box-sizing: border-box;

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