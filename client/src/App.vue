<template>
    <div>
<!--        <Map v-if="isMapShow" :webSocketurl="baseUrl + webSocketList?.length ? webSocketList[0] : ''"> </Map>-->
      <Map v-if="isMapShow" webSocketurl="ws:localhost:8080/websocket/locationCount"> </Map>
    </div>

</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import Map from './components/map.vue';
import { getWebSocketList } from '@/api/feng';
let baseUrl = 'ws://localhost:8080';
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



<style scoped></style>
