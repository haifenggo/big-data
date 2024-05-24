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
comprehensive是分区名字，里面的数组是每一个元素表示一个主题的关键词集合。第二个是关键词的权重。建议画热力图什么的。
下面这里表示一个主题，主题里面包含两个关键词。即一个分区会有三个主题，一个主题包含n个关键词。
```json
{
    "决定": "0.001",
    "吃": "0.002"
}
```
下面是整个数据的格式。
```json
{
    "comprehensive": [
        {
            "决定": "0.001",
            "吃": "0.002"
        },
        {
            "哈哈": "0.3",
            "嘻嘻": "0.4"
        }
    ],
    "anime": [
        {
            "无酸纸": "0.333"
        },
        {
            "变色": "0.777"
        }
    ]
}

```

### 词云图
生成了一张词云图，放在`/src/main/resources/data`目录下

### 每个分区的前十平均播放量和弹幕数量
redis的key是`top_10_views`和`top_10_comments`
数据格式
```json
{
    "comprehensive": 100,
    "anie": 200,
}
```