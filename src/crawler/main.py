import pymongo
import redis
import sys
from configparser import ConfigParser
from crawler import *
from analyze import *
from dbController import *
from kafkaController import *
from confluent_kafka import Producer, Consumer, KafkaException


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
    # print(classified_emotions)
    redisCli.set('sentiment_trend', json.dumps(classified_emotions))


def likesAnalyze(all_data, redisCli):
    rankStats = rankCount(all_data)
    # print(rankStats)
    redisCli.set('likes_trend', json.dumps(rankStats))


if __name__ == '__main__':
    redisDB = connectRedis()
    kafkaProducer = connectKafka()
    # print(kafkaProducer)
    print(redisDB)
    print("=" * 12 + "Crawler Start" + "=" * 12)
    while True:
        all_data, all_comment = [], []
        Get_data(all_data=all_data, all_comment=all_comment)
        time.sleep(2)
        # getOthers(all_data = all_data, all_comment = all_comment)
        print("=====================================")
        print("================send=================")
        print("=====================================")
        send_to_kafka(kafkaProducer, 'board', all_data[:10])
        send_to_kafka(kafkaProducer, 'comments', all_comment[:10])
        Sentiment(all_comment, redisDB)
        likesAnalyze(all_data, redisDB)
        print("================end==================")
        time.sleep(10)
