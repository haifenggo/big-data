# B站热榜分析大数据项目

## 前言
随着互联网技术的飞速发展，大数据的分析和应用已经成为了各行各业的重要课题。在众多的数据源中，视频分享网站Bilibili（简称B站）因其庞大的用户基数和丰富的内容类型，成为了大数据分析的理想平台。通过对B站的数据进行分析，我们可以挖掘出用户的喜好、视频内容的流行趋势以及社会热点的传播路径，这对于内容创作者、广告商以及研究机构都具有极高的价值。

本项目旨在构建一个基于大数据技术的B站热榜分析系统。通过爬虫技术实时抓取B站视频及其评论数据，利用先进的大数据处理框架进行存储、计算和分析，最终以前端可视化的形式展现出来。这不仅可以帮助我们更好地理解B站的用户行为和内容趋势，同时也是一个实践大数据技术、提升数据处理能力的优秀案例。

本项目的实现涵盖了从前端展示到后端处理、从实时数据抓取到离线数据分析的全栈技术。我们使用了Spring Boot、Flink、Kafka、Redis、MongoDB等主流技术，以及Vue3等前端框架，保证了系统的先进性、稳定性和可扩展性。

在开始使用本项目之前，请确保您已经具备了一定的编程基础和大数据技术知识。本项目的文档将为您提供详细的安装指南、使用说明和功能介绍，帮助您快速上手和深入了解B站热榜分析大数据项目。

## 快速开始
### 安装组件
#### 安装Zookeeper
1. 使用Docker运行Zookeeper服务：
```bash
docker run -d --name zookeeper -p 2181:2181 -v /etc/localtime:/etc/localtime wurstmeister/zookeeper
```
#### 安装Kafka
1. 使用Docker运行Kafka服务：
```bash
docker run -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=192.168.101.101:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.101.101:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka
```
#### 创建MongoDB容器
1. 使用Docker运行MongoDB服务：
```bash
docker run -d --name mongo -v /usr/local/mongodb/data/db:/data/db -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=mongoroot --privileged=true mongo
```
2. 创建数据库和用户：
```bash
use big-data
db.createUser({ user: "root", pwd: "big-data-root", roles: [{ role: "readWrite", db: "big-data" }] })
```
#### 创建Redis容器
1. 使用Docker运行Redis服务：
```bash
docker run -p 6379:6379 --name redis -v /data/redis/conf/redis.conf:/etc/redis/redis.conf -v /data/redis/data:/var/lib/redis -v /data/redis/logs:/logs -d redis:6.0.5 redis-server /etc/redis/redis.conf --appendonly yes --requirepass redisroot
```
#### 创建Flink
1. 使用Docker拉取Flink镜像：
```bash
docker pull flink:1.13.6-scala_2.12-java8
```
2. 创建Docker网络：
```bash
docker network create flink-network
```
3. 启动JobManager：
```bash
docker run -itd --name=jobmanager --publish 8081:8081 --network flink-network --env FLINK_PROPERTIES="jobmanager.rpc.address: jobmanager" flink:1.13.6-scala_2.12-java8 jobmanager
```
4. 启动TaskManager：
```bash
docker run -itd --name=taskmanager --network flink-network --env FLINK_PROPERTIES="jobmanager.rpc.address: jobmanager" flink:1.13.6-scala_2.12-java8 taskmanager
```
### 应用程序启动指南
#### 爬虫程序使用步骤
1. **环境要求**：确保Python版本为3.9或以上。
2. **安装依赖**：通过命令`pip install -r requirements.txt`安装必需的Python库。
3. **配置修改**：编辑`config.ini`文件，更新Kafka和Redis的配置信息；MongoDB的配置则无需更改。
4. **启动程序**：运行命令`python main.py`以启动爬虫。
5. **守护进程模式**：使用`nohup`命令可以使得爬虫在后台以守护进程模式持续运行。
#### Spring Boot项目启动
在更新`application.yaml`中的相关配置后，启动Spring Boot应用程序。
#### Flink任务启动
##### 直接启动
- 在测试环境中，可以直接启动`src/main/java/com/bigdata/bigdata/task`目录下的Flink任务。
- 或者，直接运行`flink-task/src/main/java/com/bigdata/LocationCountTask.java`和`VideoTopTask.java`中的main方法来启动任务。
##### 打包为JAR提交
- 修改`flink-task/pom.xml`文件中的`<manifest><mainClass>com.bigdata.LocationCountTask</mainClass></manifest>`配置。
- 使用Maven工具分别为这两个任务打包，生成JAR文件。
- 通过Apache Flink的Web UI（访问地址为http://192.168.101.101:8081），手动将JAR包提交到Docker容器中的Flink集群。
#### 前端项目启动
1. **安装依赖**：进入`client`目录，执行`npm install`以安装项目所需的依赖包。
2. **开发服务器启动**：依赖安装完成后，在项目根目录下运行命令`npm run dev`，以启动Vite开发服务器。
3. **项目命令查看**：`package.json`文件中包含了可用的NPM脚本命令，可以通过查看`scripts`部分来了解每个命令的具体用途。

