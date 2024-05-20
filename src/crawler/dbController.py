import pymongo
import redis
from configparser import ConfigParser

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