# 爬虫类使用方法
* 需要`python3.9`
* 安装需要的python库`pip install -r requirements.txt`
* 修改`config.ini`文件里面的kafka和redis配置,mongo不用管
* 运行`python main.py`
* nohup启动后，爬虫会以守护进程的方式运行
## Mongo数据
### 分区数据
对于每一个分区，名字对应对应分区的字段名是
* comprehensive: 综合热榜
* national_original: 国创相关
* anime: 动漫
* music: 音乐
* dancing: 舞蹈
* games: 游戏
* knowledge: 知识
* technology: 科技
* sports: 运动
* fashion: 时尚
* fun: 娱乐
* movies: 影视
* origin: 原创
* rookie: 新人
每一个分区的数据包含：
* title: 视频名
* onwerName: 作者名
* views: 播放量
* comments: 评论数
* favorites: 收藏数
* likes: 点赞数
* coins: 投币数
* shares: 分享数
* pubLocation: 发布地点
* uploadTime: 发布时间
* duration: 视频时长
* bvid: bvid
* rank: 在当前分区的排名
* rcmdReason: 推荐理由（仅综合热榜有）
### 分区弹幕
对于每一个分区的弹幕，所在的topic是comments.
弹幕包含三种数据：
* content: 弹幕内容
* videoTime: 在视频里面出现的时间（相对于视频时间）
* videoRealTime: 在视频里面出现的时间（相对于真实世界的时间）
时间都使用时间戳表示
## Redis数据
### 情感分析
redis里面存储了情感趋势。key是`sentiment_trend`, value是`json`，get出来之后转成`json`
### 点赞投币趋势
redis里面存储量点赞投币趋势。key是`likes_trend`, value是`json`get出来之后转成`json`
### LDA 主题topics
使用LDA算法为每个分区的弹幕划分了三个主题，每个主题都带有几个关键词。在redis中，key是`lda_topics`，value是`json`。
如下所示：
* comprehensive是分区名字，里面的数组是每一个数组表示一个主题的关键词, 第二个是关键词的权重。建议画热力图什么的。
```
{
    "comprehensive": [
        [
            {
                "aaa": "0.05682724"
            },
            {
                "bbb": "0.032545965"
            },
            {
                "ccc": "0.03247017"
            },
        ],
        [
            {
                "ab": "0.040921286"
            },
            {
                "bc": "0.023386508"
            },
        ]
    ]
}
```
### 高频词
使用LDA算法为每个分区跳出了十个高频词。在redis中，key是`hot_words`，value是`json`。

### 词云图
生成了一张词云图，放在`/src/main/resources/data`目录下