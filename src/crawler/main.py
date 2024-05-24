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


def testInsert():
    # 插入一条数据
    mydict = {"name": "John", "address": "Highway 37"}
    x = mycol.insert_one(mydict)
    # 打印插入的数据的ID
    print(x.inserted_id)
    # 查询所有数据
    for x in mycol.find():
        print(x)
    # 查询特定条件的数据
    myquery = {"address": "Highway 37"}
    mydoc = mycol.find(myquery)
    for x in mydoc:
        print(x)

def Sentiment(all_comment, redisCli):
    # 情感分析
    classified_texts = classify(all_comment)
    classified_emotions = SentimentAnalysis(classified_texts)
    print(classified_emotions)
    redisCli.set('sentiment_trend', json.dumps(classified_emotions))

def likesAnalyze(all_data, redisCli):
    rankStats = rankCount(all_data)
    print(rankStats)
    redisCli.set('likes_trend', json.dumps(rankStats))

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
        board = comment_list[i]["borad"]
        content = comment_list[i]["content"]
        board_comments[board].append(content)
    board_word_list = defaultdict(list)
    board_words = defaultdict(list)
    all_words = []
    for key, value in board_comments.items():
        li = get_word_list(value)
        for w in li:
            all_words.extend(w)
        board_word_list[key] = li
        board_words[key] = get_words(li)

    for key, value in board_word_list.items():
        topics[key] = lda_topic_modeling(value)
    
    redisCli.set('lda_topics', json.dumps(topics))

    counterRet = collections.Counter(all_words) #对分词做词频统计
    print(counterRet)
    wordCloud(counterRet)


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
    # redisDB = connectRedis()
    # kafkaProducer = connectKafka()
    print("\033[1;33m" + "=" *12 + "Crawler Start" + "=" * 12 + "\033[0m")
    while True:
        all_data, all_comment = [], []
        Get_data(all_data = all_data, all_comment = all_comment)
        # time.sleep(2)
        getOthers(all_data = all_data, all_comment = all_comment)
        print("\033[1m" + "=" * 37)
        print("\033[1;33m" + "=" * 16 + "send" + "=" * 16 + "\033[0m")
        print("\033[1m" + "=" * 37)
        send_to_kafka(kafkaProducer, 'board', all_data[:10])
        send_to_kafka(kafkaProducer, 'comments', all_comment[:10])
        words = trans(all_comment)
        # 情感分析
        Sentiment(words, redisDB)
        # 点赞等趋势
        likesAnalyze(all_data, redisDB)
        # LDA主题分析
        LDA(all_comment, redisDB)
        time.sleep(10)