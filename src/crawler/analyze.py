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
from sklearn.datasets import load_boston
import pandas as pd
import re
import datetime
from collections import defaultdict

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

if __name__ == '__main__':
    filename = './comment_comprehensive.csv'
    texts, timestamps = readfile(filename)
    classified_texts = classify(texts, timestamps)
    classified_emotions = SentimentAnalysis(classified_texts)
    for hour, emotions in classified_emotions.items():
        print(f"hour {hour}:")
        print(emotions)