## 主要功能

### 爬虫部分

1. 连接MongoDB数据库和Redis数据库
2. 从Bilibili网站爬取视频数据(包括视频标题、播放量、弹幕数等信息)和弹幕评论数据
3. 对爬取的视频数据进行分析，统计播放量前十的视频和弹幕数前十的视频
4. 对弹幕评论进行情感分析，统计正面、负面、中性的评论数量
5. 对爬取的视频数据进行点赞、投币、收藏等趋势分析
6. 对弹幕评论进行LDA主题分析，获取不同主题的关键词
7. 将分析结果存储到Redis数据库中
8. 将爬取的视频数据和弹幕评论数据发送到Kafka消息队列
9. 每隔一段时间(如10秒)重复执行以上功能，实现定时爬取和分析Bilibili网站数据的效果

### java部分

使用Spring Boot、Kafka和Flink来收集、处理和分发视频数据，以及生成各种统计数据和排行榜。

1. 允许客户端获取WebSocket服务的列表，以及启动各种数据获取任务。这些任务包括获取地理位置统计、情感趋势、点赞趋势、视频排行榜和前十名的视频。这些任务会将数据从Redis数据库中读取并通过WebSocket发送给客户端。
2. 监听指定的Kafka主题并处理接收到的消息。它包含以下几个功能：

   接收并处理与视频发布位置相关的统计数据，将这些数据用于生成地理位置统计信息。

   接收并处理视频数据消息，将这些视频数据存储到MongoDB数据库中，以便进行进一步的分析和查询。

   接收并处理评论数据消息，将这些评论数据存储到MongoDB数据库中，以便进行情感分析和内容分析。

   接收并处理视频排行榜数据消息，将这些数据存储到Redis数据库中，以便快速访问和展示视频排行榜信息。
3. Flink任务计算视频排行榜，从Kafka主题中读取视频数据，通过滑动窗口计算每个板块中得分最高的视频，并将这些视频的统计数据发送到另一个Kafka主题。
4. Flink任务计算视频发布位置的统计数据，从Kafka主题中读取视频数据，通过滑动窗口计算每个位置的独特视频数量，并将这些统计数据发送到另一个Kafka主题。

### 前端部分

前端部分的主要功能是与后端服务器进行交互，以获取和处理各种数据更新，并通过WebSocket连接实时接收这些更新。
1. 获取WebSocket连接列表：前端通过发送GET请求到`/webSocketList`端点，获取所有可用的WebSocket连接的列表。这些连接用于订阅不同类型的数据更新，如位置计数、情感趋势、点赞趋势等。
2. 开始数据获取任务：前端通过发送GET请求到特定的任务接口（如`/startLocationCount`、`/startSentimentTrend`、`/startLikesTrend`等），触发后端开始执行相应的数据获取任务。一旦任务被触发，后端将通过WebSocket连接定期发送数据更新。
3. 接收和处理WebSocket数据：前端建立WebSocket连接后，可以订阅并接收后端推送的数据更新。这些数据更新可以包括位置计数信息、情感趋势、点赞趋势等。前端需要根据接收到的数据更新用户界面，以显示最新的信息。
4. 数据展示：前端根据接收到的数据，以图形、表格或其它形式展示给用户。例如，可以展示不同地区的位置计数信息、情感趋势的变化图表、点赞趋势的统计等。
5. 数据交互：前端可能还提供用户交互功能，如允许用户选择查看特定类型的数据、筛选数据、调整显示选项等。

前端的主要功能是与后端服务器进行交互，获取实时数据更新，并将这些数据以用户友好的方式展示给用户。同时，前端还需要处理WebSocket连接的建立和维护，以及数据的实时订阅和接收。



## 贡献者

<!-- readme: collaborators,contributors -start -->
<!-- readme: collaborators,contributors -end -->

## 技术栈

- SpringBoot 2.6.13
- Jdk 1.8
- Vue3 3.2.+
- python3.9
- Docker
- Redis 6.0.5
- MongoDB
- Flink 1.13.6 (with Scala 2.12 and Java 8)
- Kafka 
- ZooKeeper 

