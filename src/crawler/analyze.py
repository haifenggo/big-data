#导入所需要的库
import requests
import json
import csv
import pandas as pd
import jieba
from pylab import *
from wordcloud import WordCloud
import matplotlib.pyplot as plt
import numpy as np
import matplotlib
import seaborn as sns
import time
from sklearn import datasets
from sklearn.linear_model import LinearRegression
import pandas as pd
import re
import datetime
from collections import defaultdict
import pymongo

# emotionAnalysis.py
'''依赖模块
pip install snownlp, pyecharts
'''
from snownlp import SnowNLP
from pyecharts import options as opts
from pyecharts.charts import Pie


def readfile(filename):
    texts = []
    timestamps = []

    with open(filename, encoding='utf-8') as f:
        reader = csv.reader(f)
        for row in reader:
            texts.append(row[0])
            timestamps.append(eval(row[2]))
    return texts, timestamps

def classify(comments):
    classified_texts = dict()
    texts, timestamps = [], []
    for value in comments:
        texts.append(value["content"])
        timestamps.append(value["commentRealTime"])
    # 遍历每个文字和时间戳
    for text, timestamp in zip(texts, timestamps):
        # 使用 datetime.fromtimestamp() 方法将时间戳转换为 datetime 对象
        dt = datetime.datetime.fromtimestamp(int(timestamp))
        # print(dt)
        # 取得时间戳所属的小时
        hour = dt.hour
        # 如果该小时不在分类字典中，则创建一个新的列表存放该小时的文字
        if hour not in classified_texts:
            classified_texts[hour] = []
        # 将文字添加到对应小时的列表中
        classified_texts[hour].append(text)

    # 打印分类后的结果
    # for hour, texts_in_hour in classified_texts.items():
    #     print(f"Hour {hour}:")
    #     for text in texts_in_hour:
    #         print(text)
    #     print()
    return classified_texts

def SentimentAnalysis(classified_texts):
    classified_emotions = {hour: {'positive': 0, 'negative': 0, 'neutral': 0} for hour in classified_texts.keys()}
    for hour, texts_in_hour in classified_texts.items():
        for item in texts_in_hour:
            # print(item)
            if SnowNLP(item).sentiments > 0.6:
                classified_emotions[hour]['positive'] += 1
            elif SnowNLP(item).sentiments < 0.4:
                classified_emotions[hour]['negative'] += 1
            else:
                classified_emotions[hour]['neutral'] += 1
    return classified_emotions
    # c = (
    #     Pie()
    #     .add("", list(emotions.items()))
    #     .set_colors(["blue", "purple", "orange"])
    #     .set_series_opts(label_opts=opts.LabelOpts(formatter="{b}: {c} ({d}%)"))
    #     .render("emotionAnalysis.html")
    # )

def rankCount(all_data):
    rankStats = defaultdict(lambda: {"coins": 0, "likes": 0, "favorites": 0})
    for item in all_data:
        rank = item["rank"]
        coins = item["coins"]
        likes = item["likes"]
        favorite = item["favorites"]
        rankStats[rank]["coins"] += coins
        rankStats[rank]["likes"] += likes
        rankStats[rank]["favorites"] += favorite
    return rankStats

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

def calcBoardTop10OwnerViews(all_data):
    db = connect()
    col = db['board']
    cur = col.find()
    d = []
    for line in cur:
        d.append(line)
    df = pd.DataFrame(d)
    # 按UP主和分区计算平均播放量
    avg_views_by_owner_category = df.groupby(['ownerName', 'board'])['views'].mean().unstack().fillna(0)
    # 获取各分区前10名UP主
    top10_owners_by_category = avg_views_by_owner_category.apply(lambda x: x.sort_values(ascending=False).head(10))
    # 转换为JSON格式
    top10_owners_json = {}
    for category in top10_owners_by_category.columns:
        top10_owners_json[category] = top10_owners_by_category[category].sort_values(ascending=False).head(10).to_dict()
    print(top10_owners_json)
    return top10_owners_json

def calcBoardTop10Views(all_data):
    df = pd.DataFrame(all_data)
    # 分区计算平均播放量
    avg_views_by_category = df.groupby('board')['views'].mean().reset_index()

    # 获取每个分区前十名平均播放量
    avg_views_by_category_dict = {}
    for idx, row in avg_views_by_category.iterrows():
        avg_views_by_category_dict[row['board']] = row['views']

    # 转换为 JSON 格式
    json_data = json.dumps(avg_views_by_category_dict, ensure_ascii=False, indent=4)
    return json_data

def calcDanmakuBoard(all_data):
    df = pd.DataFrame(all_data)
    # 分区计算平均播放量
    avg_views_by_category = df.groupby('board')['comments'].mean().reset_index()

    # 获取每个分区前十名平均播放量
    avg_views_by_category_dict = {}
    for idx, row in avg_views_by_category.iterrows():
        avg_views_by_category_dict[row['board']] = row['comments']

    # 转换为 JSON 格式
    json_data = json.dumps(avg_views_by_category_dict, ensure_ascii=False, indent=4)
    return json_data

def totalStatsByBoard(data):
    total_stats_by_category = data.groupby('board')[['views', 'likes', 'comments', 'coins', 'shares', 'favorites']].sum().to_dict()
    return total_stats_by_category

