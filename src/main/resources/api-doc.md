
### 1. 开启位置计数任务
#### 1.1 请求信息
- **URL:** `/startLocationCountTask`
- **Method:** `GET`
- **功能描述:** 启动一个定时任务，每隔一段时间统计一次位置信息，并将结果发送到WebSocket。
#### 1.2 请求参数
无
#### 1.3 响应参数
- **Result**: 
  - **data**: `"/websocket/locationCount"`为websocket的url地址。
#### 1.4 示例
- **请求示例**: `GET /startLocationCountTask`
- **响应示例**: 
  - 成功: `{"success": true, "msg": null, "data": "/websocket/locationCount"}`
---
### 2. 关闭位置计数任务
#### 2.1 请求信息
- **URL:** `/cancelLocationCountTask`
- **Method:** `GET`
- **功能描述:** 关闭定时任务。
#### 2.2 请求参数
无
---
### 3. 消费位置计数信息
#### 3.1 请求信息
- **URL:** `/websocket/locationCount`
- **Method:** `WebSocket`
- **功能描述:** 接收WebSocket服务器发送的位置计数信息。
#### 3.2 请求参数
无

#### 3.3 响应数据

#####  响应数据格式

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

