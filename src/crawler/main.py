import pymongo
import redis
from configparser import ConfigParser
from crawler import *
from analyze import *
from dbController import *
from kafkaController import *
from confluent_kafka import Producer, Consumer, KafkaException

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
    redisDB = connectRedis()
    kafkaProducer = connectKafka()
    while True:
        all_data, all_comment = [], []
        Get_data(all_data = all_data, all_comment = all_comment)
        time.sleep(2)
        getOthers(all_data = all_data, all_comment = all_comment)
        send_to_kafka(kafkaProducer, 'board', all_data)
        send_to_kafka(kafkaProducer, 'comments', all_comment)
        Sentiment(all_comment, redisDB)
        likesAnalyze(all_data, redisDB)
        time.sleep(10)
