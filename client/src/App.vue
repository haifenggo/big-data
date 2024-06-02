<template>
    <!-- <div>
        <h1 style="margin:0 auto ;font-size: 20px;margin-top: 10px" align="center">B站热榜分析大数据项目</h1>
        <div style="display: flex;flex-direction: row;flex-wrap: wrap;padding: 20px;justify-content: space-around;">
            <Map class="border"> </Map>
            <LinksTrend class="border"></LinksTrend>

            <SentimentTrend class="border"></SentimentTrend>
            <div style="width: 400px;">
                <img style="width: 100%;object-fit: " src="http://9206xosk7874.vicp.fun/static/wordcloud.png" alt="">
            </div>
            <Top class="border"></Top>
        </div>
    </div> -->
    <Template></Template>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Map from './components/map.vue';
import { getWebSocketList } from '@/api/feng';
import LinksTrend from './components/LinksTrend.vue';
import SentimentTrend from './components/SentimentTrend.vue';
import Top from './components/Top.vue';
import Template from './components/template.vue';
let webSocketList = ref([] as any);
const isMapShow = ref(false);
if (process.env.NODE_ENV === 'production') {
    console.log('生产环境');
}

onMounted(async () => {
    try {
        const res = await getWebSocketList();
        if (res?.success) {
            webSocketList.value = res.data;
            isMapShow.value = true;

        }
    }
    catch (e) {
        console.log('error', e);
    }
})
</script>



<style>
.border {
    margin-top: 10px;
    border: 1px solid #eeeeee;

    box-sizing: border-box;
}
</style>
