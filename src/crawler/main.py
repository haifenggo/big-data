import pymongo
import redis
from configparser import ConfigParser
from crawler import *
from analyze import *

def connectMongo():
    # 读取配置文件
    config = ConfigParser()
    config.read('config.ini')
    # 从配置文件中获取MongoDB连接参数
    host = config.get('mongodb', 'host')
    port = config.getint('mongodb', 'port')
    # username = config.get('mongodb', 'username')
    # password = config.get('mongodb', 'password')
    database = config.get('mongodb', 'database')
    # 构建MongoDB URI
    # uri = f"mongodb://{username}:{password}@{host}:{port}/{database}"
    uri = f"mongodb://{host}:{port}/{database}"
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
    # 连接Redis数据库
    redisClient = redis.StrictRedis(host=redisHost, port=redisPort, decode_responses=True)
    return redisClient

def testInsert():
    # 插入一条数据
    mydict = { "name": "John", "address": "Highway 37" }
    x = mycol.insert_one(mydict)
    # 打印插入的数据的ID
    print(x.inserted_id)
    # 查询所有数据
    for x in mycol.find():
        print(x)
    # 查询特定条件的数据
    myquery = { "address": "Highway 37" }
    mydoc = mycol.find(myquery)
    for x in mydoc:
        print(x)

def Sentiment(all_comment, redisCli):
    # 情感分析
    classified_texts = classify(all_comment)
    classified_emotions = SentimentAnalysis(classified_texts)
    for key, value in sorted(classified_emotions.items()):
        redisCli.set(key, json.dumps(value))

def likesAnalyze(all_data, redisCli):
    rankStats = rankCount(all_data)
    # 放到redis的哈希表
    for rank, stats in rankStats.items():
        r.hmset(f'rank:{rank}', stats)
    

if __name__ == '__main__':
    mongoDB = connectMongo()
    redisDB = connectRedis()
    while True:
        all_data, all_comment = [], []
        Get_data(all_data = all_data, all_comment = all_comment)
        Save_data(all_data = all_data, all_comment = all_comment, mongoDB = mongoDB)
        time.sleep(2)
        getOthers(mongoDB, all_comment)

        Sentiment(all_comment, redisDB)
        likesAnalyze(all_data, redisDB)
        time.sleep(10)