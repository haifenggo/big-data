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

from kafkaController import *

proxy = {'http':'122.225.30.179:20235'}
cookie = {'domain': '/',
        'expires': 'false',
        'httpOnly': 'false',
        'name': 'buvid3',
        'path': 'Fri, 29 Jan 2024 08:50:10 GMT',
        'value': '7A29BBDE-VA94D-4F66-QC63-D9CB8568D84331045infoc,bilibili.com'}

#爬虫部分
#1.数据是动态加载的，所以需要寻找数据地址
def Get_data(all_data, all_comment):
    global proxy,cookie
    header={
        'user-agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    url='https://api.bilibili.com/x/web-interface/popular'
    for i in range(1,12):
    # for i in range(1,2):
        param={
            'ps': 20,
            'pn': i
        }
        while True:
            res=requests.get(url=url,headers=header,proxies=proxy,cookies=cookie,params=param)
            #print(res.text)
            flag = Parse(res.text, all_data, all_comment)
            res.close()
            if flag is True:
                break
            

#2.对爬取到的数据进行解析，获取每一条视频数据的标题，Up主，播放量，评论数

#对所有视频的数据都解析，每一条数据以元组的形式保存在列表中
def Parse(text, all_data, all_comment):
    dict=json.loads(text)#把数据转化为字典方便数据提取
   # print(dict['data']['list'])
    cid_list = []
    # print("working in comprehensive")
    if dict['code'] != 0:
        time.sleep(1)
        return False
    rank = 0
    for i in dict['data']['list']:
        temp={"title": i['title'], "ownerName": i['owner']['name'], "views": i['stat']['view'], "comments": i['stat']['danmaku'],
            "favorites": i['stat']['favorite'], "likes": i['stat']['like'], "coins": i['stat']['coin'],
            "shares": i['stat']['share'], "pubLocation": i.get('pub_location', ''),
            "uploadTime": i['pubdate'], "duration": i['duration'], "rcmdReason": i['rcmd_reason']['content'],
            "bvid": i['bvid'], "rank": rank, "board": "comprehensive"}
        rank = rank + 1
        cid_list.append(i['cid'])
        all_data.append(temp)
    comment_list, comment_video_time_list, comment_real_time_list = iterCrawlComment(cid_list)
    for i in range(len(comment_list)):
        all_comment.append({"content": comment_list[i], "videoTime": comment_video_time_list[i],
                                "commentRealTime": comment_real_time_list[i], "board": "comprehensive"})
    return True

def getOthers(all_data, all_comment):
    header={
        'user-agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    urls = ['https://api.bilibili.com/x/web-interface/ranking/v2?rid=168&type=all', 
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=1&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=3&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=129&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=4&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=36&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=188&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=234&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=155&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=5&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=181&type=all',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=0&type=origin',
        'https://api.bilibili.com/x/web-interface/ranking/v2?rid=0&type=rookie']
    filenames = ['national_original', 'anime', 'music', 'dancing', 'games', 'knowledge',
                'technology', 'sports', 'fashion', 'fun', 'movies', 'origin', 'rookie']

    # filenames = ['national_original']
    global proxy, cookie
    for j in range(len(filenames)):
        print("working in ", filenames[j])
        while True:
            res=requests.get(url=urls[j],headers=header,proxies=proxy, cookies=cookie)
            dict=json.loads(res.text)#把数据转化为字典方便数据提取
            if dict['code'] == 0:
                break
            else:
                time.sleep(1)
        cid_list = []
        rank = 0
        for i in dict['data']['list']:
            # print(i)
            temp={"title": i['title'], "ownerName": i['owner']['name'], "views": i['stat']['view'], "comments": i['stat']['danmaku'],
                "favorites": i['stat']['favorite'], "likes": i['stat']['like'], "coins": i['stat']['coin'],
                "shares": i['stat']['share'], "pubLocation": i.get('pub_location', ''),
                "uploadTime": i['pubdate'], "duation": i['duration'], "bvid": i['bvid'], "rank": rank, "board": filenames[j]}
            rank += 1
            cid_list.append(i['cid'])
            all_data.append(temp)
        comment_list, comment_video_time_list, comment_real_time_list = iterCrawlComment(cid_list)
        comments_dicts = []
        for i in range(len(comment_list)):
            comments_dicts.append({"content": comment_list[i], "videotTime": comment_video_time_list[i],
                                    "commentRealTime": comment_real_time_list[i], "board": filenames[j]})
        all_comment.extend(comments_dicts)
        res.close()
        # time.sleep(1)

def iterCrawlComment(data):
    content_list, video_time_list, real_time_list = [], [], [] 
    for item in data:
        print('cid: ', item)
        c, v, r = crawlComment(item, proxy)
        content_list += c
        video_time_list += v
        real_time_list += r
        # time.sleep(0.5)
        break
    return content_list, video_time_list, real_time_list

def crawlComment(cid, proxy):
    url = f'http://comment.bilibili.com/{cid}.xml'
    print(url)
    headers = {
        'referer': 'https://www.bilibili.com/video/BV19h411s7oq?spm_id_from=333.934.0.0',
        'User-Agent': 'https://www.bilibili.com/video/BV19h411s7oq?spm_id_from=333.934.0.0',
        'cookie': "_uuid=19DF1EDB-20B7-FF74-A700-9DF415B2429530977infoc; buvid3=AAD6C6C7-FB31-40E7-92EC-7A6A7ED3920C148814infoc; sid=jzp2723t; fingerprint=2e74a5bc11a3adec2616987dde475370; buvid_fp=AAD6C6C7-FB31-40E7-92EC-7A6A7ED3920C148814infoc; buvid_fp_plain=AAD6C6C7-FB31-40E7-92EC-7A6A7ED3920C148814infoc; DedeUserID=434541726; DedeUserID__ckMd5=448fda6ab5098e5e; SESSDATA=1fe46ad7%2C1651971297%2Ceb583*b1; bili_jct=5bcd45718996ac402a29c7f23110984d; blackside_state=1; rpdid=|(u)YJlJmmu|0J'uYJYRummJm; bp_t_offset_434541726=590903773845625600; bp_video_offset_434541726=590903773845625600; CURRENT_BLACKGAP=0; LIVE_BUVID=AUTO5716377130871212; video_page_version=v_old_home; PVID=1; CURRENT_FNVAL=976; i-wanna-go-back=1; b_ut=6; b_lsid=4F7CFC82_17D78864851; bsource=search_baidu; innersign=1"
    }
    # proxies = {'http': 'http://' + proxy, 'https': 'https://' + proxy}
    requests.adapters.DEFAULT_RETRIES = 5
    session = requests.session()
    session.keep_alive = False
    content_list = []
    video_time_list, real_time_list = [], []
    try:
        resp = session.get(url, headers = headers, timeout = (2,2))
        # 调用.encoding属性获取requests模块的编码方式
        # 调用.apparent_encoding属性获取网页编码方式
        # 将网页编码方式赋值给response.encoding
        resp.encoding = resp.apparent_encoding
        # print(resp)
        content_list = re.findall('<d p=".*?">(.*?)</d>', resp.text)
        # print(content_list)
        video_time_list = re.findall('<d p="(.*?),.*?,.*?,.*?,.*?,.*?,.*?,.*?,.*?">.*?</d>', resp.text)
        real_time_list = re.findall('<d p=".*?,.*?,.*?,.*?,(.*?),.*?,.*?,.*?,.*?">.*?</d>', resp.text)
    except Exception:
        print('crawlComment Exception')
    return content_list, video_time_list, real_time_list









































    
def cloud():
    #绘制词云,查看出现最多的词语
    text = '  '
    a=''
    df=pd.read_csv('./all_data.csv')
    for line in df['视频名称']:
        a += line
        text+=a

    # 使用jieba模块将字符串分割为单词列表
    cut_text = '    '.join(jieba.cut(text))
    #设置背景图
    # color_mask = imread('./python截图图片/书本.jpg')

    cloud = WordCloud(

        background_color = 'white',
        # 对中文操作必须指明字体
        # font_path='C:/Windows/Fonts/优设标题黑_猫啃网.ttf',
        # mask = color_mask,
        max_words = 50,
        max_font_size = 200
        ).generate(cut_text)

    # 保存词云图片
    cloud.to_file('./ciwordcloud.jpg')
    plt.imshow(cloud)
    plt.axis('off')
    plt.show()

def commentChart():
    #评论数的频率直方图

    # %matplotlib inline
    plt.rcParams['font.sans-serif'] = ['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    df=pd.read_csv('./all_data.csv')
    plt.figure

    sns.distplot(df['评论数'].values,hist=True, kde=True,axlabel='tip',
                rug=False,fit=None,hist_kws = {'color':'b','label':'评论数'},
                kde_kws={'color':'b'})

    plt.legend()
    plt.xlabel('评论数')
    plt.ylabel('频率')
    plt.grid()

def show():
    #播放量的频率直方图

    sns.distplot(df['播放量'].values,hist=True, kde=True,axlabel='tip',rug=False,
                fit=None,hist_kws = {'color':'b','label':'播放量'},
                kde_kws={'color':'b'})

    plt.legend()

    plt.ylabel('')

    plt.grid()

def relationship():
    #播放量与评论数关系的散点图

    x=df['播放量'].values

    y=df['评论数'].values

    plt.figure

    size=120

    plt.xlabel('播放量(百万)')

    plt.ylabel('评论数')
    plt.scatter(x/1,y,size,color='g',label='播放评论关系')

    plt.legend(loc=2)
    plt.title("播放量与评论数关系的散点图",fontsize=16)
    plt.grid()

    plt.show()

def top35():
    #TOP35的UP主与播放量的柱形图

    data=np.array(df['播放量'][0:35])
    index=np.array(df['up主'][0:35])
    print(data)
    print(index)
    plt.ylabel('播放量（百万）')

    plt.title("TOP35的UP主与播放量的柱形图",fontsize=16)
    s = pd.Series(data, index)

    s.plot(kind='bar')
    plt.savefig('TOP35的UP主与播放量的柱形图.jpg')



    #TOP35的UP主与评论数的柱形图
    data=np.array(df['评论数'][0:35])
    index=np.array(df['up主'][0:35])
    print(data)
    print(index)
    plt.ylabel('评论数')

    s = pd.Series(data, index)

    plt.title("TOP35的UP主与评论数的柱形图",fontsize=16)

    s.plot(kind='bar')

    #数据持久化
    plt.savefig('TOP35UP主与评论数的柱形图.jpg')


def analyze():
    #数据分析，回归方程


    #计算回归方程系数和回归方程截距

    predict_model = LinearRegression()
    predict_model.fit(x.reshape(-1, 1),y)
    np.set_printoptions(precision = 3, suppress = True)
    print("回归方程系数{}".format(predict_model.coef_))
    print("回归方程截距{0:2f}".format(predict_model.intercept_))


    #播放评论关系回归方程图

    plt.scatter(x,y,size,color='g',label='播放评论关系')
    yfit = [809.206980 + 0.002 * xi for xi in x]
    plt.plot(x, yfit, color="r")

    plt.ylabel('评论数')

    plt.xlabel('播放量（百万）')

    plt.title("播放评论关系回归方程图",fontsize=16)

    plt.show()

    plt.savefig('播放评论关系回归方程图.jpg')


    

    



    



