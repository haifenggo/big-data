import pymongo
import redis
import sys
from configparser import ConfigParser
from crawler import *
from analyze import *
from dbController import *
from kafkaController import *
from confluent_kafka import Producer, Consumer, KafkaException
from trans import *
from LDA import *
import collections
import webbrowser

def connectMongo():
    # 读取配置文件
    config = ConfigParser()
    config.read('config.ini')
    # 从配置文件中获取MongoDB连接参数
    host = config.get('mongodb', 'host')
    port = config.getint('mongodb', 'port')
    username = config.get('mongodb', 'username')
    password = config.get('mongodb', 'password')
    database = config.get('mongodb', 'database')
    # 构建MongoDB URI
    uri = f"mongodb://{username}:{password}@{host}:{port}/{database}"
    # uri = f"mongodb://{host}:{port}/{database}"
    # 连接MongoDB数据库
    client = pymongo.MongoClient(uri)
    # 选择数据库和集合
    mydb = client[database]
    # 现在你可以执行MongoDB操作，比如插入、查询等
    return mydb


def connectRedis():
    # 读取配置文件
    config = ConfigParser()
    config.read('config.ini')

    # 从配置文件中获取Redis连接参数
    redisHost = config.get('redis', 'host')
    redisPort = config.getint('redis', 'port')
    redisPassword = config.get('redis', 'password')
    # 连接Redis数据库
    redisClient = redis.StrictRedis(host=redisHost, port=redisPort, password=redisPassword, decode_responses=True)
    # redisClient = redis.StrictRedis(host=redisHost, port=redisPort, decode_responses=True)
    return redisClient

def Sentiment(all_comment, redisCli):
    # 情感分析
    classified_texts = classify(all_comment)
    classified_emotions = SentimentAnalysis(classified_texts)
    redisCli.set('sentiment_trend', str(json.dumps(classified_emotions)))


def likesAnalyze(all_data, redisCli):
    rankStats = rankCount(all_data)
    # print(rankStats)
    redisCli.set('likes_trend', str(json.dumps(rankStats)))

def connect():
    # 读取配置文件
    # 从配置文件中获取MongoDB连接参数
    host = 'localhost'
    port = 27017
    # username = config.get('mongodb', 'username')
    # password = config.get('mongodb', 'password')
    database = 'bigdata'
    # 构建MongoDB URI
    # uri = f"mongodb://{username}:{password}@{host}:{port}/{database}"
    uri = f"mongodb://{host}:{port}/{database}"
    # 连接MongoDB数据库
    client = pymongo.MongoClient(uri)
    # 选择数据库和集合
    mydb = client[database]
    # 现在你可以执行MongoDB操作，比如插入、查询等
    return mydb

def LDA(comment_list, redisCli):
    # 创建一个字典，用于存储不同板块的评论列表
    board_comments = defaultdict(list)
    topics = defaultdict(list)
    # 将评论按照板块分类
    for i in range(len(comment_list)):
        board = comment_list[i]["board"]
        content = comment_list[i]["content"]
        board_comments[board].append(content)
    board_word_list = defaultdict(list)
    all_words = []
    for key, value in board_comments.items():
        li = get_word_list(value)
        for w in li:
            all_words.extend(w)
        board_word_list[key] = li

    for key, value in board_word_list.items():
        ret = lda_topic_modeling(value)
        new_topic = []
        for topic in ret:
            tdict = {}
            for word in topic:
                tdict.update(word)
            new_topic.append(tdict) 
        topics[key] = new_topic
    redisCli.set('lda_topics', str(json.dumps(topics)))

    counterRet = collections.Counter(all_words) #对分词做词频统计
    wordCloud(counterRet)

def boardAverageTop(all_data, redisCli):
    # print(all_data)
    top_10_views = calcBoardTop10Views(all_data)
    top_10_comments = calcDanmakuBoard(all_data)
    redisCli.set('top_10_views', str(top_10_views))
    redisCli.set('top_10_comments', str(top_10_comments))
    # print("---------")
    # print(str(top_10_views))
    # print(str(top_10_comments))
    # print("---------")

def getData():
    db = connect()
    col = db['comment']
    cur = col.find()
    d = []
    cnt = 0
    for line in cur:
        d.append(line)
        cnt += 1
        if cnt > 10:
            break
    return d

if __name__ == '__main__':
    redisDB = connectRedis()
    kafkaProducer = connectKafka()
    # print(kafkaProducer)
    # print(redisDB)
    print("=" * 12 + "Crawler Start" + "=" * 12)
    while True:
        all_data, all_comment = [], []
        Get_data(all_data=all_data, all_comment=all_comment)
        time.sleep(2)
        getOthers(all_data = all_data, all_comment = all_comment)
        print("=====================================")
        print("================send=================")
        print("=====================================")
        send_to_kafka(kafkaProducer, 'board', all_data)
        send_to_kafka(kafkaProducer, 'comments', all_comment)
        # 前十名统计
        boardAverageTop(all_data, redisDB)
        # 情感分析
        Sentiment(all_comment, redisDB)
        # 点赞等趋势
        likesAnalyze(all_data, redisDB)
        # LDA主题分析
        LDA(all_comment, redisDB)
        print("================end==================")
        time.sleep(10)