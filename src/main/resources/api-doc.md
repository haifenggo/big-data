### 1. 获取WebSocket连接列表
#### GET `/webSocketList`
获取所有可用的WebSocket连接的列表。

**请求描述:**

用户通过发送GET请求到`/webSocketList`端点，以获取服务器上可用的WebSocket连接列表。

**响应描述:**

服务器将返回一个JSON对象，其中包含一个名为`data`的属性，该属性是一个数组，包含了所有可用的WebSocket连接的路径。

**响应示例:**

```json
{
  "success": true,
  "msg": null,
  "data": [
    "/websocket/locationCount",
    "/websocket/sentiment_trend",
    "/websocket/likes_trend"
  ]
}
```
#### WebSocket连接与数据接收
一旦客户端获取到WebSocket连接列表，它可以使用这些路径连接到相应的WebSocket服务器。例如，要连接到位置计数WebSocket服务，客户端可以使用以下URL：
```
ws://localhost:8080/websocket/locationCount
```
连接到WebSocket服务后，客户端可以订阅并接收每2秒一次的数据更新。这些更新将通过WebSocket连接推送到客户端。

为了开始接收数据，客户端需要先通过HTTP GET请求触发相应的任务接口。例如，要开始接收位置计数数据，客户端需要向`/startLocationCount`接口发送GET请求。一旦任务被触发，服务器将每2秒通过WebSocket连接发送一次数据。

关闭WebSocket连接后，任务自动关闭。

### 2. 开始位置计数任务

#### GET `/startLocationCount`
开始位置计数任务，每2秒执行一次。

**响应示例:**

- 成功:
  ```json
  {
    "success": true,
    "msg": null,
    "data": null
  }
  ```
- 失败:
  ```json
  {
    "success": false,
    "msg": "任务添加失败",
    "data": null
  }
  ```





####  2.1. WebSocket响应数据格式

服务器将通过WebSocket每两秒发送一次位置计数信息，数据格式如下：

```json
{
    "浙江": {
        "count": "3",
        "timestamp": "1716217190000"
    },
    "上海": {
        "count": "2",
        "timestamp": "1716217190000"
    },
    "四川": {
        "count": "1",
        "timestamp": "1716217190002"
    },
    "北京": {
        "count": "1",
        "timestamp": "1716217190000"
    },
    "湖北": {
        "count": "1",
        "timestamp": "1716217190000"
    },
    "广东": {
        "count": "2",
        "timestamp": "1716217190002"
    }
}
```

##### 响应数据说明

- **count**: 表示该位置的计数数量，例如浙江的计数为3。
- **timestamp**: 表示该位置计数信息的最近更新时间点，以毫秒为单位的时间戳。例如，浙江的最后更新时间为1716217190000。

##### 前端处理

前端接收到这些数据后，可以根据`timestamp`来判断数据的有效性。例如，如果需要显示最近5秒的数据，前端可以根据当前时间戳和`timestamp`的差值来决定是否显示该数据。

通过这种方式，前端可以动态地更新用户界面，以反映最新的位置计数信息。



### 3. 获取情感趋势

#### GET `/startSentimentTrend`
获取情感趋势，每2秒执行一次。

**响应示例:**

- 成功:
  ```json
  {
    "success": true,
    "msg": null,
    "data": null
  }
  ```
- 失败:
  ```json
  {
    "success": false,
    "msg": "任务添加失败",
    "data": null
  }
  ```

#### 数据格式

```json
{
    "0": {
        "positive": 1,
        "negative": 1,
        "neutral": 0
    },
    "1": {
        "positive": 2,
        "negative": 0,
        "neutral": 0
    },
    "5": {
        "positive": 1,
        "negative": 0,
        "neutral": 0
    }
}
```



### 4. 获取点赞趋势

#### GET `/startLikesTrend`
获取点赞趋势，每2秒执行一次。

**响应示例:**

- 成功:
  ```json
  {
    "success": true,
    "msg": null,
    "data": null
  }
  ```
- 失败:
  ```json
  {
    "success": false,
    "msg": "任务添加失败",
    "data": null
  }
  ```
  

#### 数据格式

```json
{
    "0": {
        "coins": 214046,
        "likes": 508293,
        "favorites": 226946
    },
    "1": {
        "coins": 82854,
        "likes": 304874,
        "favorites": 31492
    },
    "2": {
        "coins": 5764,
        "likes": 132407,
        "favorites": 6184
    }
}
```







### 5. 获取视频分区得分top

#### GET `/video_top`

获取分区得分top的视频和地址。

**响应示例:**

- 成功:

  ```json
  {
    "success": true,
    "msg": null,
    "data": null
  }
  ```

- 失败:

  ```json
  {
    "success": false,
    "msg": "任务添加失败",
    "data": null
  }
  ```

#### 数据格式

```json
{
    "national_original": "{\"bvid\":\"BV1Ug411F7f8\",\"score\":\"1.67735204E7\",\"title\":\"【黄逗菌】第七十六集  查 寝？ 对 我 没 用！\",\"board\":\"national_original\",\"url\":\"www.bilibili.com/BV1Ug411F7f8\"}",
    "comprehensive": "{\"bvid\":\"BV1Kt421u7mJ\",\"score\":\"1.31560247E7\",\"title\":\"你把戒指吃下去了啊？...唔？！\",\"board\":\"comprehensive\",\"url\":\"www.bilibili.com/BV1Kt421u7mJ\"}"
}
```





