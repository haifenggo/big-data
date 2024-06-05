<template>
    <div>
        <div class="container-header">

            <div class="location">
                <i class="icon iconfont icon-buoumaotubiao23"></i>
                <span class="areaName"></span>
            </div>
            <h3>B站热榜分析大数据项目</h3>
        </div>
        <div class="container-content">
            <div class="top">
                <div class="count-base">

                    <div class="com-screen-content">

                        <div id="main1" style="width:100%;height:500px;"><Map></Map></div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div>
                <div class="count-resource q1">
                    <div class="com-count-title">词云</div>
                    <div class="com-screen-content2">

                        <div class="com-screen-content">
                            <div id="main2" style="margin-top:10px;width:100%;height:300px;" @click="handleChangeImage">
                                <img style="width: 100%;object-fit: " id="ciyun" :src="imagrUrl" alt="">
                                <div>
                                    <ul class="nowTime" style="font-size: 50px;margin-top: 20px;">
                                        <li v-html="currentdate"></li>
                                        <li>{{ HMS }}</li>

                                    </ul>
                                </div>
                            </div>
                        </div>
                        <span class="left-top"></span>
                        <span class="right-top"></span>
                        <span class="left-bottom"></span>
                        <span class="right-bottom"></span>
                    </div>
                </div>
                <div class="count-resource q2">
                    <div class="com-count-title">热门视频</div>


                    <div class="com-screen-content">

                        <div id="main3" style="margin-top:10px;width:100%;height:420px;overflow-y: auto;">
                            <Top></Top>
                        </div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div>
            </div>

            <div class="mid">
                <div class="count-share w1">
                    <div class="com-count-title"></div>
                    <div class="com-screen-content">
                        <LinksTrend></LinksTrend>



                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div>
                <div class="count-share w2">
                    <div class="com-count-title"></div>
                    <div class="com-screen-content">
                        <div id="main5" style="width:100%;height:300px;">
                            <SentimentTrend></SentimentTrend>
                        </div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div>
            </div>

            <div class="bottom">
                <div class="count-topic e1">
                    <div class="com-count-title">每个分区的前十平均播放量和弹幕数量</div>
                    <div class="com-screen-content">
                        <div id="main6" style="width:100%;height:300px;">
                            <Top10></Top10>
                        </div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div>
                <!-- <div class="count-topic e2">
                    <div class="com-count-title">主题库共享次数</div>
                    <div class="com-screen-content">
                        <div id="main7" style="width:100%;height:300px;"></div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div> -->
            </div>
            <div class="bottom">
                <div class="count-topic e1">
                    <div class="com-count-title">LDA主题</div>
                    <div class="com-screen-content">
                        <div id="main6" style="width:100%;height:300px;">
                            <LDA></LDA>
                        </div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div>
                <!-- <div class="count-topic e2">
                    <div class="com-count-title">主题库共享次数</div>
                    <div class="com-screen-content">
                        <div id="main7" style="width:100%;height:300px;"></div>
                    </div>
                    <span class="left-top"></span>
                    <span class="right-top"></span>
                    <span class="left-bottom"></span>
                    <span class="right-bottom"></span>
                </div> -->
            </div>
            <div class="clearfix"></div>
        </div>
    </div>


</template>
<script lang="ts" setup>
import Map from './map.vue';
import LinksTrend from './LinksTrend.vue';
import SentimentTrend from './SentimentTrend.vue';
import Top from './Top.vue';
import LDA from './LDA.vue';
import Top10 from './Top10.vue';
import { ref } from 'vue';

let imageBaseUrl = import.meta.env.VITE_BASE_URL;
const imagrUrl = imageBaseUrl + '/static/wordcloud.png';
let start = false;
const handleChangeImage = () => {
    //if (start) return;
    start = true;

    const ele = document.getElementById('ciyun');
    let date = new Date();
    let temp = imagrUrl + '?t=' + date.getTime();
    ele?.setAttribute('src', temp);
    setTimeout(() => {
        start = false;
    }, 2000);
}
const HMS = ref('' as any);
const currentdate = ref('' as any);
//获取当前时间

function getNowFormatDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month: any = date.getMonth() + 1;
    var strDate: any = date.getDate();
    var Hour: any = date.getHours();       // 获取当前小时数(0-23)
    var Minute: any = date.getMinutes();     // 获取当前分钟数(0-59)
    var Second: any = date.getSeconds();     // 获取当前秒数(0-59)
    var show_day = new Array('星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六');
    var day = date.getDay();
    if (Hour < 10) {
        Hour = "0" + Hour;
    }
    if (Minute < 10) {
        Minute = "0" + Minute;
    }
    if (Second < 10) {
        Second = "0" + Second;
    }
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    currentdate.value = '<div><p style="font-size:30px">' + year + '年' + month + '月' + strDate + '号 ' + show_day[day] + '</p></div>';
    HMS.value = Hour + ':' + Minute + ':' + Second;

    //$('.topRec_List li div:nth-child(3)').html(temp_time);
    setTimeout(getNowFormatDate, 1000);//每隔1秒重新调用一次该函数
}
getNowFormatDate();
</script>

<style>
/* @import '../assets/css/test-1024'; */
/* @import '../assets/css/test-1280'; */
@import '../assets/css/test-1280';



/* @media screen and (min-width:1600px) {
    @import '../assets/css/test-1920';
} */
</style>